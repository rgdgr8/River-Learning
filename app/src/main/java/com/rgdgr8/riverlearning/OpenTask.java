package com.rgdgr8.riverlearning;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpenTask implements Serializable {
    public static final String OPEN = "OPEN";
    public static final String CLOSED = "CLOSED";
    public static final String WAITING = "CLOSED";
    public static final String IN_PROGRESS = "CLOSED";

    private final Integer id;
    private String task;
    private String description;
    private String repeat = null;
    @SerializedName(value = "allocatedby_name", alternate = {"allocatedto_name"})
    private String alloc;
    private String allocation_date;
    private String target_end;
    private String status;

    public OpenTask(Integer id, String task, String description, String repeat, String alloc, String allocation_date, String target_end, String status) {
        this.id = id;
        this.task = task;
        this.description = description;
        this.repeat = repeat;
        this.alloc = alloc;
        this.allocation_date = allocation_date;
        this.target_end = target_end;
        this.status = status;

        /*if (this.repeat.charAt(0) == '-')
            this.repeat = null;
        if (this.alloc.charAt(0) == '-')//condition is subject to change if name starts with -
            this.alloc = null;
        if (this.status.charAt(0) == '-')
            this.status = null;*/
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "OpenTask{" +
                "id=" + id +
                ", task='" + task + '\'' + (task==null) +
                ", description='" + description + '\'' + (description==null) +
                ", repeat='" + repeat + '\'' + (repeat==null) +
                ", alloc='" + alloc + '\'' + (alloc==null) +
                ", allocation_date='" + allocation_date + '\'' + (allocation_date==null) +
                ", target_end='" + target_end + '\'' + (target_end==null) +
                ", status='" + status + '\'' + (status==null) +
                '}';
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
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

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getRepeat() {
        return repeat;
    }
}
