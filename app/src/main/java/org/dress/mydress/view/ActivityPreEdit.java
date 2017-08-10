package org.dress.mydress.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.dress.mydress.R;
import org.dress.mydress.Utility.ImageProcess.ProcessData;
import org.dress.mydress.Utility.ImageProcess.RemoveBackground;
import org.dress.mydress.Utility.UtilityView.CropImageView;
import org.dress.mydress.model.Cloth;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Point;

/**
 * Created by Cyberlink on 2017/7/15.
 */

public class ActivityPreEdit extends AppCompatActivity {

    private static final String TAG = "PreEditView";
    CropImageView mEditImageView = null;
    Bitmap mBitmap = null;
    String mPhotoPath = null;
    private TextView mTextMessage;
    private int PICK_IMAGE_REQUEST = 1;
    private int EDIT_PHOTO_REQUEST = 2;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.photo_edit_preedit:
                    if(SettingIsCorrect()) {
                        DoRemoveBackground();
                    }
                    return true;
                case R.id.photo_edit_edit:
                    DoEditPhoto();
                    return true;
                case R.id.photo_edit_photos:
                    StartSelectPhoto();
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preedit);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.photo_menu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        init();
    }

    private void init()
    {
        Intent pre_intent = this.getIntent();
        mPhotoPath = pre_intent.getStringExtra( "photo_path" );
        mEditImageView = (CropImageView) findViewById(R.id.EditImageView);
        mTextMessage = (TextView) findViewById(R.id.camera_textmessage);
        SetImageView(mPhotoPath);
    }

    private void SetImageView(String photo_src)
    {
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        mEditImageView.getLayoutParams().height = display.heightPixels *7/10;
        mEditImageView.getLayoutParams().width = display.widthPixels ;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo_src, bmOptions);
        bmOptions.inJustDecodeBounds = false;

        mBitmap = BitmapFactory.decodeFile(photo_src, bmOptions);
        mEditImageView.setImageBitmap(mBitmap);
    }

    private boolean SettingIsCorrect()
    {
        String Setting_error = null;
        boolean has_opencv = OpenCVLoader.initDebug();
        boolean has_editing_photo = (mPhotoPath!=null);
        if(!has_opencv) {
            Setting_error = getString(R.string.not_have_opencv);
            if(!has_editing_photo)
                Setting_error = Setting_error +',' +getString(R.string.not_have_photo);
        }
        else {
            if (!has_editing_photo)
                Setting_error = getString(R.string.not_have_photo);
        }
        boolean is_correct = has_opencv && has_editing_photo;

        if(!is_correct)
            PopupWarning(Setting_error);
        return is_correct;
    }

    private void DoRemoveBackground()
    {
        if(mPhotoPath!= null) {
            CropImageView.WindowLocData current_window =  mEditImageView.GetCurrentWindow();
            Point top_left = new Point(current_window.start_x, current_window.start_y);
            Point botton_right = new Point(current_window.end_x, current_window.end_y);
            ProcessData remove_background_data = new ProcessData(mPhotoPath, top_left, botton_right);
            new RemoveBackground(ActivityPreEdit.this, mEditImageView).execute(remove_background_data);
        }

    }

    private void PopupWarning(String warning_text)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPreEdit.this);	//main是class name
        builder.setTitle("Warning");
        builder.setMessage(warning_text);
        builder.show();
    }
    private void DoEditPhoto()
    {
        Intent edit_photo_intent = new Intent(ActivityPreEdit.this, ActivityEdit.class);
        Cloth json_cloth = new Cloth();

        edit_photo_intent.putExtra("photo_path", mPhotoPath );
        edit_photo_intent.putExtra("cloth_data", json_cloth );
        startActivityForResult(edit_photo_intent, EDIT_PHOTO_REQUEST);
    }

    private void StartSelectPhoto()
    {
        //TODO: reference customer gallery(https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#file-pickers)
        Intent select_photo_intent = new Intent(ActivityPreEdit.this, ActivityOverview.class);
        finish();
        startActivity( select_photo_intent );
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE_REQUEST )
        {
            if(resultCode == Activity.RESULT_OK)
            {
                mPhotoPath = data.getStringExtra("data");

                SetImageView(mPhotoPath);
                mTextMessage.setText(getString(R.string.find_object));
            }

        }
    }

}
