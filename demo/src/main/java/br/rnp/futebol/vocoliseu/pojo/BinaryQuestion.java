package br.rnp.futebol.vocoliseu.pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by camargo on 23/11/16.
 */
public class BinaryQuestion implements Serializable {

    private String question, answer1, answer2;
    private final String[] ATTRIBUTES = {"question", "answer1", "answer2"};


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public JSONObject toJson() {
        int cont = 0;
        JSONObject json = new JSONObject();
        try {
            json.put(ATTRIBUTES[cont++], this.getQuestion());
            json.put(ATTRIBUTES[cont++], this.getAnswer1());
            json.put(ATTRIBUTES[cont], this.getAnswer2());
        } catch (JSONException e) {
            return null;
        }
        return json;
    }

    public BinaryQuestion fromJson(JSONObject json) {
        int cont = 0;
        try {
            this.question = json.getString(ATTRIBUTES[cont++]);
            this.answer1 = json.getString(ATTRIBUTES[cont++]);
            this.answer2 = json.getString(ATTRIBUTES[cont]);
        } catch (Exception e) {
            return null;
        }
        return this;
    }

}
