package com.itlize.project.service;

import com.itlize.project.entity.Project;
import com.itlize.project.entity.User;

public interface ProjectService {
    public boolean create(Project project, User user);
    public boolean delete(Project project);
    public boolean update(Project project,String formula);
    public boolean deleteTemplate(Project project);
    public Project get(Integer id);
    public String toJson(Integer id);
    public void clear();
}
