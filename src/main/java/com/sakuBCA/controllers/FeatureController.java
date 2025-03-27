package com.sakuBCA.controllers;

import com.sakuBCA.dtos.FeatureDTO;
import com.sakuBCA.models.Feature;
import com.sakuBCA.repositories.FeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Secured("FEATURE_FEATURES_ACCESS")
    @GetMapping("/all")
    public List<FeatureDTO> getAllFeatures() {
        List<Feature> features = featureService.getAllFeatures();
        return features.stream().map(FeatureDTO::new).toList();
    }

    @Secured("FEATURES_ACCESS")
    @PostMapping
    public Feature createFeature(@RequestBody Feature feature) {
        return featureService.createFeature(feature);
    }

    @Secured("FEATURES_ACCESS")
    @GetMapping("/{id}")
    public Feature getFeatureById(@PathVariable UUID id) {
        return featureService.getFeatureById(id);
    }

    @Secured("FEATURES_ACCESS")
    @PutMapping("/{id}")
    public Feature updateFeature(@PathVariable UUID id, @RequestBody Feature feature) {
        return featureService.updateFeature(id, feature);
    }

    @Secured("FEATURES_ACCESS")
    @DeleteMapping("/{id}")
    public void deleteFeature(@PathVariable UUID id) {
        featureService.deleteFeature(id);
    }
}

