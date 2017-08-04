package br.rnp.futebol.verona.pojo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by camargo on 18/10/16.
 */
public class InfoHelper implements Serializable {

    private boolean askAge, askGender, askCons, askFam;
    private final String ATTRIBUTES[] = {"askAge", "askGender", "askCons", "askFam"};

    public InfoHelper(boolean askAge, boolean askGender, boolean askCons, boolean askFam) {
        this.askAge = askAge;
        this.askGender = askGender;
        this.askCons = askCons;
        this.askFam = askFam;
    }

    public boolean hasInfo() {
        return askAge || askGender || askCons || askFam;
    }

    public InfoHelper(){}

    public boolean askAge() {
        return askAge;
    }

    public void setAskAge(boolean askAge) {
        this.askAge = askAge;
    }

    public boolean askGender() {
        return askGender;
    }

    public void setAskGender(boolean askGender) {
        this.askGender = askGender;
    }

    public boolean askCons() {
        return askCons;
    }

    public void setAskCons(boolean askCons) {
        this.askCons = askCons;
    }

    public boolean askFam() {
        return askFam;
    }

    public void setAskFam(boolean askFam) {
        this.askFam = askFam;
    }

    public JSONObject toJson() {
        int cont = 0;
        JSONObject json = new JSONObject();
        try {
            json.put(ATTRIBUTES[cont++], this.askAge);
            json.put(ATTRIBUTES[cont++], this.askGender);
            json.put(ATTRIBUTES[cont++], this.askCons);
            json.put(ATTRIBUTES[cont], this.askFam);
        } catch (JSONException e) {
            json = null;
        }
        return json;
    }

    public InfoHelper fromJson(JSONObject json) {
        int cont = 0;
        try {
            this.askAge = json.getBoolean(ATTRIBUTES[cont++]);
            this.askGender = json.getBoolean(ATTRIBUTES[cont++]);
            this.askCons = json.getBoolean(ATTRIBUTES[cont++]);
            this.askFam = json.getBoolean(ATTRIBUTES[cont]);
        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    public boolean compareTo(InfoHelper iHelper) {
        return this.askAge == iHelper.askAge()
                && this.askGender == iHelper.askGender()
                && this.askCons == iHelper.askCons()
                && this.askFam == iHelper.askFam();
    }


}
