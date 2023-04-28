package edu.northeastern.numad22fa_team51_project.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String user_email;
    private String user_id;
    private String user_name;
    private String user_passwd;
    private String user_img;
    private String user_mobile;
    private Boolean selected = false;
    private String user_points;
    private String user_tasks_completed;

    public UserModel() {
    }
    public UserModel(String user_email, String user_id, String user_name, String user_passwd, String user_img, String user_mobile, String user_points, String user_tasks_completed) {
        this.user_email = user_email;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_passwd = user_passwd;
        this.user_img = user_img;
        this.user_mobile = user_mobile;
        this.user_points = user_points;
        this.user_tasks_completed = user_tasks_completed;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_passwd() {
        return user_passwd;
    }

    public void setUser_passwd(String user_passwd) {
        this.user_passwd = user_passwd;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_points() {
        return user_points;
    }

    public void setUser_points(String user_points) {
        this.user_points = user_points;
    }

    public String getUser_tasks_completed() {
        return user_tasks_completed;
    }

    public void setUser_tasks_completed(String user_tasks_completed) {
        this.user_tasks_completed = user_tasks_completed;
    }
}