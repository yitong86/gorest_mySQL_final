package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.Todo;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.repositories.TodoRepository;

public class TodoValidation {
    public static ValidationError validateTodo(Todo todo, TodoRepository todoRepo,boolean isUpdate){
        ValidationError errors = new ValidationError();
        return errors;
    }
}

