package br.rnp.futebol.vocoliseu.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by camargo on 10/11/16.
 */
public class TExperimentDAO extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "TExperimentDAO";
    private static final int VERSION = 1;
    private static final String[] COLUMNS = {"filename", "json"};


    public TExperimentDAO(Context ctx) {
        super(ctx, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int cont = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ".concat(TABLE_NAME).concat(" ("));
        sb.append(COLUMNS[cont++].concat(" TEXT PRIMARY KEY, "));
        sb.append(COLUMNS[cont].concat(" TEXT);"));
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sb = "DROP TABLE IF EXISTS ".concat(TABLE_NAME.concat(";"));
        db.execSQL(sb);
        onCreate(db);
    }

    public int getExpsCount() {
        int count = 0;
        Cursor c = getWritableDatabase().query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        while (c.moveToNext())
            count++;
        c.close();
        return count;
    }


}
