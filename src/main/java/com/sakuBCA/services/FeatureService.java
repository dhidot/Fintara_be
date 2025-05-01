package com.sakuBCA.services;

import com.sakuBCA.dtos.superAdminDTO.FeatureCategoryDTO;
import com.sakuBCA.models.Feature;
import com.sakuBCA.repositories.FeatureCategoryView;
import com.sakuBCA.repositories.FeatureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeatureService {
    @Autowired
    private final FeatureRepository featureRepository;

    public List<Feature> findAllById(List<UUID> ids) {
        return featureRepository.findAllById(ids);
    }

    public List<Feature> getAllFeatures() {
        return featureRepository.findAll();
    }

    public Map<String, List<FeatureCategoryDTO>> getGroupedFeatureNames() {
        return featureRepository.findAllProjected().stream()
                .collect(Collectors.groupingBy(
                        FeatureCategoryView::getCategory,
                        Collectors.mapping(
                                f -> new FeatureCategoryDTO(f.getId(), f.getName()),
                                Collectors.toList()
                        )
                ));
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
