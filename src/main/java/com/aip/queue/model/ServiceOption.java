package com.aip.queue.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ServiceOption {
    private static final List<ServiceOption> ALL = Collections.unmodifiableList(Arrays.asList(
            new ServiceOption("Student Affairs", "STA"),
            new ServiceOption("Accounts Office", "ACC"),
            new ServiceOption("Examination Cell", "EXM"),
            new ServiceOption("IT Support Desk", "IT")
    ));

    private final String name;
    private final String prefix;

    private ServiceOption(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public static List<ServiceOption> all() {
        return ALL;
    }

    public static ServiceOption find(String name) {
        if (name == null) {
            return null;
        }
        for (ServiceOption option : ALL) {
            if (option.name.equals(name)) {
                return option;
            }
        }
        return null;
    }

    public static boolean isValid(String name) {
        return find(name) != null;
    }

    public static String prefixFor(String serviceName) {
        ServiceOption option = find(serviceName);
        return option == null ? "Q" : option.getPrefix();
    }
}
