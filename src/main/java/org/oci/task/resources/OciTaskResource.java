package org.oci.task.resources;

import io.dropwizard.hibernate.UnitOfWork;
import org.apache.commons.lang3.StringUtils;
import org.oci.task.api.OciTaskServResponse;
import org.oci.task.core.OciTask;
import org.oci.task.db.OciTaskDao;
import org.oci.task.error.OciError;
import org.oci.task.error.OciErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.NoSuchElementException;
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
    public Response createTask(OciTask ociTask) {
        logger.info("Creating new Task");
        OciTaskServResponse ociResponse = new OciTaskServResponse();
        Response.Status httpStatus = Response.Status.OK;

        if(ociTask != null && StringUtils.isNotBlank(ociTask.getTitle())) {
            try {
                OciTask task = ociTaskDao.save(ociTask);
                ociResponse.setTaskId(task.getId());
                httpStatus = Response.Status.CREATED;
            }
            catch(Exception ex) {
                httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
                ociResponse.setError(new OciError(OciErrorCode.INTERNAL_ERROR, ex.getMessage()));
                logger.error(ex.toString() + " - Stacktrace: " + Arrays.asList(ex.getStackTrace()).toString());
            }
        }
        else {
            httpStatus = Response.Status.BAD_REQUEST;
            ociResponse.setError(new OciError(OciErrorCode.INVALID_ARGUMENT, "Task Title cannot be blank!"));
            logger.error("Task Title cannot be blank!");
        }

        return prepareResponse(httpStatus, ociResponse);
    }

    @PUT
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tasks/{id}")
    public Response updateTask(@PathParam("id") long id, OciTask ociTask) {
        logger.info("Updating existing Task - taskId=" + id);
        OciTaskServResponse ociResponse = new OciTaskServResponse();
        Response.Status httpStatus = Response.Status.OK;

        if(id != 0 && ociTask != null && StringUtils.isNotBlank(ociTask.getTitle())) {
            ociTask.setId(id);
            try {
                OciTask task = ociTaskDao.save(ociTask);
                ociResponse.setTaskId(task.getId());
            }
            catch(NoSuchElementException ex) {
                httpStatus = Response.Status.BAD_REQUEST;
                ociResponse.setError(new OciError(OciErrorCode.INVALID_ARGUMENT, ex.getMessage()));
                logger.error(ex.toString() + " - Stacktrace: " + Arrays.asList(ex.getStackTrace()).toString());
            }
            catch(Exception ex) {
                httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
                ociResponse.setError(new OciError(OciErrorCode.INTERNAL_ERROR, ex.getMessage()));
                logger.error(ex.toString() + " - Stacktrace: " + Arrays.asList(ex.getStackTrace()).toString());
            }
        }
        else {
            httpStatus = Response.Status.BAD_REQUEST;
            ociResponse.setError(new OciError(OciErrorCode.INVALID_ARGUMENT, "Id cannot be zero and Task Title cannot be blank!"));
            logger.error("Id cannot be zero and Task Title cannot be blank!");
        }

        return prepareResponse(httpStatus, ociResponse);
    }

    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tasks")
    public Response listTasks() {
        logger.info("Load Tasks");
        OciTaskServResponse ociResponse = new OciTaskServResponse();
        Response.Status httpStatus = Response.Status.OK;

        try {
            ociResponse.setTasks(ociTaskDao.findAll());
        }
        catch(Exception ex) {
            httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
            ociResponse.setError(new OciError(OciErrorCode.INTERNAL_ERROR, ex.getMessage()));
            logger.error(ex.toString() + " - Stacktrace: " + Arrays.asList(ex.getStackTrace()).toString());
        }

        return prepareResponse(httpStatus, ociResponse);
    }

    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tasks/{id}")
    public Response getTask(@PathParam("id") long id) {
        logger.info("Getting existing Task - taskId=" + id);
        OciTaskServResponse ociResponse = new OciTaskServResponse();
        Response.Status httpStatus = Response.Status.OK;

        if(id != 0) {
            try {
                Optional<OciTask> task = ociTaskDao.findById(id);
                ociResponse.setTask(task.isPresent() ? task.get() : null);
            } catch(NoSuchElementException ex) {
                httpStatus = Response.Status.NOT_FOUND;
                ociResponse.setError(new OciError(OciErrorCode.NO_DATA_FOUND, ex.getMessage()));
                logger.error(ex.toString() + " - Stacktrace: " + Arrays.asList(ex.getStackTrace()).toString());
            } catch(Exception ex) {
                httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
                ociResponse.setError(new OciError(OciErrorCode.INTERNAL_ERROR, ex.getMessage()));
                logger.error(ex.toString() + " - Stacktrace: " + Arrays.asList(ex.getStackTrace()).toString());
            }
        }
        else {
            httpStatus = Response.Status.BAD_REQUEST;
            ociResponse.setError(new OciError(OciErrorCode.INVALID_ARGUMENT, "Id cannot be zero!"));
            logger.error("Id cannot be zero!");
        }

        return prepareResponse(httpStatus, ociResponse);
    }

    @DELETE
    @UnitOfWork
    @Path("/tasks/{id}")
    public Response deleteTask(@PathParam("id") long id) {
        logger.info("Deleting existing Task - taskId=" + id);
        OciTaskServResponse ociResponse = new OciTaskServResponse();
        Response.Status httpStatus = Response.Status.OK;

        try {
            ociTaskDao.delete(id);
        }
        catch(Exception ex) {
            httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
            ociResponse.setError(new OciError(OciErrorCode.INTERNAL_ERROR, ex.getMessage()));
            logger.error(ex.toString() + " - Stacktrace: " + Arrays.asList(ex.getStackTrace()).toString());
        }

        return prepareResponse(httpStatus, ociResponse);
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
