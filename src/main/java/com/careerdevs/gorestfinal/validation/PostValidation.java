package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;

import java.util.Optional;

public class PostValidation {

    public static ValidationError validatePost(Post post, PostRepository postRepo,
                                               UserRepository userRepo,
                                               boolean isUpdate){


        ValidationError errors = new ValidationError();


        //validated data for post
        if(isUpdate){
            if(post.getId() == 0){
                errors.addError("id","ID can not be left blank");
            }else{
                Optional<Post> foundPost = postRepo.findById(post.getId());
                if(foundPost.isEmpty()){
                    errors.addError("id","No user found with the id:" + post.getId());
                }
            }
        }
        String postTitle = post.getTitle();
        String postBody = post.getBody();
        long postUserId = post.getUser_id();

        if(postTitle == null || postTitle.trim().equals("")){
            errors.addError("Title","Title can not be left blank");
        }
        if(postBody == null || postBody.trim().equals("")){
            errors.addError("Body","Body cannot be left blank");
        }
        if(postUserId == 0){
            errors.addError("User_id","User_id cannot be left blank");
        }else{
            //is the postUserId connected to an existing user
            Optional<User> foundUser = userRepo.findById(postUserId);
            if(foundUser.isEmpty()){
                errors.addError("user_id","User_id is invalid because there is no user found with the id :" + postUserId);
            }

        }

        return errors;

    }
}
