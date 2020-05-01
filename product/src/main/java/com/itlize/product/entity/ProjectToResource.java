package com.itlize.product.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class ProjectToResource {
    @Id
    @GeneratedValue
    private Integer id;

    @CreatedDate
    private Date time_created;

    @ManyToOne(targetEntity = Project.class,cascade = CascadeType.DETACH)
    private Project project;

    @ManyToOne(targetEntity = Resource.class,cascade = CascadeType.DETACH)
    private Resource resource;

    @Override
    public String toString() {
        return "ProjectToResource{" +
                "id=" + id +
                ", project=" + project +
                ", resource=" + resource +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\":\"" + id +
                "\", \"project\":\"" + project +
                "\", \"resource\":\"" + resource +
                "\"}";
    }

    public ProjectToResource() {
    }
    public ProjectToResource(Project projectId, Resource resourceId) {
        this.project = projectId;
        this.resource = resourceId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project projectId) {
        this.project = projectId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resourceId) {
        this.resource = resourceId;
    }


}