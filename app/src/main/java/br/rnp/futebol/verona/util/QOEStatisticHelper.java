package br.rnp.futebol.verona.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QOEStatisticHelper {
    private int freezes = 0, frzDuration = 0, PST = 0;
    private ArrayList<String> iniResList = new ArrayList<>(), finalResList = new ArrayList<>();
    private ArrayList<String> iniBRList = new ArrayList<>(), finalBRList = new ArrayList<>();
    private String provider;
    private int count = 0;

    public void addIniRes(String res) {
        iniResList.add(res);
    }

    public void addFinalRes(String res) {
        finalResList.add(res);
    }

    public void addIniBR(String res) {
        iniBRList.add(res);
    }

    public void addFinalBR(String res) {
        finalBRList.add(res);
    }

    public void increaseCounter() {
        count++;
    }

    public int getFreezes() {
        return freezes;
    }

    public void increaseFreezes(int frz) {
        this.freezes += frz;
    }

    public void setFreezes(int freezes) {
        this.freezes = freezes;
    }

    public int getFrzDuration() {
        return frzDuration;
    }

    public void increaseFrzDur(int frz) {
        this.frzDuration += frz;
    }

    public void setFrzDuration(int frzDuration) {
        this.frzDuration = frzDuration;
    }

    public int getPST() {
        return PST;
    }

    public void increasePST(int pst) {
        this.PST += pst;
    }

    public void setPST(int PST) {
        this.PST = PST;
    }

    public String getIniRes() {
        return getMostPopular(iniResList);
    }

    public String getFinalRes() {
        return getMostPopular(finalResList);
    }

    public String getIniBR() {
        return getMostPopular(iniBRList);
    }

    public String getFinalBR() {
        return getMostPopular(finalBRList);
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    private String getMostPopular(ArrayList<String> list) {
        ArrayList<Integer> numbers = new ArrayList<>();
        ArrayList<String> aux = new ArrayList<>();
        int maior;

        for (String s : list) {
            if (!aux.contains(s)) {
                Log.i("Str", s);
                aux.add(s);
                numbers.add(1);
            } else {
                int i = aux.indexOf(s);
                int o = numbers.get(i);
                numbers.set(i, o + 1);
            }
        }
        for (int i : numbers) {
            Log.i("Str", i + "");
        }

        if (numbers.size() > 0) {
            maior = numbers.get(0);
            for (int i = 1; i < numbers.size(); i++)
                if (i > maior) maior = i;
            return aux.get(numbers.indexOf(maior));
        } else {
            return "None";
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "frzs: " + freezes + " | dur: " + frzDuration + " | count: " + count;
    }
    /*
        private int freezes = 0, frzDuration = 0, PST = 0;
        private ArrayList<String> iniResList = new ArrayList<>(), finalResList = new ArrayList<>();
        private ArrayList<String> iniBRList = new ArrayList<>(), finalBRList = new ArrayList<>();
        private String provider;
        private int count = 0;
     */

    public JSONObject toJSON() {
        try {
            JSONObject json = new JSONObject();
            json.put("freezes", freezes);
            json.put("frzDuration", frzDuration);
            json.put("pst", PST);
            json.put("provider", provider);
            json.put("count", count);
            addJArray(json, toJSONArray(iniBRList), "iniBR");
            addJArray(json, toJSONArray(finalBRList), "finalBR");
            addJArray(json, toJSONArray(iniResList), "iniRes");
            addJArray(json, toJSONArray(finalResList), "finalRes");
            return json;
        } catch (JSONException e) {
            Log.i("error", e.getMessage());
            return null;
        }
    }

    public QOEStatisticHelper fromJSON(JSONObject json) {
        try {
            this.freezes = json.getInt("freezes");
            this.frzDuration = json.getInt("frzDuration");
            this.PST = json.getInt("pst");
            this.count = json.getInt("count");
            this.provider = json.getString("provider");
            this.iniBRList = toStringArray(json, "iniBR");
            this.finalBRList = toStringArray(json, "finalBR");
            this.iniResList = toStringArray(json, "iniRes");
            this.finalResList = toStringArray(json, "finalRes");
            return this;
        } catch (JSONException e) {
            Log.i("error", e.getMessage());
            return null;
        }
    }

    public ArrayList<String> toStringArray(JSONObject json, String jArrayName) throws JSONException {
        ArrayList<String> auxiliar = null;
        if (json.has(jArrayName)) {
            auxiliar = new ArrayList<>();
            JSONArray array = json.getJSONArray(jArrayName);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                auxiliar.add(obj.getString("f"));
            }
        }
        return auxiliar;
    }

    public JSONArray toJSONArray(ArrayList<String> str) throws JSONException {
        JSONArray array = null;
        if (str != null && str.size() > 0) {
            array = new JSONArray();
            for (String f : str) {
                JSONObject obj = new JSONObject();
                obj.put("f", f);
                array.put(obj);
            }
        }
        return array;
    }


    public void addJArray(JSONObject json, JSONArray array, String name) throws JSONException {
        if (array != null)
            json.put(name, array);
    }
}

