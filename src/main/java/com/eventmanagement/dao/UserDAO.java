package com.eventmanagement.dao;

import com.eventmanagement.model.*;
import java.util.List;

public interface UserDAO {
    User findByEmail(String email);
    User findById(long id);
    long create(User user);
    void updateProfile(User user);
    void updateAdmin(User user);
    List<User> findAll(String search, String role, String status);
}
