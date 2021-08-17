package com.rgdgr8.riverlearning;

public class OpenTask {
    private final int sr;
    private String task;
    private String allocTo;
    private String allocDate;
    private String targetDate;
    private boolean status;

    public OpenTask(int sr, String task, String allocTo, String allocDate, String targetDate, boolean status) {
        this.sr = sr;
        this.task = task;
        this.allocTo = allocTo;
        this.allocDate = allocDate;
        this.targetDate = targetDate;
        this.status = status;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setAllocTo(String allocTo) {
        this.allocTo = allocTo;
    }

    public void setAllocDate(String allocDate) {
        this.allocDate = allocDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getSr() {
        return sr;
    }

    public String getTask() {
        return task;
    }

    public String getAllocTo() {
        return allocTo;
    }

    public String getAllocDate() {
        return allocDate;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public boolean getStatus() {
        return status;
    }
}
