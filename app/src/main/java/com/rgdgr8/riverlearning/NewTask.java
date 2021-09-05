package com.rgdgr8.riverlearning;

import org.jetbrains.annotations.NotNull;

//TODO is used for both new task and updating allocated task
public class NewTask {
    private String task;
    private String description;
    private String repeat;
    private String target_end;
    private Integer allocated_to;
    private String status;

    public String getTask() {
        return task;
    }

    public String getDescription() {
        return description;
    }

    public String getRepeat() {
        return repeat;
    }

    public String getTarget_end() {
        return target_end;
    }

    public Integer getAllocated_to() {
        return allocated_to;
    }

    public String getStatus() {
        return status;
    }

    public NewTask(@NotNull String task, @NotNull String description, String repeat, String target_end, Integer allocated_to, String status) {
        this.task = task;
        this.description = description;
        this.repeat = repeat;
        this.target_end = target_end;
        this.allocated_to = allocated_to;
        this.status = status;

        if (this.repeat.charAt(0) == '-')
            this.repeat = null;
        if (this.allocated_to<1)//condition is subject to change if name starts with -
            this.allocated_to = null;
        if (this.status.charAt(0) == '-')
            this.status = null;
    }
}
