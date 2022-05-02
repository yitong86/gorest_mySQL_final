package com.careerdevs.gorestfinal.controllers;


import com.careerdevs.gorestfinal.models.Todo;
import com.careerdevs.gorestfinal.repositories.TodoRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.validation.TodoValidation;
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
@RequestMapping("/api/todos")
public class TodoController {
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTodos() {
        try {
            Iterable<Todo> allTodos = todoRepository.findAll();
            return new ResponseEntity<>(allTodos, HttpStatus.OK);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneTodo(@PathVariable("id") String id) {
        try {
            if (ApiErrorHandling.isStrNaN(id))
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not valid ID.");

            long uID = Integer.parseInt(id);

            Optional<Todo> foundTodo = todoRepository.findById(uID);
            if (foundTodo.isEmpty())
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Todo not found with ID:" + id);
            return new ResponseEntity<>(foundTodo, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id) {
        try {
            if (ApiErrorHandling.isStrNaN(id))
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + "is not valid ID.");
            long uID = Integer.parseInt(id);
            Optional<Todo> foundTodo = todoRepository.findById(uID);
            if (foundTodo.isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Todo is not found with ID: " + id);
            }
            return new ResponseEntity<>(foundTodo, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAllUser() {
        try {
            long totalTodos = todoRepository.count();
            todoRepository.deleteAll();
            return new ResponseEntity<>("Todos Deleted: " + totalTodos, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadTodoById(@PathVariable("id") String id,
                                            RestTemplate restTemplate) {
        try {
            if (ApiErrorHandling.isStrNaN(id)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + "is not valid ID");
            }
            int uID = Integer.parseInt(id);
            String url = "https://gorest.co.in/public/v2/posts/" + uID;
            Todo foundTodo = restTemplate.getForObject(url, Todo.class);
            System.out.println(foundTodo);
            if (foundTodo == null) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "TOdo data was not found with ID: " + id);

            }
            Todo saveTodo = todoRepository.save(foundTodo);
            return new ResponseEntity<>(saveTodo, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @PostMapping("/uploadall")
    public ResponseEntity<?> uploadAll(RestTemplate restTemplate) {
        try {
            String url = "https://gorest.co.in/public/v2/posts/";
            ResponseEntity<Todo[]> response = restTemplate.getForEntity(url, Todo[].class);
            Todo[] firstPageTodos = response.getBody();

            if (firstPageTodos == null) {
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get first page" + "todos from gorest.");
            }
            ArrayList<Todo> allTodos = new ArrayList<>(Arrays.asList(firstPageTodos));
            HttpHeaders responseHeaders = response.getHeaders();
            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            for (int i = 2; i <= totalPgNum; i++) {
                String pageUrl = url + "?page=" + i;
                Todo[] pageTodos = restTemplate.getForObject(pageUrl, Todo[].class);

                if (pageTodos == null) {
                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to Get page " + i + "of todos from GoRest");
                }
                allTodos.addAll(Arrays.asList(firstPageTodos));
            }
            todoRepository.saveAll(allTodos);
            return new ResponseEntity<>("Todo created: " + allTodos.size(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createTodo(@RequestBody Todo newTodo) {
        try {
            ValidationError errors = TodoValidation.validateTodo(newTodo, todoRepository, userRepository,false);
            if (errors.hasError()) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }
            Todo createTodo = todoRepository.save(newTodo);
            return new ResponseEntity<>(createTodo, HttpStatus.CREATED);
        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateTodo(@RequestBody Todo updateTodo) {
        try {
            ValidationError newTodoErrors = TodoValidation.validateTodo(updateTodo,todoRepository,userRepository,true);
            if(newTodoErrors.hasError()){
                throw  new HttpClientErrorException(HttpStatus.BAD_REQUEST,newTodoErrors.toJSONString());

            }
            Todo saveTodo = todoRepository.save(updateTodo);
            return new ResponseEntity<>(saveTodo,HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }


}
