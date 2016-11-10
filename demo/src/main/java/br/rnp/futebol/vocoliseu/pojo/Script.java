package br.rnp.futebol.vocoliseu.pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * [UFRGS] VO-CoLisEU
 * Created by camargo on 19/10/2016
 * Script bean
 */
public class Script {

    private String name;
    private String fileName;
    private String address;
    private boolean askInfo;
    private boolean useDash;
    private String description;
    private List<Metric> metrics;
    private final String ATTRIBUTES[] = {"name", "filename", "address", "askInfo", "useDash", "description", "metrics"};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAskInfo() {
        return askInfo;
    }

    public void setAskInfo(boolean askInfo) {
        this.askInfo = askInfo;
    }

    public boolean isUseDash() {
        return useDash;
    }

    public void setUseDash(boolean useDash) {
        this.useDash = useDash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        int cont = 0;
        try {
            json.put(ATTRIBUTES[cont++], this.getName());
            json.put(ATTRIBUTES[cont++], this.getFileName());
            json.put(ATTRIBUTES[cont++], this.getAddress());
            json.put(ATTRIBUTES[cont++], this.isAskInfo());
            json.put(ATTRIBUTES[cont++], this.isUseDash());
            json.put(ATTRIBUTES[cont++], this.getDescription());
            for (Metric m : this.getMetrics())
                array.put(m.toJson());
            json.put(ATTRIBUTES[cont], array);
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    public Script fromJson(JSONObject json) {
        int cont = 0;
        try {
            this.name = json.getString(ATTRIBUTES[cont++]);
            this.fileName = json.getString(ATTRIBUTES[cont++]);
            this.address = json.getString(ATTRIBUTES[cont++]);
            this.askInfo = json.getBoolean(ATTRIBUTES[cont++]);
            this.useDash = json.getBoolean(ATTRIBUTES[cont++]);
            this.description = json.getString(ATTRIBUTES[cont++]);
            JSONArray array = json.getJSONArray(ATTRIBUTES[cont]);
            this.metrics = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                this.metrics.add(new Metric().fromJson((JSONObject) array.get(i)));
            }
        } catch (JSONException e) {
            return null;
        }
        return this;
    }

}
