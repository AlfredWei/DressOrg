package org.dress.mydress.view;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import org.dress.mydress.R;
import org.dress.mydress.model.Cloth;

public class ActivityEdit extends AppCompatActivity {

    private static final String TAG = "overview";
    String mPhotoPath = null;
    ImageView Photo_view;
    EditText m_text_name, m_text_tags;
    Spinner m_cloth_type_spinner;
    Cloth edited_cloth_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();

    }

    private void init()
    {
        Intent pre_intent = this.getIntent();
        mPhotoPath = pre_intent.getStringExtra( "photo_path" );
        edited_cloth_data = pre_intent.getParcelableExtra("cloth_data");
        Photo_view = (ImageView) findViewById( R.id.edit_imgview );
        m_text_name = (EditText) findViewById( R.id.text_cloth_name);
        m_text_tags  = (EditText) findViewById( R.id.text_colth_tag );
        m_cloth_type_spinner = (Spinner) findViewById( R.id.cloth_type_spinner) ;
        ArrayAdapter<CharSequence> clothtype_list = ArrayAdapter.createFromResource(ActivityEdit.this,
                R.array.cloth_tpye, android.R.layout.simple_spinner_item);
        clothtype_list.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        m_cloth_type_spinner.setAdapter(clothtype_list);

        SetImageView(mPhotoPath);
    }

    private void SetClothData()
    {
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
        Log.v(TAG,m_text_name.getText().toString());
        Log.v(TAG,m_text_tags.getText().toString());
        Log.v(TAG,m_cloth_type_spinner.getSelectedItem().toString());
    }

    private void ReturnPreEditView()
    {
        finish();
    }

}
