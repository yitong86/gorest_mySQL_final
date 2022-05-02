package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.Todo;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.repositories.TodoRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;

import java.util.Optional;

public class TodoValidation {
    public static ValidationError validateTodo(Todo todo, TodoRepository todoRepo, UserRepository userRepo,boolean isUpdate){
        ValidationError errors = new ValidationError();

        //validated data for post
        if(isUpdate){
            if(todo.getId() == 0){
                errors.addError("id","ID can not be left blank");
            }else{
                Optional<Todo> foundUser = todoRepo.findById(todo.getId());
                if(foundUser.isEmpty()){
                    errors.addError("id","No user found with the id:" + todo.getId());
                }
            }
        }
        String todoTitle = todo.getTitle();
        String todoStatus = todo.getStatus();
        long todoUserId = todo.getUser_id();

        if(todoTitle == null || todoTitle.trim().equals("")){
            errors.addError("title","Title can not be left blank");
        }
        if(todoStatus == null || todoStatus.trim().equals("")){
            errors.addError("body","body cannot be left blank");
        }
        if(todoUserId == 0){
            errors.addError("user_id","User_id cannot be left blank");
        }else{
            //is the postUserId connected to an existing user
            Optional<User> foundUser = userRepo.findById(todoUserId);
            if(foundUser.isEmpty()){
                errors.addError("user_id","User_id is invalid because there is no user found with the id :" + todoUserId);
            }

        }

        return errors;

    }
}
