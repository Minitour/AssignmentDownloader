package mobi.newsound.model;

import com.google.gson.annotations.Expose;

import java.util.Arrays;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class Participant {

    @Expose
    private int id;


    @Expose
    private String firstname;

    @Expose
    private String lastname;

    @Expose
    private String fullname;

    @Expose
    private String email;

    @Expose
    private String idnumber;

    @Expose
    private Role[] roles;




    public static class Role {
        @Expose
        private int roleid;

        @Expose
        private String name;

        @Expose
        private String shortname;

        public int getRoleid() {
            return roleid;
        }

        public String getName() {
            return name;
        }

        public String getShortname() {
            return shortname;
        }
    }

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public Role[] getRoles() {
        return roles;
    }

    public int getMainRole() {
        return roles.length > 0 ? roles[0].getRoleid() : 0;
    }

    public String searchableValue(){
        return id + " " + firstname + " " + lastname + " " + fullname + " " + email + " " + idnumber + " " + Arrays.toString(roles);
    }

}
