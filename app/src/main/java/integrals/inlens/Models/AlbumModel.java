package integrals.inlens.Models;

/**
 * Created by Athul Krishna on 08/02/2018.
 */

public class AlbumModel {
    private String AlbumCoverImage;
    private String AlbumDescription;
    private String AlbumTitle;
    private String PostedByProfilePic;
    private String Time;
    private String UserName;
    private String User_ID;

    public AlbumModel(String albumCoverImage, String albumDescription,
                      String albumTitle,
                      String postedByProfilePic,
                      String time, String userName,
                      String user_ID) {
        AlbumCoverImage = albumCoverImage;
        AlbumDescription = albumDescription;
        AlbumTitle = albumTitle;
        PostedByProfilePic = postedByProfilePic;
        Time = time;
        UserName = userName;
        User_ID = user_ID;
    }

    public AlbumModel() {
    }

    public String getAlbumCoverImage() {
        return AlbumCoverImage;
    }

    public void setAlbumCoverImage(String albumCoverImage) {
        AlbumCoverImage = albumCoverImage;
    }

    public String getAlbumDescription() {
        return AlbumDescription;
    }

    public void setAlbumDescription(String albumDescription) {
        AlbumDescription = albumDescription;
    }

    public String getAlbumTitle() {
        return AlbumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        AlbumTitle = albumTitle;
    }

    public String getPostedByProfilePic() {
        return PostedByProfilePic;
    }

    public void setPostedByProfilePic(String postedByProfilePic) {
        PostedByProfilePic = postedByProfilePic;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
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
}
