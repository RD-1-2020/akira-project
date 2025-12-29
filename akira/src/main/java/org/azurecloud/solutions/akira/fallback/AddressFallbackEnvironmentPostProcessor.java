package org.azurecloud.solutions.akira.fallback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.azurecloud.solutions.akira.fallback.DockerHost.AKIRA_POSTGRES;

public class AddressFallbackEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final int TIMEOUT_MS = 200;

    private static final List<AddressFallbackRule> RULES = List.of(
            AddressFallbackRuleFactory.jdbc("spring.datasource.url", AKIRA_POSTGRES, 5432)
    );

    private static final List<String> HIGH_PRIORITY_SOURCES = List.of(
            "systemProperties",
            "systemEnvironment",
            "commandLineArgs",
            "SPRING_APPLICATION_JSON"
    );

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        Map<String, Object> overrides = new LinkedHashMap<>();

        for (AddressFallbackRule rule : RULES) {
            if (!isOverriddenByHighPrioritySource(env, rule.key())
                    && !HostReachability.canConnect(rule.dockerHost(), rule.port(), TIMEOUT_MS)) {
                overrides.put(rule.key(), rule.fallbackValue());
            }
        }

        if (!overrides.isEmpty()) {
            env.getPropertySources().addFirst(new MapPropertySource("addressFallback", overrides));
        }
    }

    private boolean isOverriddenByHighPrioritySource(ConfigurableEnvironment env, String key) {
        return env.getPropertySources().stream()
                .filter(ps -> ps.containsProperty(key))
                .map(PropertySource::getName)
                .anyMatch(name -> HIGH_PRIORITY_SOURCES.stream().anyMatch(name::contains));
    }
}