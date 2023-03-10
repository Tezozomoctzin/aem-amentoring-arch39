package com.mentorting.aem.core.services;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Component(
        service = WorkflowProcess.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Retouched Metadata Workflow Process",
                Constants.SERVICE_VENDOR + "=Mentoring",
                "process.label=Retouched Workflow Process Metadata value setting"
        })
public class RetouchedWorkflowProcess implements WorkflowProcess {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String RETOUCHED_PROPERTY_NAME = "retouched";
    public static final String FORMAT_PROPERTY_NAME = "dc:format";
    public static final String JPEG_FORMAT = "image/jpeg";

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        String payload = workItem.getWorkflowData().getPayload().toString();
        String baseAssetPath = payload.split("/renditions")[0];

        ModifiableValueMap assetMetadata = Optional
                .ofNullable(workflowSession.adaptTo(ResourceResolver.class))
                .map(resolver -> resolver
                        .getResource(baseAssetPath + "/jcr:content/metadata"))
                .map(resource -> resource.adaptTo(ModifiableValueMap.class))
                .orElseThrow(() -> new WorkflowException("Metadata not found"));
        String assetFormat = assetMetadata.getOrDefault(FORMAT_PROPERTY_NAME, null).toString();
        if (assetFormat == null || !assetFormat.equals(JPEG_FORMAT)) {
            return;
        }
        assetMetadata.put(RETOUCHED_PROPERTY_NAME, "no");
    }
}
