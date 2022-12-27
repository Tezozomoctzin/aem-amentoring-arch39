package com.mentorting.aem.core.services;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.*;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Retouched Metadata Workflow Process",
                Constants.SERVICE_VENDOR + "=Mentoring",
                "process.label=Retouched Workflow Process Metadata value setting"
        })
public class RetouchedWorkflowProcess implements WorkflowProcess {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    public static final String RETOUCHED = "retouched";
    public static final String JPEG_FORMAT = "image/jpeg";

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = ResourceResolverUtility.getResourceResolver(resourceResolverFactory);
            String payload = workItem.getWorkflowData().getPayload().toString();
            Resource resource = resourceResolver.getResource(payload + "/jcr:content/metadata");
            if(resource == null){
                logger.error("Resource is null");
                return;
            }
            ModifiableValueMap resourceMetadata = resource.adaptTo(ModifiableValueMap.class);
            if(resourceMetadata == null){
                logger.error("Resource Metadata is null");
                return;
            }
            String format = resourceMetadata.get("dc:format", String.class);
            if(JPEG_FORMAT.equals(format)) {
                System.out.println("Setting retouched metadata to true");
                resourceMetadata.put(RETOUCHED, "no");
                resourceResolver.commit();
            }
        } catch (LoginException | PersistenceException e) {
            throw new RuntimeException(e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

    }
}
