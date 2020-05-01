package com.itlize.project.service.serviceImpl;

import com.itlize.project.entity.Project;
import com.itlize.project.entity.ProjectToResource;
import com.itlize.project.entity.User;
import com.itlize.project.repository.ProjectRepository;
import com.itlize.project.repository.UserRepository;
import com.itlize.project.service.ProjectService;
import com.itlize.project.service.ProjectToResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService{


        @Autowired
        private ProjectRepository projectRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private ProjectToResourceService projectToResourceService;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

        @Override
        public boolean create(Project project, User user) {
            if(project==null || user ==null) {
                System.out.println("null input!");
                return false;
            }
            Project target = get(project.getId());
            if(target!=null){
                System.out.println("project already exists");
                return false;
            }
            try{
                projectRepository.save(project);

                user.addProjects(project);
                projectRepository.save(project);
                userRepository.save(user);
            }catch (Exception e){
                System.out.println("Sth wrong happens when creating: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            return  true;
        }


        @Override
        @Transactional
        public boolean delete(Project project) {
            if(project==null){
                System.out.println("null input");
                return false;
            }
            System.out.println("deleting project: " +project.getId());

            try{

                projectRepository.delete(project);
            }catch (Exception e){
                System.out.println("Sth wrong happens when deleting");
                e.printStackTrace();
                return false;
            }
            return  true;
        }

        @Override
        public Project get(Integer id) {
            if(id==null)
                return null;
            Optional<Project> res = projectRepository.findById(id);
            if(res.isPresent()){
                return res.get();
            }
            return null;
        }

        @Override
        public String toJson(Integer id) {
            RestTemplate restTemplate1=new RestTemplate();
            ServiceInstance serviceInstance=loadBalancerClient.choose("PRODUCT");

            Project project = get(id);
            if(id==null||project==null)
                return "{\"error\":\"Project does not exist!\"}";
            List<ProjectToResource> resources = projectToResourceService.get(project);
            List<String> resourceJsons = new ArrayList<String>();
            for(ProjectToResource ptr : resources){
                String url=String.format("http://%s:%s",serviceInstance.getHost(),serviceInstance.getPort())+"/resource/getJson?id="+ptr.getResource().getId();
                String resourceJson=restTemplate1.getForObject(url,String.class);
                resourceJsons.add(resourceJson);
            }
            return "[" + String.join("", resourceJsons) + "]";
        }

        @Override
        public boolean update(Project project,String formula){
            project.setFormulaColumn(formula);
            projectRepository.save(project);
            return true;
        }

        @Override
        public boolean deleteTemplate(Project project){
            project.setFormulaColumn("");
            projectRepository.save(project);
            return true;
        }

        @Override
        public void clear() {
            projectRepository.deleteAll();
        }
}
