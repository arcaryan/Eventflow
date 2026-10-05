package com.eventmanagement.service;

import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.exception.BusinessRuleException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.PasswordUtil;
import com.eventmanagement.util.ValidationUtil;

public class AuthService {
    private final UserDAO users = DAOFactory.users();
    private final ActivityService activity = new ActivityService();

    public User authenticate(String email, String password) {
        User user = users.findByEmail(ValidationUtil.email(email));
        if (user == null || !PasswordUtil.matches(password, user.getPasswordHash())) throw new ValidationException("Email or password is incorrect.");
        if (user.getStatus() != AccountStatus.ACTIVE) throw new BusinessRuleException("This account is not active. Contact an administrator.");
        activity.log(user.getId(), "USER_LOGIN", "USER", user.getId(), "User signed in to EventFlow.");
        return user;
    }

    public User register(String name, String email, String password, String phone, UserRole role) {
        String cleanName=ValidationUtil.required(name,"Name");
        String cleanEmail=ValidationUtil.email(email);
        ValidationUtil.password(password);
        if (role != UserRole.ATTENDEE && role != UserRole.ORGANIZER) throw new ValidationException("Choose a valid account type.");
        if (users.findByEmail(cleanEmail) != null) throw new BusinessRuleException("An account with this email already exists.");
        User user=new User(null,cleanName,cleanEmail,PasswordUtil.hash(password),role,AccountStatus.ACTIVE);user.setPhone(phone);users.create(user);activity.log(user.getId(), "USER_CREATED", "USER", user.getId(), "New " + role.name().toLowerCase() + " account created.");return user;
    }
}
