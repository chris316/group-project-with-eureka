package com.itlize.project.controller;

import com.itlize.project.entity.*;
import com.itlize.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectToResourceService projectToResourceService;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    private User getCurrentUser(Principal principal) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        return userService.findByUsername(authenticationToken.getName());
    }

    @PostMapping("/addProject")
    public ResponseEntity<?> addProject(Principal principal,
                                        @RequestParam(name="projectName") String projectName){
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User user = userService.findByUsername(authenticationToken.getName());
        Project projectToAdd = new Project();
        projectToAdd.setProjectName(projectName);
        boolean isSuccessful = projectService.create(projectToAdd,user);
        if(!isSuccessful){
            return new ResponseEntity<>("{\"error\":\"sth wrong happens when creating new project!\"}",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(projectToAdd,HttpStatus.OK);
    }

    @PostMapping("/deleteProject")
    public ResponseEntity<?> deleteProject(Principal principal,
                                           @RequestParam(name="projectId") Integer projectId){
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User user = userService.findByUsername(authenticationToken.getName());
        Project projectToDelete=projectService.get(projectId);
        boolean isSuccessful=projectService.delete(projectToDelete);
        if(!isSuccessful){
            return new ResponseEntity<>("{\"error\":\"sth wrong happens when deleting project!\"}",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(projectToDelete,HttpStatus.OK);
    }

    @GetMapping("/read")
    public ResponseEntity<?> read(Principal principal,
                                  @RequestParam(name = "projectId")Integer projectId){
        //--------------user validation
        User user = getCurrentUser(principal);


        Project project = projectService.get(projectId);
        if(project == null){
            return new ResponseEntity<>("{\"error\":\"project not found!\"}", HttpStatus.BAD_REQUEST);
        }
        if(!user.getUsername().equals(project.getOwner().getUsername())){
            return new ResponseEntity<>("{\"error\":\"project does not belong to the user\"}",HttpStatus.BAD_REQUEST);
        }
        //--------------end of validation

        String body = projectService.toJson(projectId);
        return new ResponseEntity<>(String.format("{\"tableDetail\":%s}", body), HttpStatus.OK);
    }

    @PostMapping("/addResource")
    public ResponseEntity<?> addResource(Principal principal,
                                         @RequestParam(name = "projectId")Integer projectId,
                                         @RequestParam(name = "resourceId")Integer resourceId){
        RestTemplate restTemplate1=new RestTemplate();
        ServiceInstance serviceInstance=loadBalancerClient.choose("PRODUCT");

        //--------------user validation
        User user = getCurrentUser(principal);

        Project project = projectService.get(projectId);
        if(project == null){
            return new ResponseEntity<>("{\"error\":\"project not found!\"}",HttpStatus.BAD_REQUEST);
        }
        if(!user.getUsername().equals(project.getOwner().getUsername())){
            return new ResponseEntity<>("{\"error\":\"project does not belong to the user\"}",HttpStatus.BAD_REQUEST);
        }
        //--------------end of validation

        String url=String.format("http://%s:%s",serviceInstance.getHost(),serviceInstance.getPort())+"/resource/readId?id="+resourceId;
        Resource resource=restTemplate1.getForObject(url,Resource.class);
        if(resource==null){
            return new ResponseEntity<>("{\"error\":\"resource does not exist!\"}",HttpStatus.BAD_REQUEST);
        }

        ProjectToResource ptr = new ProjectToResource();
        boolean isSuccessful = projectToResourceService.create(ptr,project,resource);
        if(!isSuccessful){
            return new ResponseEntity<>("{\"error\":\"sth wrong happens when linking resource to current project!\"}",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ptr,HttpStatus.OK);
    }

    @PostMapping("/deleteResource")
    public ResponseEntity<?> deleteResource(Principal principal,
                                            @RequestParam(name = "projectId")Integer projectId,
                                            @RequestParam(name = "resourceId")Integer resourceId){
        RestTemplate restTemplate1=new RestTemplate();
        ServiceInstance serviceInstance=loadBalancerClient.choose("PRODUCT");
        //--------------user validation
        User user = getCurrentUser(principal);

        Project project = projectService.get(projectId);
        if(project == null){
            return new ResponseEntity<>("{\"error\":\"project not found!\"}",HttpStatus.BAD_REQUEST);
        }
        if(!user.getUsername().equals(project.getOwner().getUsername())){
            return new ResponseEntity<>("{\"error\":\"project does not belong to the user\"}",HttpStatus.BAD_REQUEST);
        }
        //--------------end of validation

        String url=String.format("http://%s:%s",serviceInstance.getHost(),serviceInstance.getPort())+"/resource/readId?id="+resourceId;
        Resource resource=restTemplate1.getForObject(url,Resource.class);
        if(resource==null){
            return new ResponseEntity<>("{\"error\":\"resource does not exist!\"}",HttpStatus.BAD_REQUEST);
        }

        ProjectToResource ptr = projectToResourceService.get(project,resource);
        boolean isSuccessful = projectToResourceService.delete(ptr);
        if(!isSuccessful){
            return new ResponseEntity<>("{\"error\":\"sth wrong happens when linking resource to current project!\"}",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ptr,HttpStatus.OK);
    }

    @PostMapping("/addTemplate")
    public ResponseEntity<?> addTemplate(Principal principal,
                                         @RequestParam(name = "projectId")Integer projectId,
                                         @RequestParam(name="columnType") String columnType,
                                         @RequestParam(name="formula") String formula) {
        //--------------user validation
        User user = getCurrentUser(principal);

        Project project = projectService.get(projectId);
        if(project == null){
            return new ResponseEntity<>("{\"error\":\"project not found!\"}",HttpStatus.BAD_REQUEST);
        }
        if(!user.getUsername().equals(project.getOwner().getUsername())){
            return new ResponseEntity<>("{\"error\":\"project does not belong to the user\"}",HttpStatus.BAD_REQUEST);
        }
        //--------------end of validation


        //set type by columnType param
        ProjectColumnEnum type = columnType.equals("Number") ? ProjectColumnEnum.NUMBER : (columnType.equals("Text") ? ProjectColumnEnum.TEXT : (columnType.equals("Formula") ? ProjectColumnEnum.FORMULA : null));
        if (type == null) {
            return new ResponseEntity<>("{\"error\":\"Type not recognized\"}", HttpStatus.BAD_REQUEST);
        }
        boolean isSuccessful = projectService.update(project,formula);
        if (!isSuccessful) {
            return new ResponseEntity<>("{\"error\":\"Sth wrong happens when creating new column for current project! Check if column name is taken\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PostMapping("/deleteTemplate")
    public ResponseEntity<?> deleteTemplate(Principal principal,
                                            @RequestParam(name = "projectId")Integer projectId){
        //--------------user validation
        User user = getCurrentUser(principal);

        Project project = projectService.get(projectId);
        if(project == null){
            return new ResponseEntity<>("{\"error\":\"project not found!\"}",HttpStatus.BAD_REQUEST);
        }
        if(!user.getUsername().equals(project.getOwner().getUsername())){
            return new ResponseEntity<>("{\"error\":\"project does not belong to the user\"}",HttpStatus.BAD_REQUEST);
        }
        //--------------end of validation

        boolean isSuccessful=projectService.deleteTemplate(project);
        if (!isSuccessful) {
            return new ResponseEntity<>("{\"error\":\"Sth wrong happens when deleting the column\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PostMapping("/updateTemplate")
    public ResponseEntity<?> updateTemplate(Principal principal,
                                            @RequestParam(name = "projectId")Integer projectId,
                                            @RequestParam(name="columnType") String columnType,
                                            @RequestParam(name="formula") String formula) {
        //--------------user validation
        User user = getCurrentUser(principal);

        Project project = projectService.get(projectId);
        if(project == null){
            return new ResponseEntity<>("{\"error\":\"project not found!\"}",HttpStatus.BAD_REQUEST);
        }
        if(!user.getUsername().equals(project.getOwner().getUsername())){
            return new ResponseEntity<>("{\"error\":\"project does not belong to the user\"}",HttpStatus.BAD_REQUEST);
        }
        //--------------end of validation


        //set type by columnType param
        ProjectColumnEnum type = columnType.equals("Number") ? ProjectColumnEnum.NUMBER : (columnType.equals("Text") ? ProjectColumnEnum.TEXT : (columnType.equals("Formula") ? ProjectColumnEnum.FORMULA : null));
        if (type == null) {
            return new ResponseEntity<>("{\"error\":\"Type not recognized\"}", HttpStatus.BAD_REQUEST);
        }
        //update info
        boolean isSuccessful=projectService.update(project,formula);
        if (!isSuccessful) {
            return new ResponseEntity<>("{\"error\":\"Sth wrong happens when creating new column for current project! Check if column name is taken\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

}