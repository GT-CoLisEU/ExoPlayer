package br.rnp.futebol.vocoliseu.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import br.rnp.futebol.vocoliseu.pojo.TExperiment;

/**
 * [UFRGS] VO-CoLisEU
 * Created by camargo on 25/10/2016
 * Data Access Object (DAO) class to manage the experiments saved in
 * the device internal memory
 */

public class TExpForListDAO extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "TExpForList";
    private static final int VERSION = 1;
    private static final String[] COLUMNS = {"filename"};


    public TExpForListDAO(Context ctx) {
        super(ctx, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int cont = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ".concat(TABLE_NAME).concat(" ("));
        sb.append(COLUMNS[cont].concat(" TEXT PRIMARY KEY NOT NULL);"));
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
     * @param exp exp to be inserted
     */
    public void insert(TExperiment exp) {
        int cont = 0;
        ContentValues values = new ContentValues();
        values.put(COLUMNS[cont], exp.getFilename());
        getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    /**
     * Delete an experiment in the DB
     */
    public void delete(String filename) {
//        if (checkFile(exp.getFileName())) {
        getWritableDatabase().delete(TABLE_NAME, COLUMNS[0].concat(" = ?"), new String[]{filename});
//        }
    }

    /**
     * Get all the saved experiments
     *
     * @return the saved experiments
     */
    public ArrayList<String> getExpsNames() {
        try {
            ArrayList<String> exps = new ArrayList<>();
            Cursor c = getWritableDatabase().query(TABLE_NAME, COLUMNS, null, null, null, null, null);
            while (c.moveToNext()) {
                int cont = 0;
                exps.add(c.getString(cont));
            }
            c.close();
            return exps;
        } catch (Exception e) {
            Log.i("ERRO", e.getMessage());
            return null;
        }
    }

    public ArrayList<TExperiment> getExpsByNames(ArrayList<String> names) {
        ArrayList<TExperiment> exps = new ArrayList<>();
        try {
            for (String s : names) {
                try {
                    exps.add(new TExperiment().fromJson(new JSONObject(read(s))));
                } catch (Exception e) {
                    Log.i("TExpForListDAO", "Error while reading file");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return exps;
    }

    private String read(String file) {
        try {
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(file.concat(".txt"));
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            String text = "", line;
            while ((line = reader.readLine()) != null) {
                text += line.concat(" ");
            }
            return text;
        } catch (IOException e) {
            return "";
        }
    }
}
