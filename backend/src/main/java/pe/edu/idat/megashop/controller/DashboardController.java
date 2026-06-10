package pe.edu.idat.megashop.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.megashop.service.DashboardService;
import pe.edu.idat.megashop.web.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {
    private final DashboardService dashboard;

    public DashboardController(DashboardService dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping("/resumen")
    public ApiResponse<Map<String, Object>> summary() {
        return ApiResponse.of(dashboard.summary());
    }

    @GetMapping("/ventas")
    public ApiResponse<List<Map<String, Object>>> sales() {
        return ApiResponse.of(dashboard.sales());
    }

    @GetMapping("/clientes")
    public ApiResponse<List<Map<String, Object>>> clients() {
        return ApiResponse.of(dashboard.topClients());
    }
}
