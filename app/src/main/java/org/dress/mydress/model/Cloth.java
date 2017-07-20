package org.dress.mydress.model;

/**
 * Created by j-weishiyi on 2017/6/12.
 */
import android.os.Parcel;
import android.os.Parcelable;
import org.dress.mydress.model.ClothType;

public class Cloth implements Parcelable{
    private ClothType cType;
    private String sName;
    private String sClothID;
    private String sTags;
    private String sLocalPath;
    private String sLocalID;

    public Cloth(){
        cType = ClothType.UNDEF;
        sName = new String();
        sClothID = new String();
        sTags = new String();
        sLocalPath = new String();
        sLocalID = new String();
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
