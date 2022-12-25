package com.mentorting.aem.core.services;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import java.util.List;

@Component(
        service = ResourceChangeListener.class,
        immediate = true,
        property = {
                ResourceChangeListener.PATHS + "=/content/dam/mentoring",
                ResourceChangeListener.CHANGES + "=ADDED"
        }
)
public class MentoringDAMResourceChangeListener implements ResourceChangeListener {

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    WorkflowService workflowService;

    private final String WORKFLOW_MODEL_NAME = "custom-image-rendingtion-wkfl";

    @Override
    public void onChange(@NotNull List<ResourceChange> list) {
        try {
            ResourceResolver resourceResolver = ResourceResolverUtility.getResourceResolver(resourceResolverFactory);
            Session session = resourceResolver.adaptTo(Session.class);
            WorkflowSession workflowSession = workflowService.getWorkflowSession(session);
            WorkflowModel model = workflowSession.getModel(WORKFLOW_MODEL_NAME);
            list.forEach(resourceChange -> {
                WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", resourceChange.getPath());
                try {
                    workflowSession.startWorkflow(model, workflowData);
                } catch (WorkflowException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (LoginException | WorkflowException e) {
            throw new RuntimeException(e);
        }
    }
}
