package br.rnp.futebol.vocoliseu.pojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by camargo on 18/10/16.
 */
public class Metric {

    public static final int QOS = 1, S_QOE = 2, O_QOE = 3;
    private String name;
    private int id, type;
    private boolean used;
    private final String ATTRIBUTES[] = {"id", "name", "type", "used"};

    public Metric() {
    }

    public Metric(int id, String name, int type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public JSONObject toJson() {
        int cont = 0;
        JSONObject json = new JSONObject();
        try {
            json.put(ATTRIBUTES[cont++], this.id);
            json.put(ATTRIBUTES[cont++], this.name);
            json.put(ATTRIBUTES[cont++], this.type);
            json.put(ATTRIBUTES[cont], this.used);
        } catch (JSONException e) {
            json = null;
        }
        return json;
    }

    public Metric fromJson(JSONObject json) {
        int cont = 0;
        try {
            this.id = json.getInt(ATTRIBUTES[cont++]);
            this.name = json.getString(ATTRIBUTES[cont++]);
            this.type = json.getInt(ATTRIBUTES[cont++]);
            this.used = json.getBoolean(ATTRIBUTES[cont]);
        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    public String getTypeName() {
        switch (this.type) {
            case 1:
                return "QoS";
            case 2:
                return "QoE";
            case 3:
                return "QoE";
            default:
                break;
        }
        return null;
    }


}
