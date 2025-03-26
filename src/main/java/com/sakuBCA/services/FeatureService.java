package com.sakuBCA.services;

import com.sakuBCA.models.Feature;
import com.sakuBCA.repositories.FeatureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeatureService {
    @Autowired
    private final FeatureRepository featureRepository;

    public List<Feature> getAllFeatures() {
        return featureRepository.findAll();
    }

    public Feature createFeature(Feature feature) {
        return featureRepository.save(feature);
    }

    public Feature getFeatureById(UUID id) {
        return featureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature not found"));
    }

    public String getFeatureNameById(UUID id) {
        return featureRepository.findFeatureNameById(id);
    }

    public Feature updateFeature(UUID id, Feature updatedFeature) {
        Feature feature = getFeatureById(id);
        feature.setName(updatedFeature.getName());
        return featureRepository.save(feature);
    }

    public void deleteFeature(UUID id) {
        featureRepository.deleteById(id);
    }
}
