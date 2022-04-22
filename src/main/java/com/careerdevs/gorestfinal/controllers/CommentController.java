package com.careerdevs.gorestfinal.controllers;



/*

      Required Routes for GoRestSQL Final: complete for each resource; User, Post, Comment, Todo,

           * GET route that returns one [resource] by ID from the SQL database
           * GET route that returns all [resource]s stored in the SQL database
           * DELETE route that deletes one [resource] by ID from SQL database (returns the deleted SQL [resource] data)
           * DELETE route that deletes all [resource]s from SQL database (returns how many [resource]s were deleted)
           * POST route that queries one [resource] by ID from GoREST and saves their data to your local database (returns
           the SQL [resource] data)
           *POST route that uploads all [resource]s from the GoREST API into the SQL database (returns how many
           [resource]s were uploaded)
           *POST route that create a [resource] on JUST the SQL database (returns the newly created SQL [resource] data)
           *PUT route that updates a [resource] on JUST the SQL database (returns the updated SQL [resource] data)
    * */

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.validation.CommentValidation;
import com.careerdevs.gorestfinal.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllComments(){
        try{
            Iterable<Comment> allComments = commentRepository.findAll();
            return new ResponseEntity<>(allComments, HttpStatus.OK);
        }catch(Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneComment(@PathVariable ("id") String id){
        try{
            if(ApiErrorHandling.isStrNaN(id))
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + "is not valid ID");

                long uID = Integer.parseInt(id);
            Optional<Comment> foundComment = commentRepository.findById(uID);
            if(foundComment.isEmpty())

                throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Comment not found with id: " + id);
                return new ResponseEntity<>(foundComment,HttpStatus.OK);
        }catch(HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(),e.getStatusCode());

        }catch (Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable ("id") String id){
        try{
            if(ApiErrorHandling.isStrNaN(id)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,id + " is not valid");

            }
            long uID = Integer.parseInt(id);
            Optional<Comment> foundComment = commentRepository.findById(uID);
            if(foundComment.isEmpty()){
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Comment not found with id :" +id);

            }
            return new ResponseEntity<>(foundComment,HttpStatus.OK);


        }catch(HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(),e.getStatusCode());
        }catch(Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }
    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAllUser(){
        try{
            long totalComments = commentRepository.count();
            commentRepository.deleteAll();
            return new ResponseEntity<>("Comments Deleted: " + totalComments,HttpStatus.OK);

        }catch(HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(),e.getStatusCode());
        }catch(Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }
    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadCommentById(@PathVariable ("id") String id, RestTemplate restTemplate){
        try{

            if(ApiErrorHandling.isStrNaN(id)){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,id + " is not valid ID");

            }
            int uID = Integer.parseInt(id);
            //check the range
            String url = "https://gorest.co.in/public/v2/posts/" + uID;

            Comment foundComment = restTemplate.getForObject(url,Comment.class);

            System.out.println(foundComment);
            if(foundComment == null){
                throw  new HttpClientErrorException(HttpStatus.NOT_FOUND,"Post data was null.");
            }
            Comment saveComment = commentRepository.save(foundComment);
            return new ResponseEntity<>(saveComment,HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {
            //System.out.println("HTTP");
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            //System.out.println("GENERIC");
            return ApiErrorHandling.genericApiError(e);

        }
    }
    @PostMapping("/uploadall")
    public ResponseEntity<?> uploadAll(RestTemplate restTemplate){
        try{

            String url = "https://gorest.co.in/public/v2/comments/";
            ResponseEntity<Comment[]> response = restTemplate.getForEntity(url,Comment[].class);
            Comment[] firstPageComments = response.getBody();

            if(firstPageComments == null ){
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to get first page" + "comments from GoRest");
            }
            ArrayList<Comment> allComments = new ArrayList<>(Arrays.asList(firstPageComments));
            HttpHeaders responseHeaders = response.getHeaders();
            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            for(int i = 2;i < totalPgNum; i++){
                String pageUrl = url + "?page=" +i;
                Comment[] pageComments = restTemplate.getForObject(pageUrl,Comment[].class);

                if(pageComments == null){
                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to Get page " + i + "of users from GoRest");

                }
                    allComments.addAll(Arrays.asList(firstPageComments));
            }
            commentRepository.saveAll(allComments);
            return new ResponseEntity<>("Comments created: "+ allComments.size(),HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            //System.out.println("HTTP");
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            //System.out.println("GENERIC");
            return ApiErrorHandling.genericApiError(e);

        }
    }
    @PostMapping("/")
    public ResponseEntity<?> createComment(@RequestBody Comment newComment){
        try{
            ValidationError errors = CommentValidation.validateComment(newComment,commentRepository,false);
            if(errors.hasError()){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,errors.toJSONString());

            }
                Comment createdComment = commentRepository.save(newComment);
                return new ResponseEntity<>(createdComment,HttpStatus.CREATED);

        }catch(HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(),e.getStatusCode());
        }
        catch(Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }
    @PutMapping("/")
    public ResponseEntity<?> updateComment(@RequestBody Comment updateComment){
        try{
            ValidationError newCommentErrors = CommentValidation.validateComment(updateComment,commentRepository,true);
            if(newCommentErrors.hasError()){
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,newCommentErrors.toString());

            }
               Comment saveComment = commentRepository.save(updateComment);
                return new ResponseEntity<>(saveComment,HttpStatus.OK);
        }catch(HttpClientErrorException e){
            return ApiErrorHandling.customApiError(e.getMessage(),e.getStatusCode());
        }
        catch(Exception e){
            return ApiErrorHandling.genericApiError(e);
        }
    }

}
