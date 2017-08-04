package br.rnp.futebol.verona.pojo.unused;

import java.util.ArrayList;

/**
 * Created by camargo on 10/11/16.
 */
public class Experiment {

    private String name;
    private String filename;
    private ArrayList<Script> scripts;
    private final String ATTRIBUTES[] = {"name", "filename", "scripts"};

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

    public ArrayList<Script> getScripts() {
        return scripts;
    }

    public void setScripts(ArrayList<Script> scripts) {
        this.scripts = scripts;
    }



}
