package integrals.inlens.Helper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Athul Krishna on 18/01/2018.
 */

public class RecentImageDatabase extends SQLiteOpenHelper {
    private Context context;



    public RecentImageDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "RecentDatabase.db", factory, version);
        this.context=context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE RECENTTABLE( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IMAGEURI TEXT,WEATHERDETAILS TEXT,LOCATIONDETAILS TEXT," +
                "TIMETAKEN TEXT,UPLOADSTATUS TEXT,TEXTCAPTION TEXT," +
                "UPLOADERID TEXT,USERNAME TEXT,PROFILEPICURI TEXT,AUDIOCAPTION TEXT);");
    }


    public void InsertUploadValues(String ImageUri, String WeatherDetails,
                                   String LocationDetails,
                                   String AudioCaptionUri, String TimeTaken,
                                   String UploadStatus, String TextCaption,
                                   String UploaderID, String Username, String ProfilePicUri) {
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

        this.getWritableDatabase().insertOrThrow("RECENTTABLE", "", contentValues);


    }

    public void UpdateUploadStatus(int InLensePostID, String NewUploadStatus) {
        this.getWritableDatabase().execSQL("UPDATE RECENTTABLE" + " SET UPLOADSTATUS='" + NewUploadStatus + "' WHERE ID='" + InLensePostID + "'");
    }

    public void DeleteInLensePost(int InLensePostID) {
        this.getWritableDatabase().delete("RECENTTABLE", "ID='" + InLensePostID + "'", null);
    }
    public int GetNumberOfRows() {
        int S=1;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT COUNT(*)FROM RECENTTABLE",null);
        while (cursor.moveToNext()) {
            S= cursor.getInt(0);
        }
        return S;
    }



    public String GetPhotoUri(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(1);
        }
        return S;
    }

    public String GetWeatherDetails(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(2);
        }
        return S;
    }
    public String GetLocationDetails(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(3);
        }
        return S;
    }


    public String GetTimeTaken(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(4);
        }
        return S;

    }

    public String GetUploadStatus(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(5);
        }
        return S;

    }

    public String GetTextCaption(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(6);
        }
        return S;

    }

    public String GetUploaderID(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(7);
        }
        return S;

    }
    public String GetUsername(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(8);
        }
        return S;

    }

    public String GetProfilePicUri(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(9);
        }
        return S;

    }
    public String GetAudioCaption(int ID) {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM RECENTTABLE WHERE ID =" + ID, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(10);
        }
        return S;

    }
    public void DeleteDatabase(){
        context.deleteDatabase("RecentDatabase.db");
        }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS RECENTTABLE"  +");");
        onCreate(db);
    }
}
