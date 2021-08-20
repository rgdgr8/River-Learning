package com.rgdgr8.riverlearning;

public class OpenTask {
    public static final String OPEN = "OPEN";
    public static final String CLOSED = "CLOSED";

    private final Integer id;
    private String task;
    private String allocated_to;
    private String allocation_date;
    private String target_end;
    private String  status;

    public OpenTask(int id, String task, String allocated_to, String allocation_date, String target_end, String  status) {
        this.id = id;
        this.task = task;
        this.allocated_to = allocated_to;
        this.allocation_date = allocation_date;
        this.target_end = target_end;
        this.status = status;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setAllocated_to(String allocated_to) {
        this.allocated_to = allocated_to;
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

    public int getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public String getAllocated_to() {
        return allocated_to;
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
