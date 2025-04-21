package com.sakuBCA.controllers;

import com.sakuBCA.models.Plafond;
import com.sakuBCA.services.PlafondService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @Secured("FEATURE_PLAFOND_ACCESS")
    @GetMapping("get/{id}")
    public ResponseEntity<Plafond> getPlafondById(@PathVariable UUID id) {
        return ResponseEntity.ok(plafondService.getPlafondById(id));
    }

    @Secured("FEATURE_PLAFOND_ACCESS")
    @PostMapping("/add")
    public ResponseEntity<Plafond> addPlafond(@RequestBody Plafond request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plafondService.createPlafond(request));
    }


    @Secured("FEATURE_PLAFOND_ACCESS")
    @PutMapping("/update/{id}")
    public ResponseEntity<Plafond> updatePlafond(@PathVariable UUID id, @RequestBody Plafond request) {
        return ResponseEntity.ok(plafondService.updatePlafond(id, request));
    }

    @GetMapping("/{name}")
    public ResponseEntity<Plafond> getPlafondByName(@PathVariable String name) {
        return ResponseEntity.ok(plafondService.getPlafondByName(name));
    }
}
