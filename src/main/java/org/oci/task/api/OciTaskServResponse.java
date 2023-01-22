package org.oci.task.api;

import org.oci.task.core.OciTask;
import org.oci.task.error.OciError;

import java.util.List;

/**
 * @brief Payload would be returned from OCI Task Service as API response.
 * @author rajeshkurup@live.com
 */
public class OciTaskServResponse {

    private long taskId;

    private OciTask task;

    private List<OciTask> tasks;

    private OciError error;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public OciTask getTask() {
        return task;
    }

    public void setTask(OciTask task) {
        this.task = task;
    }

    public List<OciTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<OciTask> tasks) {
        this.tasks = tasks;
    }

    public OciError getError() {
        return error;
    }

    public void setError(OciError error) {
        this.error = error;
    }

}
