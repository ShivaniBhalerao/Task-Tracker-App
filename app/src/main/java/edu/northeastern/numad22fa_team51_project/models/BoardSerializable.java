package edu.northeastern.numad22fa_team51_project.models;

import java.io.Serializable;
import java.util.ArrayList;

public class BoardSerializable implements Serializable {

    private String group_name = "";
    private String group_image = "";
    private String group_createdBy = "";
    private ArrayList<String> group_assignedTo = new ArrayList<>();
    private String documentId = "";
    public ArrayList<TaskSerializableModel> taskSerializableList = new ArrayList<>();
    private String group_created_by_user_name = "";

    public BoardSerializable(String group_name, String group_image, String group_createdBy, ArrayList<String> group_assignedTo, String documentId) {
        this.group_name = group_name;
        this.group_image = group_image;
        this.group_createdBy = group_createdBy;
        this.group_assignedTo = group_assignedTo;
        this.documentId = documentId;
    }

    public String getGroup_created_by_user_name() {
        return group_created_by_user_name;
    }

    public void setGroup_created_by_user_name(String group_created_by_user_name) {
        this.group_created_by_user_name = group_created_by_user_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_image() {
        return group_image;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
    }

    public String getGroup_createdBy() {
        return group_createdBy;
    }

    public void setGroup_createdBy(String group_createdBy) {
        this.group_createdBy = group_createdBy;
    }

    public ArrayList<String> getGroup_assignedTo() {
        return group_assignedTo;
    }

    public void setGroup_assignedTo(ArrayList<String> group_assignedTo) {
        this.group_assignedTo = group_assignedTo;
    }
}
