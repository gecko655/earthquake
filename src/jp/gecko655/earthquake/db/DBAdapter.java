package jp.gecko655.earthquake.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.VoicemailContract.Status;

public class DBAdapter {

    private Context context;
    private DatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    private String[][] datas = new String[][]{
            {"TW","おなかすいた"},
            {"RT","475968127730057216"}
    };

    public DBAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseOpenHelper(this.context);
    }
    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        saveNote(datas[0][0],datas[0][1]);
        return this;
    }
     
     
     public void close(){
       dbHelper.close();
     }
     

     //
     // App Methods
     //
     
     
     public boolean deleteAllNotes(){
         return db.delete(StatusDatum.TABLE_NAME, null, null) > 0;
     }
     
     public boolean deleteNote(int id){
         return db.delete(StatusDatum.TABLE_NAME, Status._ID + "=" + id, null) > 0;
     }
     
     public Cursor getAllNotes(){
         Cursor c = null;
         c = db.query(StatusDatum.TABLE_NAME, null, null, null, null, null, null);
         return c;
     }
     
     public void saveNote(String kind, String content){
         ContentValues values = new ContentValues();
         values.put(StatusDatum.COLUMN_KIND, kind);
         values.put(StatusDatum.COLUMN_CONTENT, content);
         db.insertOrThrow(StatusDatum.TABLE_NAME, null, values);
     }

}
