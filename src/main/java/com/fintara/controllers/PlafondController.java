package com.fintara.controllers;

import com.fintara.models.Plafond;
import com.fintara.responses.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<Plafond>>> getAllPlafonds() {
        List<Plafond> plafonds = plafondService.getAllPlafonds();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", plafonds));
    }

    @Secured("FEATURE_GET_PLAFOND_BY_ID")
    @GetMapping("get/{id}")
    public ResponseEntity<ApiResponse<Plafond>> getPlafondById(@PathVariable UUID id) {
        Plafond plafond = plafondService.getPlafondById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", plafond));
    }

    @Secured("FEATURE_ADD_PLAFOND")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Plafond>> addPlafond(@Valid @RequestBody Plafond request) {
        Plafond createdPlafond = plafondService.createPlafond(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Plafond created successfully", createdPlafond));
    }

    @Secured("FEATURE_UPDATE_PLAFOND")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Plafond>> updatePlafond(@PathVariable UUID id, @RequestBody Plafond request) {
        Plafond updatedPlafond = plafondService.updatePlafond(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Plafond updated successfully", updatedPlafond));
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<Plafond>> getPlafondByName(@PathVariable String name) {
        Plafond plafond = plafondService.getPlafondByName(name);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", plafond));
    }
}
