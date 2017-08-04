package br.rnp.futebol.verona.pojo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by camargo on 18/10/16.
 */
public class ResultPair implements Serializable {

    private String title, results;

    public ResultPair(String title, String results) {
        this.title = title;
        this.results = results;
    }

    public ResultPair() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
