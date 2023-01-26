package org.oci.task.resources;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.oci.task.api.OciTaskServRequest;
import org.oci.task.api.OciTaskServResponse;
import org.oci.task.core.OciTask;
import org.oci.task.db.OciTaskDao;
import org.oci.task.error.OciError;
import org.oci.task.error.OciErrorCode;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @brief Unit Test helper for {@link OciTaskResource}
 * @author rajeshkurup@live.com
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class OciTaskResourceTest {

    private static final OciTaskDao ociTaskDaoMock = Mockito.mock(OciTaskDao.class);

    private static final OciTaskResource resource = new OciTaskResource(ociTaskDaoMock);

    @Test
    public void testGetTasksSuccess() {
        OciTask task = new OciTask();
        task.setTitle("Test task");
        List<OciTask> tasks = new ArrayList<OciTask>();
        tasks.add(task);

        Mockito.when(ociTaskDaoMock.findAll()).thenReturn(tasks);

        Response response = resource.listTasks();

        OciTaskServResponse resp = (OciTaskServResponse)response.getEntity();

        Assertions.assertEquals(1, resp.getTasks().size());
        Assertions.assertNull(resp.getError());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).findAll();
    }

    @Test
    public void testGetTasksFailed() {
        OciTaskServResponse resp = new OciTaskServResponse();
        resp.setError(new OciError(OciErrorCode.INVALID_ARGUMENT, "Invalid Argument"));

        Mockito.when(ociTaskDaoMock.findAll()).thenThrow(new RuntimeException("failed"));

        Response response = resource.listTasks();

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(resp.getError());
        Assertions.assertEquals(OciErrorCode.INTERNAL_ERROR, apiResp.getError().getErrorCode());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).findAll();
    }

    @Test
    public void testGetTaskSuccess() {
        OciTask task = new OciTask();
        task.setId(1001L);

        Mockito.when(ociTaskDaoMock.findById(Mockito.eq(1001L))).thenReturn(Optional.of(task));

        Response response = resource.getTask(1001L);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertEquals(1001L, apiResp.getTask().getId());
        Assertions.assertNull(apiResp.getError());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).findById(Mockito.eq(1001L));
    }

    @Test
    public void testGetTaskFailedInvalidId() {
        Response response = resource.getTask(0L);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INVALID_ARGUMENT, apiResp.getError().getErrorCode());
    }

    @Test
    public void testGetTaskFailedNotFound() {
        Mockito.when(ociTaskDaoMock.findById(Mockito.eq(1001L))).thenThrow(new NoSuchElementException("failed"));

        Response response = resource.getTask(1001L);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.NO_DATA_FOUND, apiResp.getError().getErrorCode());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).findById(Mockito.eq(1001L));
    }

    @Test
    public void testGetTaskFailed() {
        Mockito.when(ociTaskDaoMock.findById(Mockito.eq(1001L))).thenThrow(new RuntimeException("failed"));

        Response response = resource.getTask(1001L);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INTERNAL_ERROR, apiResp.getError().getErrorCode());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).findById(Mockito.eq(1001L));
    }

    @Test
    public void testDeleteTaskSuccess() {
        Mockito.doNothing().when(ociTaskDaoMock).delete(Mockito.eq(1001L));

        Response response = resource.deleteTask(1001L);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNull(apiResp.getError());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).delete(Mockito.eq(1001L));
    }

    @Test
    public void testDeleteTaskFailed() {
        Mockito.doThrow(new RuntimeException("failed")).when(ociTaskDaoMock).delete(Mockito.eq(1001L));

        Response response = resource.deleteTask(1001L);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INTERNAL_ERROR, apiResp.getError().getErrorCode());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).delete(Mockito.eq(1001L));
    }

    @Test
    public void testUpdateTaskSuccess() {
        OciTask task = new OciTask();
        task.setId(1001L);
        task.setTitle("test task");

        OciTaskServRequest ociTask = new OciTaskServRequest();
        ociTask.setTitle("test task");

        Mockito.when(ociTaskDaoMock.save(Mockito.any(OciTask.class))).thenReturn(task);

        Response response = resource.updateTask(1001L, ociTask);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNull(apiResp.getError());
        Assertions.assertEquals(1001L, apiResp.getTaskId());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).save(Mockito.any(OciTask.class));
    }

    @Test
    public void testUpdateTaskFailedInvalidId() {
        Response response = resource.updateTask(0L, new OciTaskServRequest());

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INVALID_ARGUMENT, apiResp.getError().getErrorCode());
    }

    @Test
    public void testUpdateTaskFailedNotFound() {
        OciTask task = new OciTask();
        task.setId(1001L);
        task.setTitle("test task");

        OciTaskServRequest ociTask = new OciTaskServRequest();
        ociTask.setTitle("test task");

        Mockito.when(ociTaskDaoMock.save(Mockito.any(OciTask.class))).thenThrow(new NoSuchElementException("failed"));

        Response response = resource.updateTask(1001L, ociTask);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INVALID_ARGUMENT, apiResp.getError().getErrorCode());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).save(Mockito.any(OciTask.class));
    }

    @Test
    public void testUpdateTaskFailed() {
        OciTask task = new OciTask();
        task.setId(1001L);
        task.setTitle("test task");

        OciTaskServRequest ociTask = new OciTaskServRequest();
        ociTask.setTitle("test task");

        Mockito.when(ociTaskDaoMock.save(Mockito.any(OciTask.class))).thenThrow(new RuntimeException("failed"));

        Response response = resource.updateTask(1001L, ociTask);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INTERNAL_ERROR, apiResp.getError().getErrorCode());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).save(Mockito.any(OciTask.class));
    }

    @Test
    public void testCreateTaskSuccess() {
        OciTask task = new OciTask();
        task.setId(1001L);
        task.setTitle("test task");

        OciTaskServRequest ociTask = new OciTaskServRequest();
        ociTask.setTitle("test task");

        Mockito.when(ociTaskDaoMock.save(Mockito.any(OciTask.class))).thenReturn(task);

        Response response = resource.createTask(ociTask);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNull(apiResp.getError());
        Assertions.assertEquals(1001L, apiResp.getTaskId());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).save(Mockito.any(OciTask.class));
    }

    @Test
    public void testCreateTaskFailedInvalidTask() {
        Response response = resource.createTask(new OciTaskServRequest());

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INVALID_ARGUMENT, apiResp.getError().getErrorCode());
    }

    @Test
    public void testCreateTaskFailed() {
        OciTask task = new OciTask();
        task.setId(1001L);
        task.setTitle("test task");

        OciTaskServRequest ociTask = new OciTaskServRequest();
        ociTask.setTitle("test task");

        Mockito.when(ociTaskDaoMock.save(Mockito.any(OciTask.class))).thenThrow(new RuntimeException("failed"));

        Response response = resource.createTask(ociTask);

        OciTaskServResponse apiResp = (OciTaskServResponse)response.getEntity();

        Assertions.assertNotNull(apiResp.getError());
        Assertions.assertEquals(OciErrorCode.INTERNAL_ERROR, apiResp.getError().getErrorCode());

        Mockito.verify(ociTaskDaoMock, Mockito.atLeastOnce()).save(Mockito.any(OciTask.class));
    }

}
