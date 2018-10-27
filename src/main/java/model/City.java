package model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * City POJO model.
 */
@Entity
@Table(name = "cities")
public class City extends Base {

    public City() {
    }

    public City(String name) {
        this.name = name;
    }

    public City(int id, String name) {
        super(id, name);
    }
}
