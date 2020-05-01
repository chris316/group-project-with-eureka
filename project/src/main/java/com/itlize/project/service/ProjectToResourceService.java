package com.itlize.project.service;

import com.itlize.project.entity.Project;
import com.itlize.project.entity.ProjectToResource;
import com.itlize.project.entity.Resource;

import java.util.List;

public interface ProjectToResourceService {
    public boolean create(ProjectToResource projectToResource, Project project, Resource resource);
    public boolean delete(ProjectToResource projectToResource);
    public ProjectToResource get(Integer id);
    public ProjectToResource get(Project project, Resource resource);
    public List<ProjectToResource> get(Project project);
    public void clear();


}