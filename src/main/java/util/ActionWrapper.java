package util;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;

public class ActionWrapper {

    private static final Logger LOGGER = Logger.getLogger(ActionWrapper.class);

    public static final int NO_LIMIT = Integer.MAX_VALUE;

    public static<E> WrappedStatement<E> wrap(EntityManagerFactory factory, E entity) {
        LOGGER.trace("create new instance of WrappedStatement");

        return new WrappedStatement<>(factory, entity);
    }

    public static<E> GroupWrappedStatement<E> wrap(EntityManagerFactory factory, E entity, int groupResultLimit) {
        LOGGER.trace("create new instance of GroupWrappedStatement");

        int limit = groupResultLimit == -1 ? Integer.MAX_VALUE : groupResultLimit;

        return new GroupWrappedStatement<>(factory, entity, limit);
    }

    public static class WrappedStatement<T> {

        private EntityManagerFactory factory;
        private T entity;

        WrappedStatement(EntityManagerFactory factory, T entity) {
            LOGGER.trace("was created new instance of WrappedStatement (entity type: " +
                    entity.getClass().getSimpleName() + ")");

            this.factory = factory;
            this.entity = entity;
        }

        public T execute(Function<T> function) {
            if (!factory.isOpen()) {
                LOGGER.error("factory is closed");
                return null;
            }

            EntityManager manager = factory.createEntityManager();

            try {
                T executionResult = function.execute(manager, entity);

                LOGGER.info("executed logic of " + function);
                return executionResult;
            } catch (Exception e) {
                LOGGER.error("can't execute logic of " + function, e);
            } finally {
                manager.close();
            }
            return null;
        }

        public void execute(Procedure<T> procedure) {
            if (!factory.isOpen()) {
                LOGGER.error("factory is closed");
            }

            EntityManager manager = factory.createEntityManager();

            try {
                procedure.execute(manager, entity);

                LOGGER.info("executed logic of " + procedure);
            } catch (Exception e) {
                LOGGER.error("can't execute logic of " + procedure, e);
            } finally {
                manager.close();
            }
        }

        public T executeWithTransaction(Function<T> function) {
            if (!factory.isOpen()) {
                LOGGER.error("factory is closed");
                return null;
            }

            EntityManager manager = factory.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();

            try {
                transaction.begin();
                T result = function.execute(manager, entity);
                transaction.commit();

                LOGGER.info("executed logic of " + function);
                return result;
            } catch (Exception e) {
                LOGGER.error("can't execute logic of " + function, e);
                transaction.rollback();
            } finally {
                manager.close();
            }
            return null;
        }

        public void executeWithTransaction(Procedure<T> procedure) {
            if (!factory.isOpen()) {
                LOGGER.error("factory is closed");
            }

            EntityManager manager = factory.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();

            try {
                transaction.begin();
                procedure.execute(manager, entity);
                transaction.commit();

                LOGGER.info("executed logic of " + procedure);
            } catch (Exception e) {
                LOGGER.error("can't execute logic of " + procedure, e);
                transaction.rollback();
            } finally {
                manager.close();
            }
        }
    }

    public static class GroupWrappedStatement<T> {

        private EntityManagerFactory factory;
        private T entity;
        private int limit;

        GroupWrappedStatement(EntityManagerFactory factory, T entity, int limit) {
            LOGGER.trace("was created new instance of GroupWrappedStatement (entity type: " +
                    entity.getClass().getSimpleName() + ")");

            this.factory = factory;
            this.entity = entity;
            this.limit = limit;
        }

        public List<T> execute(GroupFunction<T> groupFunction) {
            if (!factory.isOpen()) {
                LOGGER.error("factory is closed");
                return null;
            }

            EntityManager manager = factory.createEntityManager();

            try {
                List<T> executionResult = groupFunction.execute(manager, entity, limit);

                LOGGER.info("executed logic of " + groupFunction);
                return executionResult;
            } catch (Exception e) {
                LOGGER.error("can't execute logic of " + groupFunction, e);
            } finally {
                manager.close();
            }
            return null;
        }

        public List<T> executeWithTransaction(GroupFunction<T> groupFunction) {
            if (!factory.isOpen()) {
                LOGGER.error("factory is closed");
                return null;
            }

            EntityManager manager = factory.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();

            try {
                transaction.begin();
                List<T> result = groupFunction.execute(manager, entity, limit);
                transaction.commit();

                LOGGER.info("executed logic of " + groupFunction);
                return result;
            } catch (Exception e) {
                LOGGER.error("can't execute logic of " + groupFunction, e);
                transaction.rollback();
            } finally {
                manager.close();
            }
            return null;
        }
    }

    public interface Function<T> {
        T execute(EntityManager manager, T entity);
    }

    public interface GroupFunction<T> {
        List<T> execute(EntityManager manager, T entity, int limit);
    }

    public interface Procedure<T> {
        void execute(EntityManager manager, T entity);
    }
}
