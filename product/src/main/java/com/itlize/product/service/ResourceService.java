package com.itlize.product.service;

import com.itlize.product.entity.Resource;

import java.util.List;

public interface ResourceService {
    public boolean create(Resource resource);
    public boolean delete(Resource resource);
    public boolean update(Resource resource);
    public Resource get(Integer id);
    public List<Resource> getAll();
    public String toJson(Integer id);
    public String toJson(Resource resource);
    public String getAllJson();
    public void clear();
}