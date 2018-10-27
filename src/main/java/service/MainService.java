package service;

import exception.AppException;
import model.Department;
import model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Main Service interface. Describes all Application's business logic.
 */
public interface MainService {

    /**
     * Registers User to DB.
     *
     * @param user new User, needed to be registered.
     * @return registered User (with some updated fields), if operation was successful, or null, if it wasn't.
     * @throws AppException if operation isn't possible.
     */
    User register(User user) throws AppException;

    /**
     * Adds new Department to DB.
     *
     * @param department new Department, needed to be added.
     * @return added Department (with some updated fields), if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    Department addDepartment(Department department) throws AppException;

    /**
     * Updates existent User's data.
     *
     * @param user new version of User data, needed to be updated.
     * @return old version of User data, if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    User update(User user) throws AppException;

    /**
     * Removes existent User from DB.
     *
     * @param user existent User instance, needed to be removed.
     * @return removed User data, if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    User remove(User user) throws AppException;

    /**
     * Groups Users by their Departments into Lists.
     *
     * @return result Map, where keys - Departments and values - Lists of
     * Users with such departments, if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    Map<Department, List<User>> getUsersGroupByDepartment() throws AppException;

    /**
     * Counts average salare for each user group. Users are grouped by their Departments.
     *
     * @return result Map, where keys - Departments and values - average salary of each User group,
     * if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    Map<Department, Double> getAvgSalaryGroupByDepartment() throws AppException;

    /**
     * Groups Users by their "manager" fields and sorts by condition "if user lives in Kiev".
     *
     * @return result Map, where keys - Users-managers and values - Lists of Users sorted by mentioned condition,
     * if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    Map<User, List<User>> getUsersGroupByManagersAndOrderedThatLiveInKiev() throws AppException;

    /**
     * Searches Users by their names.
     *
     * @param name search key.
     * @return result List of Users with equal to key name, if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    List<User> findByName(String name) throws AppException;

    /**
     * Searches Users by their salary.
     *
     * @param minSal minimal required salary.
     * @param maxSal maximal required salary.
     * @return result List of Users with matching salary value, if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    List<User> findInRange(double minSal, double maxSal) throws AppException;

    /**
     * Searches Users by their start_work_date.
     *
     * @param start minimal required date.
     * @param end maximal required date.
     * @return result List of Users with matching start_work_date value, if operation was successful, or null, if it wasn't.
     * @throws AppException if operation can't be carried out.
     */
    List<User> findByDate(LocalDateTime start, LocalDateTime end) throws AppException;


}
