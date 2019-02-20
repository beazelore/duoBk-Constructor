package duobk_constructor.helpers;

import duobk_constructor.model.Task;

import java.util.Date;

public class TaskWithInfo {
    private Task task;
    private Date date;
    private String mail;

    public TaskWithInfo() {
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
