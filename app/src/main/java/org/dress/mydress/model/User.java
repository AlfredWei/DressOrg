package org.dress.mydress.model;

import android.graphics.Picture;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by j-weishiyi on 2017/6/12.
 */

public class User implements Parcelable{

    public String name;
    public String login_token;
    public String login_account_type;
    public String page;
    public String mobile;


    public boolean isLogin(){

        return !login_token.isEmpty();
    }

    public User() {
        name = new String();
        login_token = new String();
        login_account_type = new String();
        page = new String();
        mobile = new String();
    }

    //Parcel Overrides
    public User(Parcel in)
    {
        String[] data = new String[5];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.name = data[0];
        this.login_token = data[1];
        this.login_account_type = data[2];
        this.page = data[3];
        this.mobile = data[4];
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name,
                this.login_token,
                this.login_account_type,
                this.page,
                this.mobile});
    }

    public static final Parcelable.Creator<User> CREATOR =
            new Parcelable.Creator<User>() {
                public User createFromParcel(Parcel in) {
                    return new User(in);
                }

                public User[] newArray(int size) {
                    return new User[size];
                }
            };
}
