package com.itlize.product.service.serviceImpl;

import com.itlize.product.entity.Resource;
import com.itlize.product.repository.ResourceRepository;
import com.itlize.product.service.ResourceService;
import com.itlize.product.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public boolean create(Resource resource) {
        if(resource==null){
            System.out.println("null input detected");
            return false;
        }
        System.out.println("Adding "+ resource.toString());
        if(resource.getId()!=null) {
            Optional<Resource> target = resourceRepository.findById(resource.getId());
            if (target.isPresent()) {
                System.out.println("resource exist:" + resource.toString());
                return false;
            }
        }
        try{
            resourceRepository.save(resource);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }
        System.out.println("resource added.");
        return true;
    }

    @Override
    @Transactional
    public boolean delete(Resource resource) {
        if(resource==null){
            System.out.println("deleting null resource");
        }
        System.out.println("deleting resource: " +resource.getId());
        try{

            resourceRepository.delete(resource);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public Resource get(Integer id) {
        Optional<Resource> target = resourceRepository.findById(id);
        if(target.isPresent()){
            return target.get();
        }else{
            return null;
        }
    }

    @Override
    public List<Resource> getAll() {
        return resourceRepository.findAll();
    }

    @Override
    public String toJson(Integer id) {
        Resource resource=resourceRepository.findById(id).get();
        return resource.toString();
    }


    @Override
    public String toJson(Resource resource) {
        return resource.toString();
    }


    @Override
    public String getAllJson() {
        List<Resource> resources = getAll();
        List<String> resourceJsons = new ArrayList<String>();
        for(Resource resource : resources){
            resourceJsons.add(toJson(resource));
        }
        return "[" + String.join(",", resourceJsons) + "]";
    }

    @Override
    public void clear() {
        resourceRepository.deleteAll();
    }


    @Override
    public boolean update(Resource resource) {
        try {
            resourceRepository.save(resource);
        }catch (Exception e){
            System.out.println("Sth wrong happens when updating:" + resource.toString());
            return false;
        }
        return true;
    }
}
