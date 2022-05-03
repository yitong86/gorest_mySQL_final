package com.careerdevs.gorestfinal.validation;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValidationError {
    //key always String
    //store all our errors
    private final HashMap<String, String> errors = new HashMap<>();
    //add errors to errors hashmap
    public void addError (String key, String errorMsg) {
        //add a new key value pairs through the put method
        errors.put(key, errorMsg);
    }

    public boolean hasError() {
        //check whether there are any errors during the process of validating this data
        return errors.size() != 0;
    }
    //turn it  into Jsonn
   // @Override
//    public String toString(){
//        String errorMessage = "ValidationError:\n";
//        for(Map.Entry<String, String> err : errors.entrySet()){
//            errorMessage +=err.getKey()+": " + err.getValue() + "\n";
//        }
//    }
    @Override
    public String toString() {
        StringBuilder errorMessage = new StringBuilder("ValidationError:\n");
        for (Map.Entry<String, String> err : errors.entrySet()) {
            errorMessage.append(err.getKey()).append(": ").append(err.getValue()).append("\n");
        }
        return errorMessage.toString();
    }

    public String toJSONString (){
        return new JSONObject(errors).toString();
    }

}