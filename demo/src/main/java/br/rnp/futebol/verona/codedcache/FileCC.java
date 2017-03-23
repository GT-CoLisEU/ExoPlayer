package br.rnp.futebol.verona.codedcache;

import java.io.Serializable;

public class FileCC implements Serializable {

    private String name, provider, extension;
    private int chunkDuration, parts;

    public FileCC(String name, String provider, String extension, int chunkDuration, int parts) {
        this.name = name;
        this.provider = provider;
        this.extension = extension;
        this.chunkDuration = chunkDuration;
        this.parts = parts;
    }

    public int getParts() {
        return parts;
    }

//    public void setParts(int parts) {
//        this.parts = parts;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExtension() {
        return extension;
    }

//    public void setExtension(String extension) {
//        this.extension = extension;
//    }

    public int getChunkDuration() {
        return chunkDuration;
    }

//    public void setChunkDuration(int chunkDuration) {
//        this.chunkDuration = chunkDuration;
//    }
}
