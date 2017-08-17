package org.dress.mydress.model;

/**
 * Created by j-weishiyi on 2017/6/12.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import org.dress.mydress.model.ClothType;
import org.json.JSONException;
import org.json.JSONObject;

public class Cloth implements Parcelable{
    private ClothType cType;
    private String sName;
    private String sClothID;
    private String sTags;
    private String sLocalPath;
    private String sLocalID;

    private final String JSON_CLOTH_TYPE = "cloth_type";
    private final String JSON_CLOTH_NAME = "cloth_name";
    private final String JSON_CLOTH_ID = "cloth_uuid";
    private final String JSON_CLOTH_TAGS = "tags";
    private final String JSON_CLOTH_LOCAL_PATH = "path";
    private final String JSON_CLOTH_LOCAL_ID = "id";

    public Cloth(){
        cType = ClothType.UNDEF;
        sName = new String();
        sClothID = new String();
        sTags = new String();
        sLocalPath = new String();
        sLocalID = new String();
    }

    public Cloth(JSONObject jContent) throws JSONException
    {
        fromJson(jContent);
    }

    public JSONObject toJson () throws JSONException
    {
        JSONObject jContent = new JSONObject();
        jContent.put(JSON_CLOTH_TYPE, this.cType.toString());
        jContent.put(JSON_CLOTH_NAME, this.sName);
        jContent.put(JSON_CLOTH_ID, this.sClothID);
        jContent.put(JSON_CLOTH_LOCAL_ID, this.sLocalID);
        jContent.put(JSON_CLOTH_LOCAL_PATH, this.sLocalPath);
        jContent.put(JSON_CLOTH_TAGS, this.sTags);

        return jContent;
    }

    public boolean fromJson(JSONObject jContent) throws JSONException
    {
        this.cType = ClothType.valueOf(jContent.getString(JSON_CLOTH_TYPE));
        this.sName = jContent.getString(JSON_CLOTH_NAME);
        this.sLocalID = jContent.getString(JSON_CLOTH_LOCAL_ID);
        this.sLocalPath = jContent.getString(JSON_CLOTH_LOCAL_PATH);
        this.sTags = jContent.getString(JSON_CLOTH_TAGS);
        this.sClothID = jContent.getString(JSON_CLOTH_ID);
        return ! sClothID.isEmpty() && ! sLocalPath.isEmpty();
    }

    //Parcel Overrides
    public Cloth(Parcel in)
    {
        String[] data = new String[6];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.cType = ClothType.valueOf(data[0]);
        this.sName = data[1];
        this.sClothID = data[2];
        this.sTags = data[3];
        this.sLocalPath = data[4];
        this.sLocalID = data[5];
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.cType.name(),
                this.sName,
                this.sClothID,
                this.sTags,
                this.sLocalPath,
                this.sLocalID
        });
    }

    public void setsName(String new_name )
    {
        sName = new_name;
    }

    public void setsTags(String new_tags )
    {
        sTags = new_tags;
    }

    public void setcType(ClothType new_type )
    {
        cType = new_type;
    }

    public String GetClothName()
    {
        return  sName;
    }

    public String GetClothTags()
    {
        return  sTags;
    }

    public ClothType GetClothType()
    {
        return  cType;
    }

    public static final Parcelable.Creator<Cloth> CREATOR =
            new Parcelable.Creator<Cloth>() {
                public Cloth createFromParcel(Parcel in) {
                    return new Cloth(in);
                }

                public Cloth[] newArray(int size) {
                    return new Cloth[size];
                }
            };
}
