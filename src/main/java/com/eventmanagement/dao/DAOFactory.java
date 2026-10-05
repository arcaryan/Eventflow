package com.eventmanagement.dao;

import com.eventmanagement.dao.impl.*;

public final class DAOFactory {
    private DAOFactory() { }
    public static UserDAO users() { return new UserDAOImpl(); }
    public static EventDAO events() { return new EventDAOImpl(); }
    public static TicketDAO tickets() { return new TicketDAOImpl(); }
    public static RegistrationDAO registrations() { return new RegistrationDAOImpl(); }
    public static TicketOrderDAO orders() { return new TicketOrderDAOImpl(); }
    public static EventMessageDAO messages() { return new EventMessageDAOImpl(); }
    public static ActivityLogDAO activity() { return new ActivityLogDAOImpl(); }
    public static SettingsDAO settings() { return new SettingsDAOImpl(); }
}
