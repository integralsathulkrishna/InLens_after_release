package integrals.inlens.GridView;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageModel implements Parcelable {

    String name, url,timeTaken;

    public ImageModel() {

    }

    protected ImageModel(Parcel in) {
        name = in.readString();
        url = in.readString();
        timeTaken=in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };


    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimeTaken() { return timeTaken; }

    public void setTimeTaken(String timeTaken) { this.timeTaken = timeTaken; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }
}
