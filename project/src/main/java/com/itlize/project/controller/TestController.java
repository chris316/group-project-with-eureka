package com.itlize.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    //test function to see if project microservice can connect to resource service to get a resource
    @GetMapping("/getResourceById")
    public String getMessage(@RequestParam(name="resourceId")Integer resourceId){

        RestTemplate restTemplate1=new RestTemplate();
        ServiceInstance serviceInstance=loadBalancerClient.choose("PRODUCT");
        String url=String.format("http://%s:%s",serviceInstance.getHost(),serviceInstance.getPort())+"/resource/readResource?id="+resourceId;
        String response=restTemplate1.getForObject(url,String.class);
        return response;
    }


}
