package com.itlize.project.repository;

import com.itlize.project.entity.Project;
import com.itlize.project.entity.ProjectToResource;
import com.itlize.project.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectToResourceRepository extends JpaRepository<ProjectToResource, Integer> {
    public List<ProjectToResource> findByProjectAndResource(Project project, Resource resource);
    public List<ProjectToResource> findByProject(Project project);
}