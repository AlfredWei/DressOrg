package org.dress.mydress.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by j-weishiyi on 2017/6/12.
 */

public class Wardrobe implements Parcelable{
    public ArrayList<Cloth> clothes;

    private final String JSON_Clothes = "clothes";

    public boolean has_clothes(){
        return clothes.size() != 0;
    }

    public Wardrobe()
    {
        clothes = new ArrayList<Cloth>();
    }
    //Parcel Overrides
    public Wardrobe(Parcel in)
    {
        in.readTypedList(clothes, Cloth.CREATOR);
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(clothes);
    }

    public static final Parcelable.Creator<Wardrobe> CREATOR =
            new Parcelable.Creator<Wardrobe>() {
                public Wardrobe createFromParcel(Parcel in) {
                    return new Wardrobe(in);
                }

                public Wardrobe[] newArray(int size) {
                    return new Wardrobe[size];
                }
            };

    public JSONObject toJson() throws JSONException
    {
        JSONObject jContent = new JSONObject();
        JSONArray  jClothes = new JSONArray();

        for(Cloth c: clothes)
            jClothes.put(c.toJson());

        jContent.put(JSON_Clothes, jClothes);
        return jContent;
    }

    public void fromJson(JSONObject jContent) throws  JSONException
    {
        JSONArray jClothes = jContent.getJSONArray(JSON_Clothes);
        clothes.clear();

        for ( int i = 0; i < jClothes.length() ; ++i)
        {
            JSONObject jCloth = jClothes.getJSONObject(i);
            Cloth cloth = new Cloth();
            if ( cloth.fromJson(jCloth) )
                clothes.add(cloth);
        }

    }

    public Boolean addCloth(Cloth cloth)
    {
        if(cloth.getLocalID().isEmpty())
        {
            cloth.setsLocalID(String.valueOf(clothes.size()));
        }

        clothes.add(cloth);
        return true;
    }
}
