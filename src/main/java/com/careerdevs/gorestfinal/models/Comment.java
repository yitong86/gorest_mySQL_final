package com.careerdevs.gorestfinal.models;

import javax.persistence.*;


@Entity
public class Comment {
//"id":1602,
// "post_id":1614,
//        "name":"Gopaal Butt",
//        "email":"gopaal_butt@collins.biz",
//        "body":"Expedita et quia."


    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private long id;

    private long post_id;

    private String name;
    private String email;
    @Column(length = 512)
    private String body;

    public long getId() {
        return id;
    }

    public long getPost_id() {
        return post_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", post_id=" + post_id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", body='" + body + '\'' +
                '}';
    }


}
