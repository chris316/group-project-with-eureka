package com.itlize.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Collection;
import java.util.HashSet;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class Project {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(targetEntity = User.class)
    private User owner;
    private String projectName;

    @CreatedDate
    private Date timeCreated;
    @LastModifiedDate
    private Date lastUpdated;

    private String formulaColumn;

    @JsonIgnore
    @OneToMany(targetEntity = ProjectToResource.class,cascade = CascadeType.REMOVE, mappedBy = "project")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<ProjectToResource> resources = new HashSet<ProjectToResource>();


    public String toJson() {
        return "{" +
                "\"id\" : \"" + id +
                "\", \"projectName\":\"" + projectName + '"' +
                '}';
    }

    public Collection<ProjectToResource> getResources() {
        return resources;
    }
    public void addProjectToResource(ProjectToResource vResource){
        if(resources.contains(vResource)){
            return ;
        }
        resources.add(vResource);
        vResource.setProject(this);
    }
    public void removeProjectToResource(ProjectToResource vResource){
        if(!resources.contains(vResource)){
            return ;
        }
        resources.remove(vResource);
        vResource.setProject(null);
    }


    public String getFormulaColumn(){return formulaColumn;}
    public void setFormulaColumn(String formulaColumn){
        this.formulaColumn=formulaColumn;
    }

    public Project() {
    }

    public Project(User owner, String projectName) {
        this.owner = owner;
        this.projectName = projectName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}