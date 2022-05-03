package com.careerdevs.gorestfinal.models;

import javax.persistence.*;
import java.util.UUID;


//The default SQL configuration for a String is VARCHAR(255), which means a character limit of 255. GoRest Post.body has a max limit of 500 so importing posts from GoRest will fail if we do not modify this default. Take the following steps to fix this issue
//        Open your Post.java file within models and find the ‘body’ field
//        add the following annotation above the field:
//@Column(length = 512)
//Open your MySQL CLI and access the gorestfinal database by using “USE gorestfinal;”
//        Then run the command “DROP TABLE post”
//        Then restart/start your SpringBoot server
//        Now your post table will be able to take in strings with a character length max of 512
@Entity
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long user_id;

    private String title;

    @Column(length = 512)
    private String body;

    public long getId() {
        return id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getUser_id() {
        return user_id;
    }



    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }




}
