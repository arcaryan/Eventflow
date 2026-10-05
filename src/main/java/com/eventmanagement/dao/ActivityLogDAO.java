package com.eventmanagement.dao;

import com.eventmanagement.model.ActivityLog;
import java.util.List;

public interface ActivityLogDAO {
    void create(ActivityLog log);
    List<ActivityLog> findRecent(int limit);
}
