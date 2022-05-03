package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.repositories.PostRepository;

import java.util.Optional;

public class CommentValidation {



    public static ValidationError validateComment(Comment comment, CommentRepository commentRepo, PostRepository postRepo,boolean isUpdate){
        ValidationError errors = new ValidationError();

        //validated data for post
        if(isUpdate){
            if(comment.getId() == 0){
                errors.addError("id","ID can not be left blank");
            }else{
                Optional<Comment> foundUser = commentRepo.findById(comment.getId());
                if(foundUser.isEmpty()){
                    errors.addError("id","No user found with the id:" + comment.getId());
                }
            }
        }
        String commentName = comment.getName();
        String commentEmail = comment.getEmail();
        long commentPostId  = comment.getPost_id();
        String commentBody = comment.getBody();

        if(commentName == null || commentName.trim().equals("")){
            errors.addError("Name ","Name  can not be left blank");
        }
        if(commentEmail == null || commentEmail.trim().equals("")){
            errors.addError("Email","Email cannot be left blank");
        }
        if(commentBody == null || commentBody.trim().equals("")){
            errors.addError("Body","Body cannot be left blank");
        }
        if(commentPostId == 0){
            errors.addError("PostId ","PostId  cannot be left blank");
        }else{
            //is the commentPostId connected to an existing user
            Optional<Post> foundUser = postRepo.findById(commentPostId);
            if(foundUser.isEmpty()){
                errors.addError("user_id","User_id is invalid because there is no user found with the id :" + commentPostId);
            }

        }

        return errors;

    }
}