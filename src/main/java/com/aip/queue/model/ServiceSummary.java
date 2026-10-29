package com.aip.queue.model;

public class ServiceSummary {
    private final String serviceName;
    private final Integer currentTokenNumber;
    private final int waitingCount;

    public ServiceSummary(String serviceName, Integer currentTokenNumber, int waitingCount) {
        this.serviceName = serviceName;
        this.currentTokenNumber = currentTokenNumber;
        this.waitingCount = waitingCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getCurrentTokenNumber() {
        return currentTokenNumber;
    }

    public int getWaitingCount() {
        return waitingCount;
    }

    public String getCurrentTokenDisplay() {
        if (currentTokenNumber == null) {
            return "—";
        }
        return ServiceOption.prefixFor(serviceName) + String.format("-%03d", currentTokenNumber);
    }
}
