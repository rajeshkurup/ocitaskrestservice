package org.oci.task.resources;

import io.dropwizard.hibernate.UnitOfWork;
import org.oci.task.core.OciTask;
import org.oci.task.db.OciTaskDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

/**
 * @brief APIs hosted by OCI Task Service to persist and manage Tasks.
 * @author rajeshkurup@live.com
 */
@Path("/v1/ocitaskrestservice")
public class OciTaskResource {

    private static Logger logger = LoggerFactory.getLogger(OciTaskResource.class);

    private final OciTaskDao ociTaskDao;

    public OciTaskResource(OciTaskDao ociTaskDao) {
        this.ociTaskDao = ociTaskDao;
    }

    @POST
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tasks")
    public OciTask createTask(OciTask ociTask) {
        logger.info("Creating new Task");
        return ociTaskDao.save(ociTask);
    }

    @PUT
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tasks/{id}")
    public OciTask updateTask(@PathParam("id") long id, OciTask ociTask) {
        logger.info("Updating existing Task - taskId=" + id);
        ociTask.setId(id);
        return ociTaskDao.save(ociTask);
    }

    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tasks")
    public List<OciTask> listTasks() {
        logger.info("Load Tasks");
        return ociTaskDao.findAll();
    }

    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tasks/{id}")
    public Optional<OciTask> getTask(@PathParam("id") long id) {
        logger.info("Getting existing Task - taskId=" + id);
        return ociTaskDao.findById(id);
    }

    @DELETE
    @UnitOfWork
    @Path("/tasks/{id}")
    public void deleteTask(@PathParam("id") long id) {
        logger.info("Deleting existing Task - taskId=" + id);
        ociTaskDao.delete(id);
    }

}
