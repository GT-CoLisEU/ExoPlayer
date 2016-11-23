package br.rnp.futebol.vocoliseu.pojo;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by camargo on 10/11/16.
 */
public class TExperiment implements Serializable {

    private String name;
    private String filename;
    private boolean askInfo;
    private boolean usedAux;
    private String instruction;
    private ArrayList<Integer> qosMetrics;
    private ArrayList<Integer> objectiveQoeMetrics;
    private ArrayList<TScript> scripts;
    private final String[] ATTRIBUTES = {"name", "filename", "askinfo", "instruction", "qosMetrics", "objQoeMetrics", "scripts"};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isAskInfo() {
        return askInfo;
    }

    public void setAskInfo(boolean askInfo) {
        this.askInfo = askInfo;
    }

    public boolean isUsedAux() {
        return usedAux;
    }

    public void setUsedAux(boolean usedAux) {
        this.usedAux = usedAux;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public ArrayList<Integer> getQosMetrics() {
        return qosMetrics;
    }

    public void setQosMetrics(ArrayList<Integer> qosMetrics) {
        this.qosMetrics = qosMetrics;
    }

    public ArrayList<Integer> getObjectiveQoeMetrics() {
        return objectiveQoeMetrics;
    }

    public void setObjectiveQoeMetrics(ArrayList<Integer> objectiveQoeMetrics) {
        this.objectiveQoeMetrics = objectiveQoeMetrics;
    }

    public ArrayList<TScript> getScripts() {
        return scripts;
    }

    public void setScripts(ArrayList<TScript> scripts) {
        this.scripts = scripts;
    }

    public TExperiment fromJson(JSONObject json) {
        int cont = 0;
        try {
            this.name = json.getString(ATTRIBUTES[cont++]);
            this.filename = json.getString(ATTRIBUTES[cont++]);
            this.askInfo = json.getBoolean(ATTRIBUTES[cont++]);
            this.instruction = json.getString(ATTRIBUTES[cont++]);

            JSONArray qos = json.getJSONArray(ATTRIBUTES[cont++]);
            JSONArray qoe = json.getJSONArray(ATTRIBUTES[cont++]);
            JSONArray jscripts = json.getJSONArray(ATTRIBUTES[cont]);

            this.qosMetrics = new ArrayList<>();
            this.objectiveQoeMetrics = new ArrayList<>();
            this.scripts = new ArrayList<>();

            for (int i = 0; i < qos.length(); i++)
                this.qosMetrics.add((Integer) qos.get(i));

            for (int i = 0; i < qoe.length(); i++)
                this.objectiveQoeMetrics.add((Integer) qoe.get(i));

            for (int i = 0; i < jscripts.length(); i++)
                this.scripts.add(new TScript().fromJson((JSONObject) jscripts.get(i)));

        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    public JSONObject toJson() {
        int cont = 0;
        JSONObject json = new JSONObject();
        JSONArray qos = new JSONArray(), qoe = new JSONArray(), scripts = new JSONArray();
        try {
            json.put(ATTRIBUTES[cont++], this.getName());
            json.put(ATTRIBUTES[cont++], this.getFilename());
            json.put(ATTRIBUTES[cont++], this.isAskInfo());
            json.put(ATTRIBUTES[cont++], this.getInstruction());
            for (Integer i : this.getQosMetrics())
                qos.put(i);
            for (Integer i : this.getObjectiveQoeMetrics())
                qoe.put(i);
            for (TScript s : this.getScripts())
                scripts.put(s.toJson());
            json.put(ATTRIBUTES[cont++], qos);
            json.put(ATTRIBUTES[cont++], qoe);
            json.put(ATTRIBUTES[cont], scripts);
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

//    "name", "filename", "askinfo", "instruction", "qosMetrics", "objQoeMetrics", "scripts"


}
