package duobk_constructor.helpers;

import duobk_constructor.model.Task;

public class TaskWithMail {

    private Task task;

    private String mail;

    public TaskWithMail(Task task, String mail) {
        this.task = task;
        this.mail = mail;
    }

    public TaskWithMail() {
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
