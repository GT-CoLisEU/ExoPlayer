package br.rnp.futebol.vocoliseu.pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by camargo on 11/11/16.
 */
public class TScript implements Serializable {

    private String video;
    private String extension;
    private String address;
    private boolean useDash, isUsedAux;
    private ArrayList<Integer> subjectiveQoeMetrics;
    private final String[] ATTRIBUTES = {"video", "extension", "address", "useDash", "subjectiveQoeMetrics"};

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isUseDash() {
        return useDash;
    }

    public void setUseDash(boolean useDash) {
        this.useDash = useDash;
    }

    public boolean isUsedAux() {
        return isUsedAux;
    }

    public void setUsedAux(boolean usedAux) {
        isUsedAux = usedAux;
    }

    public ArrayList<Integer> getSubjectiveQoeMetrics() {
        return subjectiveQoeMetrics;
    }

    public void setSubjectiveQoeMetrics(ArrayList<Integer> subjectiveQoeMetrics) {
        this.subjectiveQoeMetrics = subjectiveQoeMetrics;
    }

    public TScript fromJson(JSONObject json) {
        int cont = 0;
        try {
            this.video = json.getString(ATTRIBUTES[cont++]);
            this.extension = json.getString(ATTRIBUTES[cont++]);
            this.address = json.getString(ATTRIBUTES[cont++]);
            this.useDash = json.getBoolean(ATTRIBUTES[cont++]);

            this.subjectiveQoeMetrics = new ArrayList<>();
            JSONArray qoe = json.getJSONArray(ATTRIBUTES[cont]);

            for (int i = 0; i < qoe.length(); i++)
                subjectiveQoeMetrics.add((Integer) qoe.get(i));

        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        int cont = 0;
        try {
            json.put(ATTRIBUTES[cont++], this.getVideo());
            json.put(ATTRIBUTES[cont++], this.getExtension());
            json.put(ATTRIBUTES[cont++], this.getAddress());
            json.put(ATTRIBUTES[cont++], this.isUseDash());
            for (Integer i : this.getSubjectiveQoeMetrics())
                array.put(i);
            json.put(ATTRIBUTES[cont], array);
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    public JSONObject toSimpleJson() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        int cont = 3;
        try {
            json.put(ATTRIBUTES[cont++], this.isUseDash());
            for (Integer i : this.getSubjectiveQoeMetrics())
                array.put(i);
            json.put(ATTRIBUTES[cont], array);
        } catch (JSONException e) {
            return null;
        }
        return json;

    }

    public TScript fromSimplesJson(JSONObject json) {
        int cont = 3;
        try {
            this.useDash = json.getBoolean(ATTRIBUTES[cont++]);

            this.subjectiveQoeMetrics = new ArrayList<>();
            JSONArray qoe = json.getJSONArray(ATTRIBUTES[cont]);

            for (int i = 0; i < qoe.length(); i++)
                subjectiveQoeMetrics.add((Integer) qoe.get(i));

        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    public String getProvider() {
        return this.getAddress().concat("/").concat(this.getVideo()).concat(".").concat(this.getExtension());
    }


}
