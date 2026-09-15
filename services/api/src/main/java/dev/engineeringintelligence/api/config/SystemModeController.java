package dev.engineeringintelligence.api.config;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemModeController {
    private final AppProperties properties;

    public SystemModeController(AppProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/mode")
    public Map<String, Object> mode() {
        return Map.of(
            "mode", properties.mode().name().toLowerCase(),
            "demoData", properties.demo().enabled(),
            "minimumCohortSize", properties.security().minimumCohortSize()
        );
    }
}

