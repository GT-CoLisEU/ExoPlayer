package br.rnp.futebol.verona.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.rnp.futebol.verona.pojo.TScript;

/**
 * [UFRGS] VERONA
 * Created by camargo on 25/10/2016
 * Data Access Object (DAO) class to manage the experiments saved in
 * the device internal memory
 */

public class TScriptDAO extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "NTScriptDAO";
    private static final int VERSION = 1;
    private static final String COLUMNS[] = {"video", "extension", "address", "json"};


    public TScriptDAO(Context ctx) {
        super(ctx, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int cont = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ".concat(TABLE_NAME).concat(" ("));
        sb.append(COLUMNS[cont++].concat(" TEXT NOT NULL, "));
        sb.append(COLUMNS[cont++].concat(" TEXT NOT NULL, "));
        sb.append(COLUMNS[cont++].concat(" TEXT NOT NULL, "));
        sb.append(COLUMNS[cont].concat(" TEXT NOT NULL, "));
        sb.append("PRIMARY KEY (".concat(COLUMNS[0].concat(",").concat(COLUMNS[1]
                .concat(",").concat(COLUMNS[2].concat("));")))));
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sb = "DROP TABLE IF EXISTS ".concat(TABLE_NAME.concat(";"));
        db.execSQL(sb);
        onCreate(db);
    }

    /**
     * Check if an experiment with a passed filename is already saved in the device internal DB
     * @return true if exists
     */
//    private boolean checkFile(String filename) {
//        Cursor rawQuery = null;
//        try {
//            rawQuery = getReadableDatabase().rawQuery(
//                    "SELECT 1 from ".concat(TABLE_NAME.concat(" WHERE ".concat(COLUMNS[1]).concat(" = ?"))),
//                    new String[]{filename});
//            int rc = rawQuery.getCount();
//            return rc > 0;
//        } catch (IllegalArgumentException ie) {
//            return false;
//        } finally {
//            if (rawQuery != null)
//                rawQuery.close();
//        }
//    }

    /**
     * Insert a new experiment in the DB
     *
     * @param script script to be inserted
     */
    public boolean insert(TScript script) {
        try {
            int cont = 0;
            ContentValues values = new ContentValues();
            values.put(COLUMNS[cont++], script.getVideo());
            values.put(COLUMNS[cont++], script.getExtension());
            values.put(COLUMNS[cont++], script.getAddress());
            values.put(COLUMNS[cont], script.toSimpleJson().toString());
            getWritableDatabase().insert(TABLE_NAME, null, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete an experiment in the DB
     */
    public void delete(Integer id) {
//        if (checkFile(exp.getFileName())) {
        getWritableDatabase().delete(TABLE_NAME, COLUMNS[0].concat(" = ?"), new String[]{id.toString()});
//        }
    }

    /**
     * Get all the saved experiments
     *
     * @return the saved experiments
     */
    public ArrayList<TScript> getScripts() {
        try {
            ArrayList<TScript> exps = new ArrayList<>();
            Cursor c = getWritableDatabase().query(TABLE_NAME, COLUMNS, null, null, null, null, null);
            while (c.moveToNext()) {
                int cont = 0;
                TScript exp = new TScript();
                exp.setVideo(c.getString(cont++));
                exp.setExtension(c.getString(cont++));
                exp.setAddress(c.getString(cont++));
                exp.fromSimplesJson(new JSONObject(c.getString(cont)));
                exps.add(exp);
            }
            c.close();
            return exps;
        } catch (JSONException e) {
            return null;
        }
    }

    public ArrayList<String> getScriptsName() {
        try {
            ArrayList<String> exps = new ArrayList<>();
            Cursor c = getWritableDatabase().query(TABLE_NAME, COLUMNS, null, null, null, null, null);
            while (c.moveToNext()) {
                exps.add(c.getString(2));
            }
            c.close();
            return exps;
        } catch (Exception e) {
            return null;
        }
    }

    public int getScriptsCount() {
        int count = 0;
        Cursor c = getWritableDatabase().query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        while (c.moveToNext())
            count++;
        c.close();
        return count;
    }

}
