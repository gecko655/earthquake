package jp.gecko655.earthquake.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "jishin.db";
    private static final int DB_VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseOpenHelper(Context context, String name,
            CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public DatabaseOpenHelper(Context context, String name,
            CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            StringBuilder createSql = new StringBuilder();
            createSql.append("create table " + StatusDatum.TABLE_NAME + " (");
            createSql.append(StatusDatum.COLUMN_ID
                    + " integer primary key autoincrement not null,");
            createSql.append(StatusDatum.COLUMN_KIND + " text not null,");
            createSql.append(StatusDatum.COLUMN_CONTENT + " text not null");
            createSql.append(")");
            db.execSQL(createSql.toString());
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        Cursor c = db.query(StatusDatum.TABLE_NAME, null, null, null, null,
                null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StatusDatum.TABLE_NAME);
        onCreate(db);
    }

}
