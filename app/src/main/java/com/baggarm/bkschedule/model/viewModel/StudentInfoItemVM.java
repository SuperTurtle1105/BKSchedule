package com.baggarm.bkschedule.model.viewModel;

public class StudentInfoItemVM {
    private String title;
    private int leftIconID;
    private int rightIconID;

    public StudentInfoItemVM(String title, int leftIconID, int rightIconID) {
        this.title = title;
        this.leftIconID = leftIconID;
        this.rightIconID = rightIconID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newValue) {
        this.title = newValue;
    }

    public int getLeftIconID() {
        return leftIconID;
    }

    public void setLeftIconID(int leftIconID) {
        this.leftIconID = leftIconID;
    }

    public int getRightIconID() {
        return rightIconID;
    }

    public void setRightIconID(int rightIconID) {
        this.rightIconID = rightIconID;
    }
}
