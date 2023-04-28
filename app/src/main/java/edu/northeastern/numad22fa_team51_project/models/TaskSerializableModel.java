package edu.northeastern.numad22fa_team51_project.models;


import java.io.Serializable;
import java.util.ArrayList;

public class TaskSerializableModel implements Serializable {

    private String card_id;
    private String board_id;
    private String card_name;
    private String card_notes;
    private String createdBy;
    private ArrayList<String> assignedTo;
    private String memberList;
    private String DueDate;
    private String points;
    private String isComplete;

    public TaskSerializableModel() {
    }

    public TaskSerializableModel(String card_id, String board_id, String card_name, String card_notes, String createdBy, ArrayList<String> assignedTo, String memberList, String dueDate, String points, String isComplete) {
        this.card_id = card_id;
        this.board_id = board_id;
        this.card_name = card_name;
        this.card_notes = card_notes;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.memberList = memberList;
        this.DueDate = dueDate;
        this.points = points;
        this.isComplete = isComplete;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getBoard_id() {
        return board_id;
    }

    public void setBoard_id(String board_id) {
        this.board_id = board_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_notes() {
        return card_notes;
    }

    public void setCard_notes(String card_notes) {
        this.card_notes = card_notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ArrayList<String> getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(ArrayList<String> assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getMemberList() {
        return memberList;
    }

    public void setMemberList(String memberList) {
        this.memberList = memberList;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        this.DueDate = dueDate;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }
}
