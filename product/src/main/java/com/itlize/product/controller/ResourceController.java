package com.itlize.product.controller;

import com.itlize.product.entity.Resource;
import com.itlize.product.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @GetMapping("/read")
    public ResponseEntity<?> read(){
        String body = resourceService.getAllJson();
        return new ResponseEntity<>(String.format("{\"resourceInfo\":%s", body), HttpStatus.OK);
    }

    @GetMapping("/readId")
    public Resource readId(@RequestParam(name="id")Integer id){
        return resourceService.get(id);
    }

    @GetMapping("/readResource")
    public String readResource(@RequestParam(name="id")Integer id){
        return resourceService.get(id).toString();
    }

    @GetMapping("/saveResouce")
    public boolean saveResource(@RequestParam(name="id")Integer id){
        Resource resource=resourceService.get(id);
        return resourceService.create(resource);
    }

    @GetMapping("/getJson")
    public String getJson(@RequestParam(name="id")Integer id){
        Resource resource=resourceService.get(id);
        return resourceService.toJson(resource);
    }

    @PostMapping("/addResource")
    public ResponseEntity<?> addResource(@RequestParam(name="resourceName")String resourceName,@RequestParam(name="columnValue")String columnValue){
        Resource resourceToAdd = new Resource(resourceName,columnValue);
        resourceService.create(resourceToAdd);
        return new ResponseEntity<>(resourceToAdd, HttpStatus.OK);
    }

    @PostMapping("/deleteResource")
    public ResponseEntity<?> deleteResource(@RequestParam(name = "resourceId")Integer resourceId){
        Resource resourceToDelete = resourceService.get(resourceId);
        if(resourceToDelete==null){
            return new ResponseEntity<>("{\"error\":\"resource does not exist!\"}",HttpStatus.BAD_REQUEST);
        }
        boolean isSuccessful = resourceService.delete(resourceToDelete);
        return new ResponseEntity<>(resourceToDelete, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> setEntry(@RequestParam(name = "resourceId")Integer resourceId,
                                      @RequestParam(name = "resourceName")String resourceName,
                                      @RequestParam(name = "value") String value){
        Resource resource = resourceService.get(resourceId);
        if(resource==null){
            return new ResponseEntity<>("{\"error\":\"resource does not exist!\"}",HttpStatus.BAD_REQUEST);
        }
        resource.setColumnValue(value);
        resource.setResourceName(resourceName);
        boolean isSuccessful = resourceService.update(resource);
        if(isSuccessful){
            return new ResponseEntity<>(resource,HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"error\":\"sth wrong happens:(\"}",HttpStatus.BAD_REQUEST);
    }
}
