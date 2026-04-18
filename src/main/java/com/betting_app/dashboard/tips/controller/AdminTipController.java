package com.betting_app.dashboard.tips.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.betting_app.dashboard.tips.dto.CreateTipRequest;
import com.betting_app.dashboard.tips.dto.TipResponse;
import com.betting_app.dashboard.tips.dto.UpdateTipRequest;
import com.betting_app.dashboard.tips.service.TipService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tips")
public class AdminTipController {

    private final TipService tipService;

    public AdminTipController(TipService tipService) {
        this.tipService = tipService;
    }

    @GetMapping
    public ResponseEntity<List<TipResponse>> getAll() {
        return ResponseEntity.ok(tipService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tipService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TipResponse> create(@Valid @RequestBody CreateTipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTipRequest request
    ) {
        return ResponseEntity.ok(tipService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        tipService.delete(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tip deleted successfully"
        ));
    }
}