package br.rnp.futebol.verona.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QOSStatisticHelper  {

    private Float value = 0f;
    private String provider;
    private float packetLoss;
    private int count = 0;
    private ArrayList<Float> auxiliar;

    public QOSStatisticHelper(Float value, String provider) {
        this.value = value;
        this.provider = provider;
    }

    public QOSStatisticHelper() {}

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Float getLoss() {
        return packetLoss;
    }

    public void setLoss(Float value) {
        this.packetLoss = value;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void increaseValues(Float value) {
        this.value += value;
    }

    public void increaseLoss(Float value) {
        this.packetLoss += value;
    }

    public int getCounter() {
        return count;
    }

    public void addAux(Float f) {
        if (auxiliar == null)
            auxiliar = new ArrayList<>();
        auxiliar.add(f);
    }

    public ArrayList<Float> getAuxiliar() {
        return auxiliar;
    }

    public void increaseCounter() {
        this.count++;
    }

    public JSONObject toJSON() {
        try {
            JSONObject json = new JSONObject();
            json.put("value", value);
            json.put("provider", provider);
            json.put("packetLoss", packetLoss);
            json.put("count", count);
            JSONArray array = null;
            if (auxiliar != null && auxiliar.size() > 0) {
                array = new JSONArray();
                for (Float f : auxiliar) {
                    JSONObject obj = new JSONObject();
                    obj.put("f", f);
                    array.put(obj);
                }
            }
            if (array != null)
                json.put("auxiliar", array);
            return json;
        } catch (JSONException e) {
            Log.i("error", e.getMessage());
        }
        return null;
    }

    public QOSStatisticHelper fromJSON(JSONObject json) {
        try {
            if (json.has("value"))
                this.value = (float) json.getDouble("value");
            if (json.has("provider"))
                this.provider = json.getString("provider");
            if (json.has("packetLoss"))
                this.packetLoss = (float) json.getDouble("packetLoss");
            if (json.has("count"))
                this.count = json.getInt("count");
            if (json.has("auxiliar")) {
                this.auxiliar = new ArrayList<>();
                JSONArray array = json.getJSONArray("auxiliar");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    this.auxiliar.add((float) obj.getDouble("f"));
                }
            }
            return this;
        } catch (JSONException e) {
            Log.i("error", e.getMessage());
        }
        return null;
    }

}

