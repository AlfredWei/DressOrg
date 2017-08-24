package org.dress.mydress.data;

import android.content.Context;

import org.dress.mydress.model.Wardrobe;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/7/27.
 */

public class ModelSerializer {
    private Context mContext;
    private final String FileNamePrefix = "mydress.content";

    private final String JSON_Wardrobe = "wardrobe";

    public ModelSerializer(Context c) {
        mContext = c;
    }

    public boolean SaveUserWardrobe(String sUserID, Wardrobe wardrobe) throws JSONException, IOException {
        if (sUserID.isEmpty())
            return false;

        String fileName = FileNamePrefix + sUserID + ".json";
        FileOutputStream out = null;
        try {
            out = mContext.openFileOutput(
                    fileName,
                    Context.MODE_PRIVATE);
            String s = wardrobe.toJson().toString();
            out.write(wardrobe.toJson().toString().getBytes());
            out.flush();
            out.close();
        } finally {
            if (null != out)
                out.close();
        }

        return true;
    }

    public Wardrobe LoadUserWardrobe(String sUserID) throws JSONException, IOException {
        Wardrobe wardrobe = new Wardrobe();
        String fileName = FileNamePrefix + sUserID + ".json";

        BufferedReader reader = null;
        try {
            FileInputStream in = mContext.openFileInput(fileName);

            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONObject jContent = (JSONObject) new JSONTokener(builder.toString()).nextValue();
            wardrobe.fromJson(jContent);
        } catch (FileNotFoundException e) {
            //ignore if not exists
        } finally {
            if (null != reader)
                reader.close();
        }

        return wardrobe;
    }
}
