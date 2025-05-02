package com.fintara.controllers;

import com.fintara.models.Plafond;
import com.fintara.services.PlafondService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/plafonds")
@RequiredArgsConstructor
public class PlafondController {

    private final PlafondService plafondService;

    @Secured("FEATURE_GET_ALL_PLAFOND")
    @GetMapping("/all")
    public ResponseEntity<List<Plafond>> getAllPlafonds() {
        return ResponseEntity.ok(plafondService.getAllPlafonds());
    }

    @Secured("FEATURE_GET_PLAFOND_BY_ID")
    @GetMapping("get/{id}")
    public ResponseEntity<Plafond> getPlafondById(@PathVariable UUID id) {
        return ResponseEntity.ok(plafondService.getPlafondById(id));
    }

    @Secured("FEATURE_ADD_PLAFOND")
    @PostMapping("/add")
    public ResponseEntity<Plafond> addPlafond(@Valid @RequestBody Plafond request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plafondService.createPlafond(request));
    }


    @Secured("FEATURE_UPDATE_PLAFOND")
    @PutMapping("/update/{id}")
    public ResponseEntity<Plafond> updatePlafond(@PathVariable UUID id, @RequestBody Plafond request) {
        return ResponseEntity.ok(plafondService.updatePlafond(id, request));
    }

    @GetMapping("/{name}")
    public ResponseEntity<Plafond> getPlafondByName(@PathVariable String name) {
        return ResponseEntity.ok(plafondService.getPlafondByName(name));
    }
}
