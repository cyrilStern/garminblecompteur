package com.example.root.garminblecompteur;

/**
 * Created by cyrilstern1 on 24/08/2017.
 */

public class FileContainer{
    private String path;
    private String name;

    public FileContainer(String name, String path) {
        this.path = path;
        this.name = name.replace(".gpx", "");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
