package com.sakuBCA.controllers;

import com.sakuBCA.dtos.FeatureDTO;
import com.sakuBCA.models.Feature;
import com.sakuBCA.repositories.FeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.sakuBCA.services.FeatureService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/features")
public class FeatureController {
    private final FeatureService featureService;

    @Autowired
    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @Secured("GET_FEATURES")
    @GetMapping
    public List<FeatureDTO> getAllFeatures() {
        List<Feature> features = featureService.getAllFeatures();
        return features.stream().map(FeatureDTO::new).toList();
    }

    @PostMapping
    public Feature createFeature(@RequestBody Feature feature) {
        return featureService.createFeature(feature);
    }

    @GetMapping("/{id}")
    public Feature getFeatureById(@PathVariable UUID id) {
        return featureService.getFeatureById(id);
    }

    @PutMapping("/{id}")
    public Feature updateFeature(@PathVariable UUID id, @RequestBody Feature feature) {
        return featureService.updateFeature(id, feature);
    }

    @DeleteMapping("/{id}")
    public void deleteFeature(@PathVariable UUID id) {
        featureService.deleteFeature(id);
    }
}

