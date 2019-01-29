package duobk_constructor.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table (name = "user")
public class User {
    public User() {
    }

    public User(String mail, String userType) {
        this.mail = mail;
        this.userType = userType;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Column(name = "mail")
    private String mail;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Column(name = "user_type")
    private String userType;
}
