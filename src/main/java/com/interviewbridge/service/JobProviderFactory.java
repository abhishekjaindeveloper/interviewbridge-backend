package com.interviewbridge.service;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JobProviderFactory {

    private final Map<String, JobProvider> providers;

    public JobProviderFactory(List<JobProvider> providerList) {
        this.providers = providerList.stream()
            .collect(Collectors.toMap(
                provider -> provider.getProviderName().toUpperCase(),
                Function.identity()
            ));
    }

    public JobProvider getProvider(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Job provider name cannot be null");
        }
        JobProvider provider = providers.get(name.toUpperCase());
        if (provider == null) {
            throw new IllegalArgumentException("No job provider found with name: " + name);
        }
        return provider;
    }
}
