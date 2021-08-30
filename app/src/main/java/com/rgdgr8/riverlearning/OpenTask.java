package com.rgdgr8.riverlearning;

import com.google.gson.annotations.SerializedName;

public class OpenTask {
    public static final String OPEN = "OPEN";
    public static final String CLOSED = "CLOSED";
    public static final String WAITING = "CLOSED";
    public static final String IN_PROGRESS = "CLOSED";

    private String task;
    @SerializedName(value = "allocatedby_name", alternate = {"allocatedto_name"})
    private String alloc;
    private String allocation_date;
    private String target_end;
    private String  status;

    public OpenTask(String task, String alloc, String allocation_date, String target_end, String status) {
        this.task = task;
        this.alloc = alloc;
        this.allocation_date = allocation_date;
        this.target_end = target_end;
        this.status = status;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setAlloc(String alloc) {
        this.alloc = alloc;
    }

    public void setAllocation_date(String allocation_date) {
        this.allocation_date = allocation_date;
    }

    public void setTarget_end(String target_end) {
        this.target_end = target_end;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public String getAlloc() {
        return alloc;
    }

    public String getAllocation_date() {
        return allocation_date;
    }

    public String getTarget_end() {
        return target_end;
    }

    public String getStatus() {
        return status;
    }
}
