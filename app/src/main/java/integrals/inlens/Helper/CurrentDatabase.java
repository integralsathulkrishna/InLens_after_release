package integrals.inlens.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/*
*                                           Current
  *   ____________________________________________________________________________________
*    |     ID   | LiveCommunityID | Uploading_Total | UploadingTargetColumn | RecentTotal |
*    |__________|_________________|_________________|_______________________|_____________|
*
* */

public class CurrentDatabase extends SQLiteOpenHelper {
    private Context context;
    public CurrentDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "CurrentDatabase.db", factory, version);
        this.context=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CURRENT( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "LIVECOMMUNITY TEXT,UPLOADING_TOTAL INTEGER,UPLOADING_TARGET_COLUMN INTEGER," +
                "RECENT_TOTAL INTEGER);");


    }

    public void InsertUploadValues(String LiveCommunityID,int UploadingTotal,int UploadingTargetColumn,int RecentTotal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("LIVECOMMUNITY", LiveCommunityID);
        contentValues.put("UPLOADING_TOTAL",UploadingTotal);
        contentValues.put("UPLOADING_TARGET_COLUMN",UploadingTargetColumn);
        contentValues.put("RECENT_TOTAL",RecentTotal);
        this.getWritableDatabase().insertOrThrow("CURRENT", "", contentValues);

        }

    public String GetLiveCommunityID() {
        String S=null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM CURRENT WHERE ID =" + 1, null);
        while (cursor.moveToNext()) {
            S= cursor.getString(1);
        }
        return S;
    }

    public int GetUploadingTotal() {
        int S=0;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM CURRENT WHERE ID =" + 1, null);
        while (cursor.moveToNext()) {
            S= cursor.getInt(2);
        }
        return S;
    }

    public int GetUploadingTargetColumn() {
        int S=0;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM CURRENT WHERE ID =" + 1, null);
        while (cursor.moveToNext()) {
            S= cursor.getInt(3);
        }
        return S;
    }
    public int GetRecentTotal() {
        int S=0;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM CURRENT WHERE ID =" + 1, null);
        while (cursor.moveToNext()) {
            S= cursor.getInt(4);
        }
        return S;
    }

    public void DeleteDatabase(){
        context.deleteDatabase("CurrentDatabase.db");
        }

    public void ResetUploadTotal(int FinalValue){
            this.getWritableDatabase().execSQL("UPDATE CURRENT" + " SET UPLOADING_TOTAL='" + FinalValue + "' WHERE ID='" + 1 + "'");

            }



    public void ResetUploadTargetColumn(int FinalValue){
        this.getWritableDatabase().execSQL("UPDATE CURRENT" + " SET UPLOADING_TARGET_COLUMN='" + FinalValue + "' WHERE ID='" + 1 + "'");

    }
    public void ResetResentTotal(int FinalValue){
        this.getWritableDatabase().execSQL("UPDATE CURRENT" + " SET RECENT_TOTAL='" + FinalValue + "' WHERE ID='" + 1 + "'");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
