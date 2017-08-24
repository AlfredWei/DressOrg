package org.dress.mydress.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import org.dress.mydress.R;
import org.dress.mydress.data.ModelSerializer;
import org.dress.mydress.model.Home;
import org.dress.mydress.model.Cloth;
import org.dress.mydress.model.ClothType;

public class ActivityEdit extends AppCompatActivity {

    private static final String TAG = "EditView";

    ArrayAdapter<ClothType> m_clothtype_list;
    String mPhotoPath = null;
    ImageView Photo_view;
    EditText m_text_name, m_text_tags;
    Spinner m_cloth_type_spinner;
    Cloth m_edited_cloth_data;
    Home m_user_data;
    ModelSerializer m_ModelSerializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        DisplayClothDataInText();
        SetImageView(mPhotoPath);
    }

    private void init()
    {
        m_user_data = Home.get();
        m_ModelSerializer = new ModelSerializer(this);
        Log.v("Cloths_num :",  String.valueOf(m_user_data.getWardrobe().clothes.size() ) );
        Intent pre_intent = this.getIntent();
        mPhotoPath = pre_intent.getStringExtra( "photo_path" );
        m_edited_cloth_data = pre_intent.getParcelableExtra("cloth_data");
        Photo_view = (ImageView) findViewById( R.id.edit_imgview );
        m_text_name = (EditText) findViewById( R.id.text_cloth_name);
        m_text_tags  = (EditText) findViewById( R.id.text_colth_tag );
        m_cloth_type_spinner = (Spinner) findViewById( R.id.cloth_type_spinner) ;

        m_clothtype_list = new ArrayAdapter<ClothType>( ActivityEdit.this, android.R.layout.simple_spinner_item,
                                                        ClothType.values() );
        m_clothtype_list.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        m_cloth_type_spinner.setAdapter( m_clothtype_list );
        ClothType cloth_type = m_edited_cloth_data.GetClothType();
        int type_position = m_clothtype_list.getPosition( cloth_type );
        m_cloth_type_spinner.setSelection( type_position );
    }

    private void DisplayClothDataInText()
    {
        String cloth_name = m_edited_cloth_data.GetClothName();
        String cloth_tag = m_edited_cloth_data.GetClothTags();

        if( !cloth_name.isEmpty() )
            m_text_name.setText(cloth_name);
        if( !cloth_tag.isEmpty() )
            m_text_tags.setText(cloth_tag);
    }


    private void SetImageView(String photo_path)
    {
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        Photo_view.getLayoutParams().height = display.heightPixels *2/5;
        Photo_view.getLayoutParams().width = display.widthPixels;
        DrawableRequestBuilder<String> thumbnailRequest = Glide.with( this ).load(photo_path);

        Glide.with( this )
                .load( photo_path )
                .placeholder(R.drawable.ic_notifications_black_24dp)
                .thumbnail( thumbnailRequest )
                .into( Photo_view );
    }

    public void EditButtonClick(View button)
    {
        int button_id = button.getId();
        switch (button_id)
        {
            case R.id.ok_button:
                SaveClothData();
                break;
            case R.id.cancel_button:
                ReturnPreEditView();
                break;

        }
    }

    private void SaveClothData()
    {
        int type_position = m_cloth_type_spinner.getSelectedItemPosition();
        m_edited_cloth_data.setsName( m_text_name.getText().toString() );
        m_edited_cloth_data.setsTags( m_text_tags.getText().toString() );
        m_edited_cloth_data.setcType( m_clothtype_list.getItem(type_position) );
        Intent preedit_view_intent = new Intent(ActivityEdit.this, ActivityPreEdit.class);
        preedit_view_intent.putExtra("cloth_data", m_edited_cloth_data );
        m_user_data.getWardrobe().addCloth( m_edited_cloth_data );

        if (!m_user_data.user_info.user_id.isEmpty() &&
                !m_user_data.wardrobe_info.clothes.isEmpty()) {
            try {
                m_ModelSerializer.SaveUserWardrobe(
                        m_user_data.user_info.user_id, m_user_data.wardrobe_info);
            } catch (Exception e) {
                Log.v(TAG, "Got exception while save Wardrobe");
                Log.v(TAG, e.toString());
            }
        }
        setResult(RESULT_OK, preedit_view_intent);
        finish();
    }

    private void ReturnPreEditView()
    {
        finish();
    }

}
