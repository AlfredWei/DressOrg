package org.dress.mydress.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by j-weishiyi on 2017/6/12.
 */

public class Wardrobe implements Parcelable{
    public ArrayList<Cloth> clothes;

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
}
