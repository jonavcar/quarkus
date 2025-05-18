package com.jonavcar;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class GreetingTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "greeting.message", "Hola desde TestProfile"
        );
    }
}