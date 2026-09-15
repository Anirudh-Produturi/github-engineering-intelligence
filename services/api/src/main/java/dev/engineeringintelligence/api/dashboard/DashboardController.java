package dev.engineeringintelligence.api.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {
    private final DashboardRepository repository;

    public DashboardController(DashboardRepository repository) { this.repository = repository; }

    @GetMapping("/")
    public String dashboard(Model model) {
        var repositoryId = repository.firstRepositoryId();
        if (repositoryId.isEmpty()) return "getting-started";
        model.addAttribute("dashboard", repository.load(repositoryId.get()));
        return "dashboard";
    }

    @GetMapping("/api/v1/repositories/{repositoryId}/metrics")
    @ResponseBody
    public DashboardMetrics metrics(@PathVariable long repositoryId) { return repository.load(repositoryId); }
}
