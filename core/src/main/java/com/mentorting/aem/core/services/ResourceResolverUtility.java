package com.mentorting.aem.core.services;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;

public class ResourceResolverUtility {

    public static final String VERSIONING_SERVICE = "versioningservice";

    public static ResourceResolver getResourceResolver(ResourceResolverFactory resourceResolverFactory) throws LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, VERSIONING_SERVICE);
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

}
