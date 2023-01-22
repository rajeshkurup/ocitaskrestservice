package org.oci.task.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @brief API Request for OCI Task Service.
 * @author rajeshkurup@live.com
 */
public class OciTaskServRequest {

    /**
     * @brief Title of the Task (Maximum 1kb). Should be a valid text.
     */
    @NotNull
    @NotBlank
    private String title;

    /**
     * @brief Description of the Task (Maximum 64kb).
     */
    private String description;

    /**
     * @brief Priority of the task as number.
     */
    private int priority;

    /**
     * @brief Status of the Task. True means the Task is Completed otherwise Open. It would be False by default.
     */
    private boolean completed;

    /**
     * @brief Start date-time of the Task.
     */
    private Date startDate;

    /**
     * @brief Due date-time of the Task.
     */
    private Date dueDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

}
