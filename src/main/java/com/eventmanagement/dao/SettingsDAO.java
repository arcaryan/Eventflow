package com.eventmanagement.dao;

import java.util.Map;

public interface SettingsDAO {
    Map<String, String> findAll();
    void update(String key, String value);
}
