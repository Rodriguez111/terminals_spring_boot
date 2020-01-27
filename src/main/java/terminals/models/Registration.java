package terminals.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private int recordId;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "terminal_id", nullable = false)
    private Terminal terminal;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "admin_gave_id", nullable = false)
    private User adminGaveOut;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "admin_received_id")
    private User adminGot;


    @Column(name = "record_start_date", length = 19, nullable = false)
    private String startDate; //6

    @Column(name = "record_finish_date", length = 19, columnDefinition = "varchar(19) default ''")
    private String endDate; //7


    public Registration() {
    }

    public Registration(Terminal terminal, User user, User adminGaveOut) {
        this.terminal = terminal;
        this.user = user;
        this.adminGaveOut = adminGaveOut;
        this.startDate = startDate;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAdminGaveOut() {
        return adminGaveOut;
    }

    public void setAdminGaveOut(User adminGaveOut) {
        this.adminGaveOut = adminGaveOut;
    }

    public User getAdminGot() {
        return adminGot;
    }

    public void setAdminGot(User adminGot) {
        this.adminGot = adminGot;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return recordId == that.recordId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }
}