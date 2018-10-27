package model;


import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * User POJO model.
 */
@Entity
@Table(name = "users")
public class User extends Base {

    @Column
    private int age;

    @Column(precision = 2)
    private double salary;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "manage_id", referencedColumnName = "id")
    private User manage;

    @Column(name = "start_work_date")
    private LocalDateTime localDateTime;

    public User() {
    }

    public User(String name, int age, double salary, LocalDateTime localDateTime) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.localDateTime = localDateTime;
    }

    public User(String name, int age, double salary, Department department, City city) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.department = department;
        this.city = city;
    }

    public User(String name, int age, double salary, Department department, City city,
                LocalDateTime localDateTime) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.department = department;
        this.city = city;
        this.localDateTime = localDateTime;
    }

    public User(String name, int age, double salary, Department department, City city, User manage,
                LocalDateTime localDateTime) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.department = department;
        this.city = city;
        this.manage = manage;
        this.localDateTime = localDateTime;
    }



    public User(int id, String name, int age, double salary, Department department) {
        super(id, name);
        this.age = age;
        this.salary = salary;
        this.department = department;
    }

    public User(int id, String name, int age) {
        super(id, name);
        this.age = age;
    }

    public User(int id, String name, int age, double salary, Department department, City city) {
        super(id, name);
        this.age = age;
        this.salary = salary;
        this.department = department;
        this.city = city;
    }

    public User(int id, String name, int age, double salary, Department department, City city, User manage) {
        super(id, name);
        this.age = age;
        this.salary = salary;
        this.department = department;
        this.city = city;
        this.manage = manage;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public User getManage() {
        return manage;
    }

    public void setManage(User manage) {
        this.manage = manage;
    }

    /**
     * Not deep copy!
     */
    @Override
    public User clone() {
        return new User(id, name, age, salary, department, city, manage);
    }
}
