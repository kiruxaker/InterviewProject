package model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base abstract POJO model (parent to other Entities).
 */
@MappedSuperclass
public abstract class Base {

    @Id
    @GeneratedValue
    protected int id;

    @Column(nullable = false)
    protected String name;

    public Base() {
    }

    public Base(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Base base = (Base) o;

        return id == base.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
