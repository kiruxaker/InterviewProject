package model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Department POJO model.
 */
@Entity
@Table(name = "departments")
public class Department extends Base {

    public Department() {
    }

    public Department(String name) {
        this.name = name;
    }


    public Department(int id, String name) {
        super(id, name);
    }

    /**
     * Not deep copy!
     */
    @Override
    public Department clone() {
        return new Department(id, name);
    }
}
