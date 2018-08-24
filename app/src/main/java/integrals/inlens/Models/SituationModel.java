package integrals.inlens.Models;

public class SituationModel {

    private String Createdby;
    private String Time;
    private String Title;
    private String SituationID;
    private String SituationTime;

    public SituationModel() {
    }

    public String getCreatedby() {
        return Createdby;
    }

    public void setCreatedby(String createdby) {
        Createdby = createdby;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSituationID() {
        return SituationID;
    }

    public void setSituationID(String situationID) {
        SituationID = situationID;
    }

    public String getSituationTime() {
        return SituationTime;
    }

    public void setSituationTime(String situationTime) {
        SituationTime = situationTime;
    }

    public SituationModel(String createdby, String time,
                          String title, String situationID,
                          String situationTime) {
        Createdby = createdby;
        Time = time;
        Title = title;
        SituationID = situationID;
        SituationTime = situationTime;
    }
}
