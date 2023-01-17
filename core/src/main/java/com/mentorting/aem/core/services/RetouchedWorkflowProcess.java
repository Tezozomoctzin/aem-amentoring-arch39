package com.mentorting.aem.core.services;

import com.adobe.granite.asset.api.Asset;
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

@Component(
        service = WorkflowProcess.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Retouched Metadata Workflow Process",
                Constants.SERVICE_VENDOR + "=Mentoring",
                "process.label=Retouched Workflow Process Metadata value setting"
        })
public class RetouchedWorkflowProcess implements WorkflowProcess {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String RETOUCHED = "retouched";
    public static final String JPEG_FORMAT = "image/jpeg";

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap){
        try(ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class)) {
            if(resourceResolver == null){
                logger.error("Resource Resolver is null");
                return;
            }
            String payload = workItem.getWorkflowData().getPayload().toString();
            String baseAssetPath = payload.split("/renditions")[0];
            Resource assetResource = resourceResolver.getResource(baseAssetPath);
            if(assetResource == null){
                logger.error("Resource is null");
                return;
            }
            Asset asset = assetResource.adaptTo(Asset.class);
            if(asset == null){
                logger.error("Asset is null");
                return;
            }
            Resource metadataResource = assetResource.getChild("jcr:content/metadata");
            if(metadataResource == null){
                logger.error("Metadata Resource is null");
                return;
            }
            ModifiableValueMap assetMetadata = metadataResource.adaptTo(ModifiableValueMap.class);
            if(assetMetadata == null){
                logger.error("Asset Metadata is null");
                return;
            }
            String assetFormat = assetMetadata.get("dc:format", String.class);
            if(assetFormat == null || !assetFormat.equals(JPEG_FORMAT)){
                logger.error("Asset Format is of a wrong type");
                return;
            }
            assetMetadata.put(RETOUCHED, "no");
            resourceResolver.commit();
        } catch (PersistenceException e) {
            logger.error("Persistence Exception", e);
        }
    }
}
