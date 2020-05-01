package com.itlize.project.entity;

import java.net.URI;
import java.util.Map;

public interface ServiceInstance {
    String getServiceId();

    String getHost();

    int getPort();

    boolean isSecure();

    URI getUri();

    Map<String,String> getMetaData();
}
