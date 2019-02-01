package duobk_constructor.helpers;

import java.util.ArrayList;

public class IndexesForm {

ArrayList<Integer> start1;
ArrayList<Integer> start2;
ArrayList<Integer> end1;
ArrayList<Integer> end2;
Integer taskId;

    public IndexesForm() {
    }

    public ArrayList<Integer> getStart1() {
        return start1;
    }

    public void setStart1(ArrayList<Integer> start1) {
        this.start1 = start1;
    }

    public ArrayList<Integer> getStart2() {
        return start2;
    }

    public void setStart2(ArrayList<Integer> start2) {
        this.start2 = start2;
    }

    public ArrayList<Integer> getEnd1() {
        return end1;
    }

    public void setEnd1(ArrayList<Integer> end1) {
        this.end1 = end1;
    }

    public ArrayList<Integer> getEnd2() {
        return end2;
    }

    public void setEnd2(ArrayList<Integer> end2) {
        this.end2 = end2;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}