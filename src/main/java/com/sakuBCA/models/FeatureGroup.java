package com.sakuBCA.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "feature_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name; // Misal: "User Management", "Loan Management", dll.

    @OneToMany(mappedBy = "featureGroup")
    private Set<Feature> features;
}