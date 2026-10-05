package com.eventmanagement.service;

import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.model.ActivityLog;

public class ActivityService {
    public void log(Long actorId,String action,String entity,Long entityId,String description){
        try {
            ActivityLog l=new ActivityLog();l.setActorUserId(actorId);l.setActionType(action);l.setEntityType(entity);l.setEntityId(entityId);l.setDescription(description);DAOFactory.activity().create(l);
        } catch (RuntimeException ignored) {
            // Audit failure must not turn a successful user-facing operation into a failure.
        }
    }
}
