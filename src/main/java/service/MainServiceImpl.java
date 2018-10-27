package service;

import dao.Dao;
import exception.AppException;
import model.Department;
import model.User;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of Main Service interface. Contains implementation of all business logic.
 * @see MainService
 */
public class MainServiceImpl implements MainService {

    private static final Logger LOGGER = Logger.getLogger(MainServiceImpl.class);

    private Dao<User, Integer> userDao;
    private Dao<Department, Integer> departmentDao;

    public MainServiceImpl(Dao<User, Integer> userDao, Dao<Department, Integer> departmentDao) {
        this.userDao = userDao;
        this.departmentDao = departmentDao;
    }

    @Override
    public User register(User user) throws AppException {
        if (user == null) {
            throw new AppException("can't register, User is null");
        }

        User createdUser = userDao.create(user);

        if (createdUser == null) {
            LOGGER.error("new User wasn't registered");
        } else {
            LOGGER.info("new User was successfully registered");
        }

        return createdUser;
    }

    @Override
    public Department addDepartment(Department department) throws AppException {
        if (department == null) {
            throw new AppException("can't add, Department is null");
        }

        Department createdDepartment = departmentDao.create(department);

        if (createdDepartment == null) {
            LOGGER.error("new Department wasn't added");
        } else {
            LOGGER.info("new Department was successfully added");
        }

        return createdDepartment;
    }

    @Override
    public User update(User user) throws AppException {
        if (user == null) {
            throw new AppException("can't update, User is null");
        }

        User oldUser = userDao.update(user);

        if (oldUser == null) {
            LOGGER.error("existent User wasn't added");
        } else {
            LOGGER.info("existent User was successfully added");
        }

        return oldUser;
    }

    @Override
    public User remove(User user) throws AppException {
        if (user == null) {
            throw new AppException("can't remove, User is null");
        }

        User removedUser = userDao.remove(user.getId());

        if (removedUser == null) {
            LOGGER.error("existent User wasn't removed");
        } else {
            LOGGER.info("existent User was successfully removed");
        }

        return removedUser;
    }

    @Override
    public Map<Department, List<User>> getUsersGroupByDepartment() throws AppException {
        List<User> allUsers = userDao.findAll();
        if (allUsers == null || allUsers.size() < 1) {
            LOGGER.error("can't group Users by Department, no Users were found");
            return null;
        }

        Map<Department, List<User>> departmentListMap = allUsers.stream()
                .collect(Collectors.groupingBy(User::getDepartment, Collectors.toList()));

        if (departmentListMap == null) {
            LOGGER.error("Users weren't grouped by Department");
        } else {
            LOGGER.info("Users were successfully grouped by Department");
        }

        return departmentListMap;
    }

    @Override
    public Map<Department, Double> getAvgSalaryGroupByDepartment() throws AppException {
        List<User> allUsers = userDao.findAll();
        if (allUsers == null || allUsers.size() < 1) {
            LOGGER.error("can't get Users' average salary and group by Department, no Users were found");
            return null;
        }

        Map<Department, Double> departmentListMap = allUsers.stream()
                .collect(Collectors.groupingBy(
                        User::getDepartment,
                        Collectors.averagingDouble(User::getSalary)
                ));

        if (departmentListMap == null) {
            LOGGER.error("Users' average salaries weren't grouped by Department");
        } else {
            LOGGER.info("Users were successfully grouped by Department");
        }

        return departmentListMap;
    }

    @Override
    public Map<User, List<User>> getUsersGroupByManagersAndOrderedThatLiveInKiev() throws AppException {
        List<User> allUsers = userDao.findAll();
        if (allUsers == null || allUsers.size() < 1) {
            LOGGER.error("can't group Users by managers and order by living in Kiev, no Users were found");
            return null;
        }

        Map<User, List<User>> departmentListMap = allUsers.stream()
                .filter(user -> user.getManage() != null)
                .collect(Collectors.groupingBy(
                        User::getManage,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            list.sort((el1, el2) ->
                                    "Kiev".equals(el1.getCity().getName()) ? -1 :
                                            "Kiev".equals(el2.getCity().getName()) ? 1 :
                                                    el2.getCity().getName().compareTo(el1.getCity().getName()));
                            return list;
                        })));

        if (departmentListMap == null) {
            LOGGER.error("Users weren't grouped by Department");
        } else {
            LOGGER.info("Users were successfully grouped by Department");
        }

        return departmentListMap;
    }

    @Override
    public List<User> findByName(String name) throws AppException {
        if (name == null) {
            throw new AppException("can't start search by name, name is null");
        }

        List<User> allUsers = userDao.findAll();
        if (allUsers == null || allUsers.size() < 1) {
            LOGGER.error("can't search by name, no Users were found");
            return null;
        }

        List<User> resultList = allUsers.stream()
                .filter(user -> name.equals(user.getName()))
                .collect(Collectors.toList());

        if (resultList == null || resultList.size() < 1) {
            LOGGER.error("Users with equal name doesn't exist");
        } else {
            LOGGER.info("Users with equal name were successfully found");
        }

        return resultList;
    }

    @Override
    public List<User> findInRange(double minSal, double maxSal) throws AppException {
        if (minSal < 0 || maxSal < 0 || minSal > maxSal) {
            throw new AppException("can't start search in range, minSal(" +
                    minSal + ") and/or maxSel(" + maxSal + ") is/are wrong");
        }

        List<User> allUsers = userDao.findAll();
        if (allUsers == null || allUsers.size() < 1) {
            LOGGER.error("can't search in range, no Users were found");
            return null;
        }

        List<User> resultList = allUsers.stream()
                .filter(user -> user != null && user.getSalary() >= minSal && user.getSalary() <= maxSal)
                .collect(Collectors.toList());

        if (resultList == null || resultList.size() < 1) {
            LOGGER.error("Users with matching salary doesn't exist");
        } else {
            LOGGER.info("Users with matching salary were successfully found");
        }

        return resultList;
    }

    @Override
    public List<User> findByDate(LocalDateTime start, LocalDateTime end) throws AppException {
        if (start == null || end == null || start.isAfter(end)) {
            throw new AppException("can't start search by date, start or/and end is/are null, or start is after end");
        }

        List<User> allUsers = userDao.findAll();
        if (allUsers == null || allUsers.size() < 1) {
            LOGGER.error("can't search by date, no Users were found");
            return null;
        }

        List<User> resultList = allUsers.stream()
                .filter(user -> user != null &&
                        !user.getLocalDateTime().isBefore(start) &&
                        !user.getLocalDateTime().isAfter(end))
                .collect(Collectors.toList());

        if (resultList == null || resultList.size() < 1) {
            LOGGER.error("Users with matching start_work_date doesn't exist");
        } else {
            LOGGER.info("Users with matching start_work_date were successfully found");
        }

        return resultList;
    }
}
