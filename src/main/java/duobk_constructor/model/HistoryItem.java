package duobk_constructor.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "history_item")
public class HistoryItem {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "task_id")
    private Integer taskId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "status_before")
    private String statusBefore;
    @Column(name = "status_after")
    private String statusAfter;
    @Column(name = "explanation")
    private String explanation;
    @Column(name = "message")
    private String message;
    @Column(name = "moment", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date moment;

    public HistoryItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatusBefore() {
        return statusBefore;
    }

    public void setStatusBefore(String statusBefore) {
        this.statusBefore = statusBefore;
    }

    public String getStatusAfter() {
        return statusAfter;
    }

    public void setStatusAfter(String statusAfter) {
        this.statusAfter = statusAfter;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }
}
