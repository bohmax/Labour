package com.example.labour;

import android.os.Parcel;
import android.os.Parcelable;

public class Package_item implements Parcelable {

    private String title;
    private String description;
    private int photoId;
    private Package_Route route;

    public Package_item(String title, String description, int photoId) {
        this.title = title;
        this.description = description;
        this.photoId = photoId;
        route = new Package_Route();
    }

    private Package_item(Parcel in) {
        title = in.readString();
        description = in.readString();
        photoId = in.readInt();
        route = in.readParcelable(Package_Route.class.getClassLoader());
    }

    public static final Creator<Package_item> CREATOR = new Creator<Package_item>() {
        @Override
        public Package_item createFromParcel(Parcel in) {
            return new Package_item(in);
        }

        @Override
        public Package_item[] newArray(int size) {
            return new Package_item[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    int getPhotoId() {
        return photoId;
    }

    public Package_Route getRoute() {
        return route;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(photoId);
        dest.writeParcelable(route, flags);
    }
}
