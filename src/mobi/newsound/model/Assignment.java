package mobi.newsound.model;

import com.google.gson.annotations.Expose;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class Assignment {

    @Expose
    private int id;

    @Expose
    private int cmid;

    @Expose
    private int course;

    @Expose
    private String name;

    @Expose
    private long allowsubmissionsfromdate;

    @Expose
    private long duedate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCmid() {
        return cmid;
    }

    public void setCmid(int cmid) {
        this.cmid = cmid;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAllowsubmissionsfromdate() {
        return allowsubmissionsfromdate;
    }

    public void setAllowsubmissionsfromdate(long allowsubmissionsfromdate) {
        this.allowsubmissionsfromdate = allowsubmissionsfromdate;
    }

    public long getDuedate() {
        return duedate;
    }

    public void setDuedate(long duedate) {
        this.duedate = duedate;
    }

    private String convertTime(long time){
        Date date = new Date(time * 1000);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(date);
    }

    public String getStringDueDate(){
        return convertTime(duedate);
    }

    public String getStringStartDate(){
        return convertTime(allowsubmissionsfromdate);
    }


    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", cmid=" + cmid +
                ", course=" + course +
                ", name='" + name + '\'' +
                ", allowsubmissionsfromdate=" + allowsubmissionsfromdate +
                ", duedate=" + duedate +
                '}';
    }
}
