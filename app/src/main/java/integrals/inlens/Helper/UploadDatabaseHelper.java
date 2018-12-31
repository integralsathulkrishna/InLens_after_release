package integrals.inlens.Helper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Athul Krishna on 03/01/2018.
 */

public  class UploadDatabaseHelper extends SQLiteOpenHelper {
    private Context contextX;

    public UploadDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,"UploadDatabase.db", factory, version);

        this.contextX = context;

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    ///


    //This community ID is temporary and is needed to be changed......
    // This database should be created as soon as the community is created....
    // Community ID is Default set as 13431110..should be changed

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TABLE2( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IMAGEURI TEXT,WEATHERDETAILS TEXT,LOCATIONDETAILS TEXT," +
                "TIMETAKEN TEXT,UPLOADSTATUS TEXT,TEXTCAPTION TEXT," +
                "UPLOADERID TEXT,USERNAME TEXT,PROFILEPICURI TEXT,AUDIOCAPTION TEXT,CURRENTTIMEMILLISECOND TEXT);");
    }


    public void InsertUploadValues(String ImageUri, String WeatherDetails,
                                   String LocationDetails,
                                   String AudioCaptionUri, String TimeTaken,
                                   String UploadStatus, String TextCaption,
                                   String UploaderID, String Username, String ProfilePicUri,String CurrentTTimeMillisecond) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("IMAGEURI", ImageUri);
        contentValues.put("WEATHERDETAILS", WeatherDetails);
        contentValues.put("LOCATIONDETAILS", LocationDetails);
        contentValues.put("TIMETAKEN", TimeTaken);
        contentValues.put("UPLOADSTATUS", UploadStatus);
        contentValues.put("TEXTCAPTION", TextCaption);
        contentValues.put("UPLOADERID", UploaderID);
        contentValues.put("USERNAME", Username);
        contentValues.put("PROFILEPICURI", ProfilePicUri);
        contentValues.put("AUDIOCAPTION", AudioCaptionUri);
        contentValues.put("CURRENTTIMEMILLISECOND",CurrentTTimeMillisecond);
        this.getWritableDatabase().insertOrThrow("TABLE2", "", contentValues);

    }

    public void UpdateUploadStatus(int InLensePostID, String NewUploadStatus) {
        if(InLensePostID==0){
            InLensePostID=1;
        }
        this.getWritableDatabase().execSQL("UPDATE TABLE2" + " SET UPLOADSTATUS='" + NewUploadStatus + "' WHERE ID='" + InLensePostID + "'");
    }
    public void UpdateID(int InLensePostID,int NewInLensePostID) {
        this.getWritableDatabase().execSQL("UPDATE TABLE2" + " SET ID=" + NewInLensePostID + " WHERE ID='" + InLensePostID + "'");
    }
    public void DeleteInLensePost(String InLensePostMilliSec) {
        this.getWritableDatabase().delete("TABLE2", "CURRENTTIMEMILLISECOND ='" + InLensePostMilliSec + "'", null);
    }



    public String GetPhotoUri(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(1);
        }
        return S;
    }
    public int GetNumberOfRows() {
        int S=1;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT COUNT(*)FROM TABLE2",null);
        while (cursor.moveToNext()) {
            S= cursor.getInt(0);
        }
        return S;
    }

    public String GetWeatherDetails(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(2);
        }
        return S;
    }

    public String GetLocationDetails(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(3);
        }
        return S;
    }


    public String GetTimeTaken(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(4);
        }
        return S;

    }

    public String GetUploadStatus(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(5);
        }
        return S;

    }

    public String GetTextCaption(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(6);
        }
        return S;

    }


    public void DeleteRow(int RowID){
        this.getWritableDatabase().rawQuery("DELETE FROM TABLE2 WHERE ID = "+ RowID,null);
    }


    public String GetUploaderID(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(7);
        }
        return S;

    }
    public String GetUsername(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(8);
        }
        return S;

    }

    public String GetProfilePicUri(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(9);
        }
        return S;

    }
    public String GetAudioCaption(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(10);
        }
        return S;

    }

    public String GetCurrentTimeMilliSecond(int ID){
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TABLE2 WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(11);
        }
        return S;

    }

    public void DeleteDatabase(){
    contextX.deleteDatabase("UploadDatabase.db");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TABLE2"+ ");");
        onCreate(db);
    }

}
