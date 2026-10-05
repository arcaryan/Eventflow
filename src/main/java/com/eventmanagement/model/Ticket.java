package com.eventmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String name;
    private String description;
    private BigDecimal price = BigDecimal.ZERO;
    private int quantity;
    private int soldQuantity;
    private LocalDateTime salesStartAt;
    private LocalDateTime salesEndAt;
    private TicketStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price == null ? BigDecimal.ZERO : price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getSoldQuantity() { return soldQuantity; }
    public void setSoldQuantity(int soldQuantity) { this.soldQuantity = soldQuantity; }
    public int getAvailableQuantity() { return Math.max(0, quantity - soldQuantity); }
    public LocalDateTime getSalesStartAt() { return salesStartAt; }
    public void setSalesStartAt(LocalDateTime salesStartAt) { this.salesStartAt = salesStartAt; }
    public LocalDateTime getSalesEndAt() { return salesEndAt; }
    public void setSalesEndAt(LocalDateTime salesEndAt) { this.salesEndAt = salesEndAt; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
}
