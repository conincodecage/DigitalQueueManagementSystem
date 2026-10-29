package com.aip.queue.model;

import java.sql.Timestamp;

public class Ticket {
    private long id;
    private String serviceType;
    private int tokenNumber;
    private String customerName;
    private String status;
    private Timestamp createdAt;
    private Timestamp calledAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public int getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(int tokenNumber) { this.tokenNumber = tokenNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getCalledAt() { return calledAt; }
    public void setCalledAt(Timestamp calledAt) { this.calledAt = calledAt; }

    public String getDisplayToken() {
        return ServiceOption.prefixFor(serviceType) + String.format("-%03d", tokenNumber);
    }
}
