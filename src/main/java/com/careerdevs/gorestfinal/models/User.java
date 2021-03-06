package com.careerdevs.gorestfinal.models;




import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


//this entity annotation is meant for the repository to recognize this class as a class,
// that can be user to make a table
@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)

    private long id;


    private String name;
    private String email;
    private String gender;
    private String status;


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
