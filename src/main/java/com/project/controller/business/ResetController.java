package com.project.controller.business;

import com.project.service.business.ResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class ResetController {
    private ResetService resetService;

    @PostMapping("/db-reset")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> resetDatabase() {
        resetService.resetDatabase();
        return ResponseEntity.ok("Database reset successfully");
    }
}
