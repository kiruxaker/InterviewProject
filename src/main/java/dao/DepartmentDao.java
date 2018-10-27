package dao;

import model.Department;
import util.ActionWrapper;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Implementation of general DAO interface.
 * This class is used to work with stored Department entity instances.
 *
 * @see Dao
 */
public class DepartmentDao implements Dao<Department, Integer> {

    private EntityManagerFactory factory;

    private static final Logger LOGGER = Logger.getLogger(DepartmentDao.class);
    private static final String SELECT_ALL_DEPARTMENTS_QUERY = "SELECT d FROM Department d";

    public DepartmentDao(EntityManagerFactory factory) {
        this.factory = factory;
        LOGGER.trace("create new instance of DepartmentDao");
    }

    @Override
    public List<Department> findAll() {
        LOGGER.info("search for all Departments (without limits)");
        return ActionWrapper.wrap(factory, new Department(), ActionWrapper.NO_LIMIT)
                .execute((manager, entity, limit) -> manager
                        .createQuery(SELECT_ALL_DEPARTMENTS_QUERY, Department.class)
                        .setMaxResults(limit)
                        .getResultList());
    }

    @Override
    public List<Department> findAll(int offset, int length) {
        if (offset < 0 || length < 0) {
            LOGGER.error("offset or/and length is less then 0");
            return null;
        }

        LOGGER.info("search for all Departments (with limits)");
        return ActionWrapper.wrap(factory, new Department(), length)
                .execute((manager, entity, limit) -> manager
                        .createQuery(SELECT_ALL_DEPARTMENTS_QUERY, Department.class)
                        .setMaxResults(limit)
                        .setFirstResult(offset)
                        .getResultList());
    }

    @Override
    public Department find(Integer integer) {
        if (integer == null || integer < 0) {
            LOGGER.error("integer is null or is less then 0");
            return null;
        }

        LOGGER.info("search for single Department");
        return ActionWrapper.wrap(factory, new Department())
                .execute((manager, entity) -> {
                    return manager.find(entity.getClass(), integer);
                });
    }

    @Override
    public Department remove(Integer integer) {
        if (integer == null || integer < 0) {
            LOGGER.error("integer is null or is less then 0");
            return null;
        }

        LOGGER.info("remove single Department");
        return ActionWrapper.wrap(factory, new Department())
                .executeWithTransaction((manager, entity) -> {
                    Department removed = manager.find(entity.getClass(), integer);
                    manager.remove(removed);
                    return removed;
                });
    }

    @Override
    public Department update(Department entity) {
        if (entity == null) {
            LOGGER.error("entity is null");
            return null;
        }

        LOGGER.info("update single Department");
        return ActionWrapper.wrap(factory, entity)
                .executeWithTransaction((manager, wrapEntity) -> {
                    Department old = manager.find(wrapEntity.getClass(), wrapEntity.getId()).clone();
                    manager.merge(entity);
                    return old;
                });
    }

    @Override
    public Department create(Department entity) {
        if (entity == null) {
            LOGGER.error("entity is null");
            return null;
        }

        LOGGER.info("create and save new Department");
        ActionWrapper.wrap(factory, entity)
                .executeWithTransaction(EntityManager::persist);

        LOGGER.info("new Department was successfully created");
        return ActionWrapper.wrap(factory, entity)
                .execute((manager, wrapEntity) -> {
                    return manager.find(wrapEntity.getClass(), wrapEntity.getId());
                });
    }
}
