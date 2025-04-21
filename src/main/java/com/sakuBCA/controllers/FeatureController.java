package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.FeatureDTO;
import com.sakuBCA.models.Feature;
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

    @Secured("FEATURE_FEATURES_ACCESS")
    @GetMapping("/all")
    public List<FeatureDTO> getAllFeatures() {
        List<Feature> features = featureService.getAllFeatures();
        return features.stream().map(FeatureDTO::new).toList();
    }

    @Secured("FEATURE_ADD_FEATURES_ACCESS")
    @PostMapping
    public Feature createFeature(@RequestBody Feature feature) {
        return featureService.createFeature(feature);
    }

    @Secured("FEATURE_GET_FEATURES_BY_ID_ACCESS")
    @GetMapping("/{id}")
    public Feature getFeatureById(@PathVariable UUID id) {
        return featureService.getFeatureById(id);
    }

    @Secured("FEATURE_UPDATE_FEATURES_ACCESS")
    @PutMapping("/{id}")
    public Feature updateFeature(@PathVariable UUID id, @RequestBody Feature feature) {
        return featureService.updateFeature(id, feature);
    }

    @Secured("FEATURE_DELETE_FEATURES_ACCESS")
    @DeleteMapping("/{id}")
    public void deleteFeature(@PathVariable UUID id) {
        featureService.deleteFeature(id);
    }
}

