package edu.northeastern.numad22fa_team51_project.models;

import java.io.Serializable;

public class SelectedMembers implements Serializable {

    String id;
    String image;


    public SelectedMembers(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
