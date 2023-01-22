package org.oci.task.resources;

import io.dropwizard.hibernate.UnitOfWork;
import org.oci.task.api.OciTaskServResponse;
import org.oci.task.core.OciTask;
import org.oci.task.db.OciTaskDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @OPTIONS
    @PermitAll
    public Response options() {
        return prepareResponse(Response.Status.NO_CONTENT, "");
    }

    @OPTIONS
    @Path("/tasks")
    @PermitAll
    public Response optionsAll() {
        return prepareResponse(Response.Status.NO_CONTENT, "");
    }

    @OPTIONS
    @Path("/tasks/{id}")
    @PermitAll
    public Response optionsId() {
        return prepareResponse(Response.Status.NO_CONTENT, "");
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
    public Response listTasks() {
        logger.info("Load Tasks");
        OciTaskServResponse ociResponse = new OciTaskServResponse();
        ociResponse.setTasks(ociTaskDao.findAll());
        return prepareResponse(Response.Status.OK, ociResponse);
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

    private Response prepareResponse(Response.Status httpStatus, Object ociResponse) {
        return Response.status(httpStatus)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Access-Control-Allow-Origin,Content-Type,Authorization,Content-Length,Accept,Origin")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH")
                .header("Access-Control-Max-Age", "1209600")
                .entity(ociResponse)
                .build();
    }

}
