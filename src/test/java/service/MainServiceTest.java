package service;

import dao.Dao;
import dao.DepartmentDao;
import dao.UserDao;
import exception.AppException;
import model.City;
import model.Department;
import model.User;
import util.ActionWrapper;
import org.junit.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class MainServiceTest {

    private static final String H2_PERSISTENCE_UNIT = "hibernate-h2-unit";
//    public static final String MYSQL_PERSISTENCE_UNIT = "hibernate-mysql-unit";

    public static final String SELECT_ALL_USERS_QUERY = "SELECT u from User u";
    public static final String SELECT_ALL_DEPARTMENTS_QUERY = "SELECT d from Department d";
    public static final String SELECT_ALL_CITIES_QUERY = "SELECT c from City c";

    private static EntityManagerFactory factory;

    private static Dao<User, Integer> userDao;
    private static Dao<Department, Integer> departmentDao;
    private static MainService mainService;

    private List<City> testCityList;
    private List<Department> testDepartmentList;
    private List<User> testUserList;

    @BeforeClass
    public static void setUpClass() {
        factory = Persistence.createEntityManagerFactory(H2_PERSISTENCE_UNIT);
//        factory = Persistence.createEntityManagerFactory(MYSQL_PERSISTENCE_UNIT);
        userDao = new UserDao(factory);
        departmentDao = new DepartmentDao(factory);
        mainService = new MainServiceImpl(userDao, departmentDao);
    }

    @AfterClass
    public static void tearDownClass() {
        factory.close();
    }

    @Before
    public void setUp() throws Exception {

        testCityList = new ArrayList<>();
        testCityList.add(new City("Kiev"));
        testCityList.add(new City("New York"));
        testCityList.add(new City("London"));

        ActionWrapper.wrap(factory, new City()).executeWithTransaction(((manager, entity) -> {
            testCityList.forEach(manager::persist);
        }));

        testDepartmentList = new ArrayList<>();
        testDepartmentList.add(new Department("IT"));
        testDepartmentList.add(new Department("Logistics"));
        testDepartmentList.add(new Department("Security"));

        ActionWrapper.wrap(factory, new Department()).executeWithTransaction(((manager, entity) -> {
            testDepartmentList.forEach(manager::persist);
        }));

        testUserList = new ArrayList<>();
        testUserList.add(new User("Mr.1", 21, 5000,
                testDepartmentList.get(0), testCityList.get(0),
                LocalDateTime.of(1990, 10, 10, 0, 0)));
        testUserList.add(new User("Mr.2", 22, 4000,
                testDepartmentList.get(1), testCityList.get(1), testUserList.get(0),
                LocalDateTime.of(1995, 10, 10, 0, 0)));
        testUserList.add(new User("Mr.3", 23, 3000,
                testDepartmentList.get(2), testCityList.get(0), testUserList.get(0),
                LocalDateTime.of(2000, 10, 10, 0, 0)));
        testUserList.add(new User("Mr.4", 24, 2000,
                testDepartmentList.get(1), testCityList.get(1), testUserList.get(1),
                LocalDateTime.of(1996, 10, 10, 0, 0)));
        testUserList.add(new User("Mr.5", 25, 1000,
                testDepartmentList.get(0), testCityList.get(2), testUserList.get(1),
                LocalDateTime.of(1991, 10, 10, 0, 0)));

        ActionWrapper.wrap(factory, new User()).executeWithTransaction(((manager, entity) -> {
            testUserList.forEach(manager::persist);
        }));
    }

    @After
    public void tearDown() throws Exception {
        deleteAllTableRecords(User.class, new User(), SELECT_ALL_USERS_QUERY);
        deleteAllTableRecords(Department.class, new Department(), SELECT_ALL_DEPARTMENTS_QUERY);
        deleteAllTableRecords(City.class, new City(), SELECT_ALL_CITIES_QUERY);
    }



    // ---------- TESTS ----------

    @Test
    public void register() throws Exception {
        User newUser = new User("Alex", 35, 500,
                testDepartmentList.get(0), testCityList.get(0), testUserList.get(0),
                LocalDateTime.of(2018, 6, 1, 0, 0));
        User registeredUser = mainService.register(newUser);

        testUserList.add(newUser);
        List<User> actualUserList = userDao.findAll();

        assertEquals(newUser, registeredUser);
        assertEquals(testUserList, actualUserList);
    }

    @Test(expected = AppException.class)
    public void registerWithException() throws Exception {
        mainService.register(null);
    }

    @Test
    public void addDepartment() throws Exception {
        Department newDepartment = new Department("History");
        Department addedDepartment = mainService.addDepartment(newDepartment);

        testDepartmentList.add(newDepartment);
        List<Department> actualDepartmentList = departmentDao.findAll();

        assertEquals(newDepartment, addedDepartment);
        assertEquals(testDepartmentList, actualDepartmentList);
    }

    @Test(expected = AppException.class)
    public void addDepartmentWithException() throws Exception {
        mainService.addDepartment(null);
    }

    @Test
    public void update() throws Exception {
        User expectedNewUser = testUserList.get(0);
        expectedNewUser.setName("BlaBla");
        User expectedOldUser = testUserList.get(0).clone();

        User actualOldUser = mainService.update(expectedNewUser);
        User actualNewUser = userDao.find(expectedNewUser.getId());

        assertEquals(expectedOldUser, actualOldUser);
        assertEquals(expectedNewUser, actualNewUser);
        assertEquals(testUserList, userDao.findAll());
    }

    @Test(expected = AppException.class)
    public void updateWithException() throws Exception {
        mainService.update(null);
    }

    @Test
    public void remove() throws Exception {
        User expectedRemovedUser = testUserList.get(testUserList.size() - 1).clone();
        User actualRemovedUser = mainService.remove(testUserList.get(testUserList.size() - 1));

        testUserList.remove(testUserList.size() - 1);

        assertEquals(testUserList, userDao.findAll());
        assertEquals(expectedRemovedUser, actualRemovedUser);
    }

    @Test(expected = AppException.class)
    public void removeWithException() throws Exception {
        mainService.remove(null);
    }

    @Test
    public void getUsersGroupByDepartment() throws Exception {
        List<User> firstDepartmentList = new ArrayList<>();
        firstDepartmentList.add(testUserList.get(0));
        firstDepartmentList.add(testUserList.get(4));

        List<User> secondDepartmentList = new ArrayList<>();
        secondDepartmentList.add(testUserList.get(1));
        secondDepartmentList.add(testUserList.get(3));

        List<User> thirdDepartmentList = new ArrayList<>();
        thirdDepartmentList.add(testUserList.get(2));

        Map<Department, List<User>> expectedDepartmentMap = new HashMap<>();
        expectedDepartmentMap.put(testDepartmentList.get(0), firstDepartmentList);
        expectedDepartmentMap.put(testDepartmentList.get(1), secondDepartmentList);
        expectedDepartmentMap.put(testDepartmentList.get(2), thirdDepartmentList);

        Map<Department, List<User>> actualDepartmentMap = mainService.getUsersGroupByDepartment();

        assertEquals(expectedDepartmentMap, actualDepartmentMap);
    }

    @Test
    public void getUsersGroupByDepartmentFromEmptyDB() throws Exception {
        deleteAllTableRecords(User.class, new User(), SELECT_ALL_USERS_QUERY);
        assertNull(mainService.getUsersGroupByManagersAndOrderedThatLiveInKiev());
    }

    @Test
    public void getAvgSalaryGroupByDepartment() throws Exception {
        double firstAvg = 0;
        firstAvg += testUserList.get(0).getSalary();
        firstAvg += testUserList.get(4).getSalary();
        firstAvg /= 2;

        double secondAvg = 0;
        secondAvg += testUserList.get(1).getSalary();
        secondAvg += testUserList.get(3).getSalary();
        secondAvg /= 2;

        double thirdAvg = testUserList.get(2).getSalary();

        Map<Department, Double> expectedDepartmentMap = new HashMap<>();
        expectedDepartmentMap.put(testDepartmentList.get(0), firstAvg);
        expectedDepartmentMap.put(testDepartmentList.get(1), secondAvg);
        expectedDepartmentMap.put(testDepartmentList.get(2), thirdAvg);

        Map<Department, Double> actualDepartmentMap = mainService.getAvgSalaryGroupByDepartment();

        assertEquals(expectedDepartmentMap, actualDepartmentMap);
    }

    @Test
    public void getAvgSalaryGroupByDepartmentFromEmptyDB() throws Exception {
        deleteAllTableRecords(User.class, new User(), SELECT_ALL_USERS_QUERY);
        assertNull(mainService.getAvgSalaryGroupByDepartment());
    }

    @Test
    public void getUsersGroupByManagersAndOrderedThatLiveInKiev() throws Exception {
        List<User> firstManagerList = new ArrayList<>();
        firstManagerList.add(testUserList.get(2));
        firstManagerList.add(testUserList.get(1));

        List<User> secondManagerList = new ArrayList<>();
        secondManagerList.add(testUserList.get(3));
        secondManagerList.add(testUserList.get(4));

        Map<User, List<User>> expectedDepartmentMap = new HashMap<>();
        expectedDepartmentMap.put(testUserList.get(0), firstManagerList);
        expectedDepartmentMap.put(testUserList.get(1), secondManagerList);

        Map<User, List<User>> actualDepartmentMap =
                mainService.getUsersGroupByManagersAndOrderedThatLiveInKiev();

        assertEquals(expectedDepartmentMap, actualDepartmentMap);
    }

    @Test
    public void getUsersGroupByManagersAndOrderedThatLiveInKievFromEmptyDB() throws Exception {
        deleteAllTableRecords(User.class, new User(), SELECT_ALL_USERS_QUERY);
        assertNull(mainService.getUsersGroupByManagersAndOrderedThatLiveInKiev());
    }

    @Test
    public void findByName() throws Exception {
        String testSearchedName = testUserList.get(0).getName();

        List<User> expectedList = new ArrayList<>();

        expectedList.add(userDao.create(new User(testSearchedName, 40, 111, LocalDateTime.now())));
        expectedList.add(testUserList.get(0));
        expectedList.add(userDao.create(new User(testSearchedName, 50, 222, LocalDateTime.now())));

        List<User> actualList = mainService.findByName(testSearchedName);

        expectedList.sort(Comparator.comparingInt(User::getId));
        actualList.sort(Comparator.comparingInt(User::getId));

        assertEquals(expectedList, actualList);
    }

    @Test(expected = AppException.class)
    public void findByNameWithException() throws Exception {
        mainService.findByName(null);
    }

    @Test
    public void findInRange() throws Exception {
        double testSearchedSalary = testUserList.get(0).getSalary();

        List<User> expectedList = new ArrayList<>();

        expectedList.add(userDao.create(
                new User("Name1", 40, testSearchedSalary + 50, LocalDateTime.now())));
        expectedList.add(userDao.create(
                new User("Name2", 50, testSearchedSalary - 50, LocalDateTime.now())));
        expectedList.add(testUserList.get(0));

        List<User> actualList =
                mainService.findInRange(testSearchedSalary - 100, testSearchedSalary + 100);

        expectedList.sort(Comparator.comparingInt(User::getId));
        actualList.sort(Comparator.comparingInt(User::getId));

        assertEquals(expectedList, actualList);
    }

    @Test(expected = AppException.class)
    public void findInRangeWithLeftException() throws Exception {
        mainService.findInRange(-5, 5);
    }

    @Test(expected = AppException.class)
    public void findInRangeWithRightException() throws Exception {
        mainService.findInRange(-10, -5);
    }

    @Test(expected = AppException.class)
    public void findInRangeWithInversionException() throws Exception {
        mainService.findInRange(10, 5);
    }

    @Test
    public void findByDate() throws Exception {
        LocalDateTime testSearchedDateTime = testUserList.get(0).getLocalDateTime();

        List<User> expectedList = new ArrayList<>();

        expectedList.add(testUserList.get(0));
        expectedList.add(userDao.create(
                new User("Name1", 40, 111, testSearchedDateTime.minusDays(1))));
        expectedList.add(userDao.create(
                new User("Name2", 50, 222, testSearchedDateTime.plusDays(1))));

        List<User> actualList =
                mainService.findByDate(testSearchedDateTime.minusDays(1), testSearchedDateTime.plusDays(1));

        expectedList.sort(Comparator.comparingInt(User::getId));
        actualList.sort(Comparator.comparingInt(User::getId));

        assertEquals(expectedList, actualList);
    }

    @Test(expected = AppException.class)
    public void findByDateWithLeftException() throws Exception {
        mainService.findByDate(null, LocalDateTime.now());
    }

    @Test(expected = AppException.class)
    public void findByDateWithRightException() throws Exception {
        mainService.findByDate(LocalDateTime.now(), null);
    }

    @Test(expected = AppException.class)
    public void findByDateWithInversionException() throws Exception {
        mainService.findByDate(LocalDateTime.MAX, LocalDateTime.MIN);
    }

    private <T> void deleteAllTableRecords(Class<T> entityType, T entity, String specialQuery) {
        ActionWrapper.wrap(factory, entity)
                .executeWithTransaction(((manager, wrapEntity) -> {
                    List<T> entityList = manager.createQuery(specialQuery, entityType).getResultList();
                    entityList.forEach(manager::remove);
                }));
    }

}