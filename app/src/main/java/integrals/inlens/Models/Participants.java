package integrals.inlens.Models;

public class Participants {
    private String Profile_picture;
    private String Name;
    private String Photographer_UID;
    private String Email_ID;
    private String PhoneNumber;

    public Participants(String profile_picture,
                        String name, String photographer_UID,
                        String email_ID, String phoneNumber) {
        Profile_picture = profile_picture;
        Name = name;
        Photographer_UID = photographer_UID;
        Email_ID = email_ID;
        PhoneNumber = phoneNumber;
    }

    public String getProfile_picture() {
        return Profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        Profile_picture = profile_picture;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhotographer_UID() {
        return Photographer_UID;
    }

    public void setPhotographer_UID(String photographer_UID) {
        Photographer_UID = photographer_UID;
    }

    public String getEmail_ID() {
        return Email_ID;
    }

    public void setEmail_ID(String email_ID) {
        Email_ID = email_ID;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public Participants() {
    }
}