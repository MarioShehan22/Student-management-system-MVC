package com.shehan.edumanage.view.tm;


import javafx.scene.control.Button;

import java.util.Date;

public class IntakeTm {
    private String code1;
    private String intakeName;
    private Date date;
    private String Programs;
    private String completeState;
    private Button btn;

    public IntakeTm() {

    }

    public IntakeTm(String code1, String intakeName, Date date, String programs, String completeState, Button btn) {
        this.code1 = code1;
        this.intakeName = intakeName;
        this.date = date;
        Programs = programs;
        this.completeState = completeState;
        this.btn = btn;
    }

    public String getCode() {
        return code1;
    }

    public void setCode(String code1) {
        this.code1 = code1;
    }

    public String getIntakeName() {
        return intakeName;
    }

    public void setIntakeName(String intakeName) {
        this.intakeName = intakeName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPrograms() {
        return Programs;
    }

    public void setPrograms(String programs) {
        Programs = programs;
    }

    public String getCompleteState() {
        return completeState;
    }

    public void setCompleteState(String completeState) {
        this.completeState = completeState;
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }
}
