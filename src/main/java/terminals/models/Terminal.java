package terminals.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "terminals")
public class Terminal implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terminal_id ")
    private int id;

    @Column(name = "terminal_reg_id", length = 10, nullable = false, unique = true)
    private String regId;

    @Column(name = "terminal_model", length = 20, nullable = false)
    private String terminalModel;

    @Column(name = "terminal_serial_id", length = 30, nullable = false, unique = true)
    private String serialId;

    @Column(name = "terminal_inventory_id", length = 20, nullable = false, unique = true)
    private String inventoryId;

    @Column(name = "terminal_comment", length = 500)
    private String terminalComment;

    @Column(name = "terminal_is_active", nullable = false)
    private boolean terminalIsActive;


    @ManyToOne
    @JoinColumn(name = "terminal_department_id")
    private Department department;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "terminal_create_date", length = 19, nullable = false)
    private String createDate;

    @Column(name = "terminal_update_date", length = 19)
    private String lastUpdateDate;

    public Terminal() {
    }

    public Terminal(String regId, String serialId, String inventoryId) {
        this.regId = regId;
        this.serialId = serialId;
        this.inventoryId = inventoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getTerminalModel() {
        return terminalModel;
    }

    public void setTerminalModel(String terminalModel) {
        this.terminalModel = terminalModel;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getTerminalComment() {
        return terminalComment;
    }

    public void setTerminalComment(String terminalComment) {
        this.terminalComment = terminalComment;
    }

    public boolean isTerminalIsActive() {
        return terminalIsActive;
    }

    public void setTerminalIsActive(boolean terminalIsActive) {
        this.terminalIsActive = terminalIsActive;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Terminal terminal = (Terminal) o;
        return terminalIsActive == terminal.terminalIsActive &&
                Objects.equals(regId, terminal.regId) &&
                Objects.equals(terminalModel, terminal.terminalModel) &&
                Objects.equals(serialId, terminal.serialId) &&
                Objects.equals(inventoryId, terminal.inventoryId) &&
                Objects.equals(terminalComment, terminal.terminalComment) &&
                Objects.equals(department, terminal.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regId, terminalModel, serialId, inventoryId, terminalComment, terminalIsActive, department);
    }

    @Override
    public Terminal clone() {
        Terminal terminal = null;
        try {
            terminal = (Terminal) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return terminal;
    }
}
