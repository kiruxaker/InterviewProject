package dao;

import java.util.List;

/**
 * @param <K> type of id (KEY) field of stored entity.
 * @param <V> type of stored entity (VALUE).
 *
 * @author kirillparolys
 */
public interface Dao<V,K> {

    /**
     * Searches for all stored entity instances.
     */
    List<V> findAll();

    /**
     * Searches for all stored entity instances.
     * Shows limited result of search.
     *
     * @param offset starting position for limited result List (all elements before offset won't be included in the result List).
     * @param length number of elements after offset (including offset), which will be included into result List.
     */
    List<V> findAll(int offset, int length);

    /**
     * Searches for stored entity instance by its ID.
     *
     * @param id id of an instance we're trying to find.
     * @return needed instance, if operation was successful, or null, if it wasn't or if such instance was not stored to DB.
     */
    V find(K id);

    /**
     * Removes from DB stored entity instance by its ID.
     *
     * @param id id of an instance we're trying to find.
     * @return removed instance, if operation was successful, or null if it wasn't or if such instance was not stored to DB.
     */
    V remove(K id);

    /**
     * Updates stored entity instance.
     * Note: it won't create new instance (will return null instead), it can only update existent one!
     *
     * @param entity new variant of stored instance.
     * @return old variant of instance, if operation was successful, or null if it wasn't or if such instance was not stored to DB.
     */
    V update(V entity);

    /**
     * Creates new entity instance (stores it to DB).
     *
     * @param entity new instance, which will be stored.
     * @return stored variant of instance (with assigned ID field), if operation was successful, or null, if it wasn't.
     */
    V create(V entity);

}
