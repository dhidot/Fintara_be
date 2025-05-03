package com.fintara.controllers;

import com.fintara.dtos.superAdminDTO.FeatureCategoryDTO;
import com.fintara.dtos.superAdminDTO.FeatureDTO;
import com.fintara.models.Feature;
import com.fintara.responses.ApiResponse;
import com.fintara.services.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/features")
public class FeatureController {
    private final FeatureService featureService;

    @Autowired
    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @Secured("FEATURE_GET_ALL_FEATURES")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FeatureDTO>>> getAllFeatures() {
        List<Feature> features = featureService.getAllFeatures();
        List<FeatureDTO> featureDTOs = features.stream().map(FeatureDTO::new).toList();
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil semua fitur", featureDTOs));
    }

    @Secured("FEATURE_GET_ALL_FEATURES")
    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<Map<String, List<FeatureCategoryDTO>>>> getGroupedFeatures() {
        Map<String, List<FeatureCategoryDTO>> groupedFeatures = featureService.getGroupedFeatureNames();
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil fitur yang dikelompokkan", groupedFeatures));
    }

    @Secured("FEATURE_ADD_FEATURES")
    @PostMapping
    public ResponseEntity<ApiResponse<Feature>> createFeature(@RequestBody Feature feature) {
        Feature createdFeature = featureService.createFeature(feature);
        return ResponseEntity.ok(ApiResponse.success("Fitur berhasil dibuat", createdFeature));
    }

    @Secured("FEATURE_GET_FEATURES_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Feature>> getFeatureById(@PathVariable UUID id) {
        Feature feature = featureService.getFeatureById(id);
        return ResponseEntity.ok(ApiResponse.success("Fitur berhasil ditemukan", feature));
    }

    @Secured("FEATURE_UPDATE_FEATURES")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Feature>> updateFeature(@PathVariable UUID id, @RequestBody Feature feature) {
        Feature updatedFeature = featureService.updateFeature(id, feature);
        return ResponseEntity.ok(ApiResponse.success("Fitur berhasil diperbarui", updatedFeature));
    }

    @Secured("FEATURE_DELETE_FEATURES")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteFeature(@PathVariable UUID id) {
        featureService.deleteFeature(id);
        return ResponseEntity.ok(ApiResponse.success("Fitur berhasil dihapus"));
    }
}
