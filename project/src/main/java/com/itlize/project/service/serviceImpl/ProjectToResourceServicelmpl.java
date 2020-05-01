package com.itlize.project.service.serviceImpl;

import com.itlize.project.entity.Project;
import com.itlize.project.entity.ProjectToResource;
import com.itlize.project.entity.Resource;
import com.itlize.project.repository.ProjectRepository;
import com.itlize.project.repository.ProjectToResourceRepository;
import com.itlize.project.service.ProjectToResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectToResourceServicelmpl implements ProjectToResourceService {
    @Autowired
    private ProjectToResourceRepository projectToResourceRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public boolean create(ProjectToResource projectToResource, Project project, Resource resource) {
        RestTemplate restTemplate1=new RestTemplate();
        ServiceInstance serviceInstance=loadBalancerClient.choose("PRODUCT");
        if(projectToResource==null || project==null || resource==null)
            return false;
        if(get(project,resource)!=null){
            System.out.println("project has added the resource");
            return false;
        }
        try{
            projectToResourceRepository.save(projectToResource);
            project.addProjectToResource(projectToResource);
            resource.addProjects(projectToResource);
            projectRepository.save(project);
            String url=String.format("http://%s:%s",serviceInstance.getHost(),serviceInstance.getPort())+"/resource/saveResource?id="+resource.getId();
        }catch (Exception e){
            System.out.println("Sth wrong happens when creating " + projectToResource.toString());
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean delete(ProjectToResource projectToResource) {
        if(projectToResource==null){
            System.out.println("null input for deleting projectToResource");
        }
        System.out.println("deleting ptr: " +projectToResource.getId());
        try{

            projectToResourceRepository.delete(projectToResource);
        }catch (Exception e){
            System.out.println("Sth wrong happens when deleting " + projectToResource.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public ProjectToResource get(Integer id) {
        Optional<ProjectToResource> target = projectToResourceRepository.findById(id);
        if(target.isPresent()){
            return target.get();
        }
        return null;
    }

    @Override
    public ProjectToResource get(Project project, Resource resource) {
        List<ProjectToResource> res = projectToResourceRepository.findByProjectAndResource(project,resource);
        if(res.size()>0){
            return res.get(0);
        }
        return null;
    }

    @Override
    public List<ProjectToResource> get(Project project) {
        return projectToResourceRepository.findByProject(project);
    }

    @Override
    public void clear() {
        projectToResourceRepository.deleteAll();
    }
}