package dev.engineeringintelligence.api.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class DashboardIntegrationTest {
    @Container static final PostgreSQLContainer<?> POSTGRES=new PostgreSQLContainer<>("postgres:17-alpine");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",POSTGRES::getJdbcUrl);r.add("spring.datasource.username",POSTGRES::getUsername);
        r.add("spring.datasource.password",POSTGRES::getPassword);r.add("github.webhook-secret",()->"test-secret");
    }
    @Autowired MockMvc mvc;
    @Test void rendersSyntheticDashboard() throws Exception { mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("dashboard")).andExpect(content().string(org.hamcrest.Matchers.containsString("acme-demo/checkout-service"))); }
    @Test void exposesSameMetricsAsJson() throws Exception { mvc.perform(get("/api/v1/repositories/9000001/metrics")).andExpect(status().isOk()).andExpect(jsonPath("$.summary.openPullRequests").value(3)).andExpect(jsonPath("$.flakyJobs[0].job").value("unit-tests")); }
}
