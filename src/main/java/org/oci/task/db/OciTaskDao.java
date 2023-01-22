package org.oci.task.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.oci.task.core.OciTask;

import java.util.List;
import java.util.Optional;

/**
 * @brief Data Access Object for persisting Tasks in OCI Task System.
 * @author rajeskurup@live.com
 */
public class OciTaskDao extends AbstractDAO<OciTask> {

    public OciTaskDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<OciTask> findById(long id) {
        return Optional.ofNullable(get(id));
    }

    public OciTask save(OciTask ociTask) {
        return persist(ociTask);
    }

    public List<OciTask> findAll() {
        return list(query("FROM org.oci.task.core.OciTask"));
    }

    public void delete(long id) {
        Query query = currentSession().createNamedQuery("org.oci.task.core.OciTask.deleteById");
        query.setParameter("taskId", id);
        query.executeUpdate();
    }

}
