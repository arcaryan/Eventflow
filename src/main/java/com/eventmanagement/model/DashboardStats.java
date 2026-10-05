package com.eventmanagement.model;

import java.math.BigDecimal;

public class DashboardStats {
    private long totalUsers;
    private long totalOrganizers;
    private long totalAttendees;
    private long totalEvents;
    private long pendingEvents;
    private long approvedEvents;
    private long totalRegistrations;
    private long totalTicketsSold;
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalOrganizers() { return totalOrganizers; }
    public void setTotalOrganizers(long totalOrganizers) { this.totalOrganizers = totalOrganizers; }
    public long getTotalAttendees() { return totalAttendees; }
    public void setTotalAttendees(long totalAttendees) { this.totalAttendees = totalAttendees; }
    public long getTotalEvents() { return totalEvents; }
    public void setTotalEvents(long totalEvents) { this.totalEvents = totalEvents; }
    public long getPendingEvents() { return pendingEvents; }
    public void setPendingEvents(long pendingEvents) { this.pendingEvents = pendingEvents; }
    public long getApprovedEvents() { return approvedEvents; }
    public void setApprovedEvents(long approvedEvents) { this.approvedEvents = approvedEvents; }
    public long getTotalRegistrations() { return totalRegistrations; }
    public void setTotalRegistrations(long totalRegistrations) { this.totalRegistrations = totalRegistrations; }
    public long getTotalTicketsSold() { return totalTicketsSold; }
    public void setTotalTicketsSold(long totalTicketsSold) { this.totalTicketsSold = totalTicketsSold; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue; }
}
