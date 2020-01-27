package terminals.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id ")
    private int id;

    @Column(name = "department_name", length = 30, nullable = false, unique = true)
    private String department;

    public Department() {
    }

    public Department(String department) {
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(department, that.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(department);
    }
}
