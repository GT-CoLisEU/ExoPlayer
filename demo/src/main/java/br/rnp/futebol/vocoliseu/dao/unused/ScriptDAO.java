package br.rnp.futebol.vocoliseu.dao.unused;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.rnp.futebol.vocoliseu.pojo.unused.Script;

import java.util.ArrayList;
import java.util.List;

/**
 * [UFRGS] VO-CoLisEU
 * Created by camargo on 25/10/2016
 * Data Access Object (DAO) class to manage the experiments saved in
 *  the device internal memory
 */

public class ScriptDAO extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "ScriptDAO";
    private static final int VERSION = 1;
    private static final String[] COLUMNS = {"name", "filename", "address"};


    public ScriptDAO(Context ctx) {
        super(ctx, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int cont = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ".concat(TABLE_NAME).concat(" ("));
        sb.append(COLUMNS[cont++].concat(" TEXT PRIMARY KEY, "));
        sb.append(COLUMNS[cont++].concat(" TEXT, "));
        sb.append(COLUMNS[cont].concat(" TEXT);"));
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
    private boolean checkFile(String filename) {
        Cursor rawQuery = null;
        try {
            rawQuery = getReadableDatabase().rawQuery(
                    "SELECT 1 from ".concat(TABLE_NAME.concat(" WHERE ".concat(COLUMNS[1]).concat(" = ?"))),
                    new String[]{filename});
            int rc = rawQuery.getCount();
            return rc > 0;
        } catch (IllegalArgumentException ie) {
            return false;
        } finally {
            if (rawQuery != null)
                rawQuery.close();
        }
    }

    /**
     * Insert a new experiment in the DB
     * @param exp experiment to be inserted
     */
    public void insert(Script exp) {
        int cont = 0;
        if (!checkFile(exp.getFileName())) {
            ContentValues values = new ContentValues();
            values.put(COLUMNS[cont++], exp.getName());
            values.put(COLUMNS[cont++], exp.getFileName());
            values.put(COLUMNS[cont], exp.getAddress());
            getWritableDatabase().insert(TABLE_NAME, null, values);
        }
    }

    /**
     * Delete an experiment in the DB
     * @param exp experiment to be deleted
     */
    public void delete(Script exp) {
        if(checkFile(exp.getFileName())) {
            getWritableDatabase().delete(TABLE_NAME, COLUMNS[1].concat(" = ?"), new String[]{exp.getFileName()});
        }
    }

    /**
     * Get all the saved experiments
     * @return the saved experiments
     */
    public List<Script> getExperiments() {
        List<Script> exps = new ArrayList<>();
        Cursor c = getWritableDatabase().query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        while (c.moveToNext()) {
            int cont = 0;
            Script exp = new Script();
            exp.setName(c.getString(cont++));
            exp.setFileName(c.getString(cont++));
            exp.setAddress(c.getString(cont));
            exps.add(exp);
        }
        c.close();
        return exps;
    }


}
