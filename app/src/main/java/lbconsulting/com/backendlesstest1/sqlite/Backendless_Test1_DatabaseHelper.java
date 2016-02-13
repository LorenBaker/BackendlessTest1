package lbconsulting.com.backendlesstest1.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lbconsulting.com.backendlesstest1.classes.MyLog;


public class Backendless_Test1_DatabaseHelper extends SQLiteOpenHelper {

    private static Context mContext;

    private static final String DATABASE_NAME = "BackendlessTest1.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase dBase;

    public Backendless_Test1_DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Backendless_Test1_DatabaseHelper.dBase = database;
        MyLog.i("Backendless_Test1_DatabaseHelper", "onCreate");
        ListThemeSqlTable.onCreate(database, mContext);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i("Backendless_Test1_DatabaseHelper", "onUpgrade");
        ListItemsSqlTable.onUpgrade(database, oldVersion, newVersion, mContext);

    }

    public static SQLiteDatabase getDatabase() {
        return dBase;
    }

}
