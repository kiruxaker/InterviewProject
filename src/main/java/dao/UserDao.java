package dao;

import model.User;
import util.ActionWrapper;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Implementation of general DAO interface.
 * This class is used to work with stored User entity instances.
 * @see Dao
 */
public class UserDao implements Dao<User, Integer> {

    private EntityManagerFactory factory;

    private static final Logger LOGGER = Logger.getLogger(UserDao.class);
    private static final String SELECT_ALL_USERS_QUERY = "SELECT u FROM User u";

    public UserDao(EntityManagerFactory factory) {
        this.factory = factory;
        LOGGER.trace("create new instance of UserDao");
    }

    @Override
    public List<User> findAll() {
        LOGGER.info("search for all Users (without limits)");
        return ActionWrapper.wrap(factory, new User(), ActionWrapper.NO_LIMIT)
                .execute((manager, entity, limit) -> manager
                        .createQuery(SELECT_ALL_USERS_QUERY, User.class)
                        .setMaxResults(limit)
                        .getResultList());
    }

    @Override
    public List<User> findAll(int offset, int length) {
        if (offset < 0 || length < 0) {
            LOGGER.error("offset or/and length is less then 0");
            return null;
        }

        LOGGER.info("search for all Users (with limits)");
        return ActionWrapper.wrap(factory, new User(), length)
                .execute((manager, entity, limit) -> manager
                        .createQuery(SELECT_ALL_USERS_QUERY, User.class)
                        .setMaxResults(limit)
                        .setFirstResult(offset)
                        .getResultList());
    }

    @Override
    public User find(Integer integer) {
        if (integer == null || integer < 0) {
            LOGGER.error("integer is null or is less then 0");
            return null;
        }

        LOGGER.info("search for single User");
        return ActionWrapper.wrap(factory, new User())
                .execute((manager, entity) -> {
                    return manager.find(entity.getClass(), integer);
                });
    }

    @Override
    public User remove(Integer integer) {
        if (integer == null || integer < 0) {
            LOGGER.error("integer is null or is less then 0");
            return null;
        }

        LOGGER.info("remove single User");
        return ActionWrapper.wrap(factory, new User())
                .executeWithTransaction((manager, entity) -> {
                    User removed = manager.find(entity.getClass(), integer);
                    manager.remove(removed);
                    return removed;
                });
    }

    @Override
    public User update(User entity) {
        if (entity == null) {
            LOGGER.error("entity is null");
            return null;
        }

        LOGGER.info("update single User");
        return ActionWrapper.wrap(factory, entity)
                .executeWithTransaction((manager, wrapEntity) -> {
                    User old = manager.find(wrapEntity.getClass(), wrapEntity.getId()).clone();
                    manager.merge(entity);
                    return old;
                });
    }

    @Override
    public User create(User entity) {
        if (entity == null) {
            LOGGER.error("entity is null");
            return null;
        }

        LOGGER.info("create and save new User");
        ActionWrapper.wrap(factory, entity).executeWithTransaction(EntityManager::persist);

        LOGGER.info("new User was successfully created");
        return ActionWrapper.wrap(factory, entity)
                .execute((manager, wrapEntity) -> {
                    return manager.find(wrapEntity.getClass(), wrapEntity.getId());
                });
    }
}
