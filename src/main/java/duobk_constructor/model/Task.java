package duobk_constructor.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table (name = "task")
public class Task {
    public Task() {
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "unprocessed")
    private String unprocessed;
    @Column(name = "processed")
    private String processed;
    @Column(name = "status")
    private String status;
    @Column(name = "result")
    private String result;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "book_id")
    private Integer bookId;
    @Column(name = "entry1_id")
    private Integer entry1_id;
    @Column(name = "entry2_id")
    private Integer entry2_id;
    @Column(name = "name")
    private String name;
    @Column(name = "bad")
    private String bad;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnprocessed() {
        return unprocessed;
    }

    public void setUnprocessed(String unprocessed) {
        this.unprocessed = unprocessed;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getEntry1_id() {
        return entry1_id;
    }

    public void setEntry1_id(Integer entry1_id) {
        this.entry1_id = entry1_id;
    }

    public Integer getEntry2_id() {
        return entry2_id;
    }

    public void setEntry2_id(Integer entry2_id) {
        this.entry2_id = entry2_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBad() {
        return bad;
    }

    public void setBad(String bad) {
        this.bad = bad;
    }
}
