package integrals.inlens.Models;

/**
 * Created by Athul Krishna on 7/14/2017.
 */

public class Blog {
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
}