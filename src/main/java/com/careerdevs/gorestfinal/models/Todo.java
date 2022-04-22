package com.careerdevs.gorestfinal.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Todo {

//    id":1552," +
//            ""user_id":3194," +
//            ""title":"Fugit totus thymbra defaeco sunt aiunt."," +
//        ""due_on":"2022-05-13T00:00:00.000+05:30"," +
//            ""status":"completed"

    @Id
    @GeneratedValue
    private long id;

    private long user_id;
    private String title;



    private String due_on;
    private String status;

    public long getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDue_on() {
        return due_on;
    }

    public String getStatus() {
        return status;
    }


    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", due_on='" + due_on + '\'' +
                ", status='" + status + '\'' +
                '}';
    }




}
