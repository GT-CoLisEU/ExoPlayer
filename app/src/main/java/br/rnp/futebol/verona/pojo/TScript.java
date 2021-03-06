package br.rnp.futebol.verona.pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class TScript implements Serializable {

    private String video;
    private String extension;
    private String address;
    private int loop;
    private boolean useDash, isUsedAux;
    private ArrayList<Integer> subjectiveQoeMetrics;
    private BinaryQuestion question;
    private final String[] ATTRIBUTES = {"video", "extension", "address", "useDash", "subjectiveQoeMetrics", "question", "loop"};

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

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public TScript fromJson(JSONObject json) {
        int cont = 0;
        try {
            this.video = json.getString(ATTRIBUTES[cont++]);
            this.extension = json.getString(ATTRIBUTES[cont++]);
            this.address = json.getString(ATTRIBUTES[cont++]);
            this.useDash = json.getBoolean(ATTRIBUTES[cont++]);

            this.subjectiveQoeMetrics = new ArrayList<>();
            JSONArray qoe = json.getJSONArray(ATTRIBUTES[cont++]);

            for (int i = 0; i < qoe.length(); i++)
                subjectiveQoeMetrics.add((Integer) qoe.get(i));
            if (json.has(ATTRIBUTES[cont]))
                this.question = new BinaryQuestion().fromJson(json.getJSONObject(ATTRIBUTES[cont]));
            if (json.has(ATTRIBUTES[++cont]))
                this.loop = json.getInt(ATTRIBUTES[cont]);
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
            json.put(ATTRIBUTES[cont++], array);
            if (this.question != null)
                json.put(ATTRIBUTES[cont], this.question.toJson());
            json.put(ATTRIBUTES[++cont], this.getLoop());
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
            if (this.getSubjectiveQoeMetrics() != null && !this.getSubjectiveQoeMetrics().isEmpty())
            for (Integer i : this.getSubjectiveQoeMetrics())
                array.put(i);
            json.put(ATTRIBUTES[cont++], array);
            if (this.question != null)
                json.put(ATTRIBUTES[cont], this.question.toJson());
            json.put(ATTRIBUTES[++cont], this.getLoop());
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
            if (json.has(ATTRIBUTES[cont])) {
                JSONArray qoe = json.getJSONArray(ATTRIBUTES[cont]);
                for (int i = 0; i < qoe.length(); i++)
                    subjectiveQoeMetrics.add((Integer) qoe.get(i));
            }
            if (json.has(ATTRIBUTES[++cont]))
                this.question = new BinaryQuestion().fromJson(json.getJSONObject(ATTRIBUTES[cont]));
            if (json.has(ATTRIBUTES[++cont]))
                this.loop = json.getInt(ATTRIBUTES[cont]);
        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    public String getProvider() {
        return this.getAddress().concat("/").concat(this.getVideo()).concat(".").concat(this.getExtension());
    }

    public BinaryQuestion getQuestion() {
        return question;
    }

    public void setQuestion(BinaryQuestion question) {
        this.question = question;
    }

    public TScript copy(TScript s) {
        this.video = s.getVideo();
        this.extension = s.getExtension();
        this.address = s.getAddress();
        return this;
    }

    @Override
    public String toString() {
        return this.getAddress() + ("/") + (this.getVideo()) + (".") + (this.getExtension());
    }
}
