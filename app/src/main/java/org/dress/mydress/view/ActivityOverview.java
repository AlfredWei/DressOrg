package org.dress.mydress.view;
import org.dress.mydress.R;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.AccessTokenTracker;
import com.facebook.AccessToken;
import com.facebook.ProfileTracker;
import com.facebook.Profile;
import com.google.gson.JsonIOException;

import org.dress.mydress.Utility.UtilityView.ImageAdapter;
import org.dress.mydress.model.Home;
import org.dress.mydress.data.ConstValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.dress.mydress.model.User;
import org.dress.mydress.model.UserType;
import org.dress.mydress.model.Wardrobe;
import org.dress.mydress.data.ModelSerializer;
import org.json.JSONException;

public class ActivityOverview extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int PREEDIT_REQUEST = 1111;
    private static final String TAG = "overview";
    private ImageAdapter myImageAdapter;
    private GridView gridview;
    private Toast toast = null;
    private File m_photofile = null;
    private  String photo_director = null;
    private  File[] photo_list = null;
    private BottomNavigationView mBottomView;

    private ModelSerializer mModelSerializer;

    private Home     mHomeModel;
    private CallbackManager mFBCallbackManager;
    private AccessToken mAccessToken;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    PopupHomeMenu();
                    break;
                case R.id.navigation_dashboard:
                    break;
                case R.id.navigation_account:
                {
                    if(mHomeModel.user_info.isLogin())
                        SetActivity(ActivityAccountInfo.class, ConstValue.IntentResultNone);
                    else
                        SetActivity(ActivityLogin.class, ConstValue.IntentResultLogin);
                }
                    break;
            }
            return true;
        }
    };

    private AccessTokenTracker accessTokenTracker = new AccessTokenTracker(){
        @Override
        protected void onCurrentAccessTokenChanged(
                AccessToken oldAccessToken,
                AccessToken currentAccessToken) {
            // Set the access token using
            // currentAccessToken when it's loaded or set.
            if(mHomeModel.user_info.isLogin())
            {
                //token change, maybe user changed login account
                RestoreUserInfo();
                RestoreWardrobeInfo();
                InitLayout();
            }
        }
    };

    private ProfileTracker mFBProfileTracker = new ProfileTracker() {
    @Override
    protected void onCurrentProfileChanged(
            Profile oldProfile,
            Profile currentProfile) {
        // App code
            RestoreUserInfo();
            InitLayout();
    }
};

    void SetActivity(Class<?> cls, int nMonitorID)
    {
        Intent intent = new Intent(this, cls);
        if ( 0 != nMonitorID)
            startActivityForResult(intent, nMonitorID);
        else
            startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_overview);
        mHomeModel = new Home();
        mModelSerializer = new ModelSerializer(this);
        mBottomView =(BottomNavigationView) findViewById(R.id.navigation);
        mFBCallbackManager = CallbackManager.Factory.create();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if( null != savedInstanceState)
        {
            mHomeModel.user_info = savedInstanceState.getParcelable(ConstValue.KeyUser);
            mHomeModel.wardrobe_info = savedInstanceState.getParcelable(ConstValue.KeyWardrobe);
        }
        
		InitPhotList();
		
        RestoreUserInfo();

        RestoreWardrobeInfo();

        InitLayout();
    }
    
	private void InitPhotList()
    {
        photo_director = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        photo_list = new File(photo_director).listFiles();
        gridview = (GridView) findViewById(R.id.gallery_gridimg);
        myImageAdapter = new ImageAdapter( this, this, photo_list);
        gridview.setAdapter(myImageAdapter);
        CheckAlbumDir();
    }
	
    protected void RestoreUserInfo()
    {
        //check google account or fb token to make sure acount is login or not
        // If the access token is available already assign it.
        AccessToken token = AccessToken.getCurrentAccessToken();
        if( null != token )
        {
            mHomeModel.user_info.login_token = token.getToken();
            mHomeModel.user_info.user_id = token.getUserId();
            mHomeModel.user_info.login_account_type = UserType.UserTypeFacebook;
            Profile user_profile = Profile.getCurrentProfile();
            mHomeModel.user_info.name = user_profile.getName();
            mHomeModel.user_info.page = user_profile.getLinkUri().toString();
        }
        else
        {
            mHomeModel.user_info = new User();
            mHomeModel.wardrobe_info = new Wardrobe();
        }
    }

    protected void RestoreWardrobeInfo()
    {
        //base on user account get all user data
        String sUserID = mHomeModel.user_info.user_id;
        if(!sUserID.isEmpty()) {
            Wardrobe wardrobe = null;
            try {
                wardrobe = mModelSerializer.LoadUserWardrobe(sUserID);
            }
            catch (Exception e){
                Log.v(TAG, "Cannot Load wardrobe info");
                Log.v(TAG, e.toString());
            }
            if ( null != wardrobe && !wardrobe.clothes.isEmpty() )
                mHomeModel.wardrobe_info = wardrobe;
        }
    }

    protected void InitLayout()
    {
        //base on Home model init layout
        if (mHomeModel.user_info.isLogin())
        {
            //mTextMessage.setText(mHomeModel.user_info.name);
        }

    }

    @Override
    protected  void onSaveInstanceState(Bundle savedInstanceState)
    {
        if( null != savedInstanceState ) {
            savedInstanceState.putParcelable(ConstValue.KeyUser, mHomeModel.user_info);
            savedInstanceState.putParcelable(ConstValue.KeyWardrobe, mHomeModel.wardrobe_info);
        }
    }

	    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.imgthumb_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imgthumb_select_photo:
                DoPreeditSelectPhoto();
                return true;
            case R.id.imgthumb_deleete_photo:
                DoDeleteSelectedPhoto();
                return true;
            case R.id.imgthumb_cancel_photo:
                myImageAdapter.DoCancelSelectBox();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DoPreeditSelectPhoto()
    {
        if( !HasReadAndWriteExteranlStoragePermission() ) {
            RequestReadAndWritePermission();
        }
        else {
            int selectedphoto_num = myImageAdapter.getSelectedphotoNum();
            if (selectedphoto_num == 0) {
                MakeTextAndShow(ActivityOverview.this, getString(R.string.not_select_photo), Toast.LENGTH_SHORT);
            } else if (selectedphoto_num == 1) {
                ArrayList<String> selected_photo = myImageAdapter.GetSelectPhotoPath();
                Intent preedit_photo_intent = new Intent();
                preedit_photo_intent.setClass(ActivityOverview.this, ActivityPreEdit.class);
                preedit_photo_intent.putExtra("photo_path", selected_photo.get(0));
                startActivityForResult( preedit_photo_intent,PREEDIT_REQUEST);
            } else {
                MakeTextAndShow(ActivityOverview.this, getString(R.string.many_select_photo), Toast.LENGTH_SHORT);
            }
        }
    }

    private void DoDeleteSelectedPhoto()
    {
        myImageAdapter.DoDeleteSelectedPhoto();
        photo_list = new File(photo_director).listFiles();

        if( photo_list.length == 0)
            Refresh();
    }

    private void PopupHomeMenu(){
        PopupMenu popup = new PopupMenu(ActivityOverview.this, mBottomView);
        popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_take_picture: {
                        if(HasReadAndWriteExteranlStoragePermission()) {
                            dispatchTakePictureIntent();
                        }
                        else {
                            RequestReadAndWritePermission();
                        }
                        return true;
                    }
                    case R.id.home_edit_photo: {
                        DoPreeditSelectPhoto();
                        return true;
                    }
                }
                return false;
            }
        });
        popup.show();
    }

    private  void MakeTextAndShow(final Context context, final String text, final int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }

    private boolean HasReadAndWriteExteranlStoragePermission()
    {
        int read_exteranl_storage_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_exteranl_storage_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean is_read_exteranl_srotage = (read_exteranl_storage_permission == PackageManager.PERMISSION_GRANTED);
        boolean is_write_exteranl_srotage = (write_exteranl_storage_permission == PackageManager.PERMISSION_GRANTED);
        return (is_read_exteranl_srotage && is_write_exteranl_srotage);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity( getPackageManager() ) != null) {
            m_photofile = null;
            try {
                m_photofile = createImageFile();
            } catch (IOException ex) {
                Log.v(TAG, ":createImageFile error");
            }
            // Continue only if the File was successfully created
            if (m_photofile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getString(R.string.photo_provider), m_photofile);
                takePictureIntent.putExtra("return-data", false);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_"+ timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
        return imageF;
    }

    private File getAlbumDir()
    {
        return this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private void CheckAlbumDir()
    {
        File storage_dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!storage_dir.exists())
            storage_dir.mkdir();
    }

    private void RequestReadAndWritePermission()
    {
        int REQUEST_READWRITE_STORAGE = 1;
        final int version = Build.VERSION.SDK_INT;
        String external_readwrite_permission[] =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (version >= 23) {
            ActivityCompat.requestPermissions(this, external_readwrite_permission, REQUEST_READWRITE_STORAGE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
			case CAMERA_REQUEST:
			{
				if(resultCode == Activity.RESULT_OK)
	            {
	                if(photo_list.length ==0) {
	                    Refresh();
	                }
	                else {
	                    photo_list = new File(photo_director).listFiles();
	                    myImageAdapter.ReStart(photo_list);
	                }

	            }else if(resultCode == Activity.RESULT_CANCELED)
	                m_photofile.delete();
			}
			break;
			case PREEDIT_REQUEST:
			{
				Refresh();
			}
			break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        mFBProfileTracker.stopTracking();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(!mHomeModel.user_info.user_id.isEmpty() &&
                !mHomeModel.wardrobe_info.clothes.isEmpty()){
            try {
                mModelSerializer.SaveUserWardrobe(
                        mHomeModel.user_info.user_id, mHomeModel.wardrobe_info);
            }
            catch (Exception e){
                Log.v(TAG, "Got exception while save Wardrobe");
                Log.v(TAG, e.toString());
            }
        }

    }

    private void Refresh()
    {
        if (android.os.Build.VERSION.SDK_INT >= 11){
            recreate();
        }else{
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }
}
