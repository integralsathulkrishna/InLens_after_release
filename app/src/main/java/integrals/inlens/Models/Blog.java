package integrals.inlens.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Athul Krishna on 7/14/2017.
 */

public class Blog implements Parcelable {
    private String AudioCaption;
    private String Image;
    private String ImageThumb;
    private String BlogDescription;
    private String BlogTitle;
    private String Location;
    private String TimeTaken;
    private String UserName;
    private String User_ID;
    private String WeatherDetails;
    private String PostedByProfilePic;
    private String OriginalImageName;

    public Blog() {
    }

    public Blog(String audioCaption, String image, String imageThumb,
                String blogDescription, String blogTitle, String location,
                String timeTaken, String userName, String user_ID,
                String weatherDetails,
                String postedByProfilePic,
                String originalImageName) {
        AudioCaption = audioCaption;
        Image = image;
        ImageThumb = imageThumb;
        BlogDescription = blogDescription;
        BlogTitle = blogTitle;
        Location = location;
        TimeTaken = timeTaken;
        UserName = userName;
        User_ID = user_ID;
        WeatherDetails = weatherDetails;
        PostedByProfilePic = postedByProfilePic;
        OriginalImageName = originalImageName;
    }

    protected Blog(Parcel in) {
        AudioCaption = in.readString();
        Image = in.readString();
        ImageThumb = in.readString();
        BlogDescription = in.readString();
        BlogTitle = in.readString();
        Location = in.readString();
        TimeTaken = in.readString();
        UserName = in.readString();
        User_ID = in.readString();
        WeatherDetails = in.readString();
        PostedByProfilePic = in.readString();
        OriginalImageName = in.readString();
    }

    public static final Parcelable.Creator<Blog> CREATOR = new Parcelable.Creator<Blog>() {

        @Override
        public Blog createFromParcel(Parcel parcel) {
            return new Blog(parcel);
        }

        @Override
        public Blog[] newArray(int i) {
            return new Blog[i];
        }
    };




    public String getAudioCaption() {
        return AudioCaption;
    }

    public void setAudioCaption(String audioCaption) {
        AudioCaption = audioCaption;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getImageThumb() {
        return ImageThumb;
    }

    public void setImageThumb(String imageThumb) {
        ImageThumb = imageThumb;
    }

    public String getBlogDescription() {
        return BlogDescription;
    }

    public void setBlogDescription(String blogDescription) {
        BlogDescription = blogDescription;
    }

    public String getBlogTitle() {
        return BlogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        BlogTitle = blogTitle;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTimeTaken() {
        return TimeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        TimeTaken = timeTaken;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getWeatherDetails() {
        return WeatherDetails;
    }

    public void setWeatherDetails(String weatherDetails) {
        WeatherDetails = weatherDetails;
    }

    public String getPostedByProfilePic() {
        return PostedByProfilePic;
    }

    public void setPostedByProfilePic(String postedByProfilePic) {
        PostedByProfilePic = postedByProfilePic;
    }

    public String getOriginalImageName() {
        return OriginalImageName;
    }

    public void setOriginalImageName(String originalImageName) {
        OriginalImageName = originalImageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
               parcel.writeString( AudioCaption);
               parcel.writeString(Image);
               parcel.writeString(ImageThumb);
               parcel.writeString( BlogDescription);
               parcel.writeString(BlogTitle);
               parcel.writeString(Location);
               parcel.writeString(TimeTaken);
               parcel.writeString(UserName);
               parcel.writeString(User_ID);
               parcel.writeString(WeatherDetails);
               parcel.writeString(PostedByProfilePic);
               parcel.writeString(OriginalImageName);
      }

}