package com.sakuBCA.controllers;

import com.sakuBCA.models.Plafond;
import com.sakuBCA.services.PlafondService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plafonds")
@RequiredArgsConstructor
public class PlafondController {

    private final PlafondService plafondService;

    @Secured("FEATURE_PLAFOND_ACCESS")
    @GetMapping("/all")
    public ResponseEntity<List<Plafond>> getAllPlafonds() {
        return ResponseEntity.ok(plafondService.getAllPlafonds());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Plafond> getPlafondByName(@PathVariable String name) {
        return ResponseEntity.ok(plafondService.getPlafondByName(name));
    }
}
