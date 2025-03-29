package com.sakuBCA.config;

import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.models.*;
import com.sakuBCA.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Configuration
public class StartupConfig {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            List<String> roles = List.of("SUPER_ADMIN", "BACK_OFFICE", "BRANCH_MANAGER", "MARKETING", "CUSTOMER");

            for (String roleName : roles) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    roleRepository.save(new Role(roleName));
                }
            }
        };
    }

    @Bean
    CommandLineRunner initBranches(BranchRepository branchRepository) {
        return args -> {
            List<String> branchNames = List.of("Pusat", "Jakarta Selatan", "Surabaya", "Bandung", "Medan");

            for (String name : branchNames) {
                if (branchRepository.findByName(name).isEmpty()) {
                    Branch branch = Branch.builder()
                            .name(name)
                            .address("Alamat " + name)
                            .build();
                    branchRepository.save(branch);
                }
            }

            System.out.println("✅ Branches berhasil diinisialisasi!");
        };
    }

    @Transactional
    @Bean
    CommandLineRunner initRolesAndFeatures(RoleRepository roleRepository, FeatureRepository featureRepository, RoleFeatureRepository roleFeatureRepository) {
        return args -> {
            List<String> roleNames = List.of("SUPER_ADMIN", "BACK_OFFICE", "BRANCH_MANAGER");

            // Pastikan semua role sudah ada di database
            for (String roleName : roleNames) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role newRole = Role.builder().name(roleName).build();
                    roleRepository.save(newRole);
                }
            }

            // Setelah role ada, baru kita assign fitur
            initFeatures(featureRepository, roleRepository, roleFeatureRepository);
        };
    }

    private void initFeatures(FeatureRepository featureRepository, RoleRepository roleRepository, RoleFeatureRepository roleFeatureRepository) {
        List<String> featureNames = List.of("PEGAWAI_PROFILE", "CUSTOMER_PROFILE", "EMPLOYEE_ACCESS", "ROLE_ACCESS", "FEATURES_ACCESS", "BRANCHES_ACCESS", "CUSTOMER_ACCESS", "ROLE_FEATURE_ACCESS", "USER_ACCESS");

        for (String featureName : featureNames) {
            Optional<Feature> existingFeature = featureRepository.findByName(featureName);
            if (existingFeature.isEmpty()) {
                Feature newFeature = Feature.builder()
                        .name(featureName)
                        .build();
                featureRepository.save(newFeature);
                featureRepository.flush();

                // Assign fitur ke role hanya setelah role dijamin ada
                if (featureName.equals("EMPLOYEE_ACCESS") || featureName.equals("ROLE_ACCESS") || featureName.equals("FEATURES_ACCESS") ||
                        featureName.equals("BRANCHES_ACCESS") || featureName.equals("CUSTOMER_ACCESS") ||
                        featureName.equals("ROLE_FEATURE_ACCESS") || featureName.equals("USER_ACCESS") ||
                                featureName.equals("PEGAWAI_PROFILE") || featureName.equals("CUSTOMER_PROFILE")) {
                    assignFeatureToRole(newFeature, "SUPER_ADMIN", roleRepository, roleFeatureRepository);
                }
            }
        }

        System.out.println("✅ Fitur-fitur berhasil diinisialisasi dan diberikan ke role yang sesuai!");
    }

    @Transactional
    private void assignFeatureToRole(Feature feature, String roleName, RoleRepository roleRepository, RoleFeatureRepository roleFeatureRepository) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            RoleFeature roleFeature = RoleFeature.builder()
                    .role(role)
                    .feature(feature)
                    .build();
            roleFeatureRepository.save(roleFeature);
            System.out.println("✅ Fitur " + feature.getName() + " diberikan ke role " + roleName);
        }
    }


    @Bean
    CommandLineRunner initSuperAdmin(UserRepository userRepository, PegawaiDetailsRepository pegawaiDetailsRepository,
                                     RoleRepository roleRepository, PasswordEncoder passwordEncoder, BranchRepository branchRepository) {
        return args -> {
            // **1. Cek Role "Super Admin"**
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role("SUPER_ADMIN");
                        return roleRepository.save(newRole);
                    });

            // **2. Cek apakah user Super Admin sudah ada**
            Optional<User> existingAdmin = userRepository.findByEmail("superadmin@example.com");
            if (existingAdmin.isEmpty()) {
                User superAdmin = User.builder()
                        .name("Super Admin")
                        .email("superadmin@example.com")
                        .password(passwordEncoder.encode("superadmin123"))
                        .role(superAdminRole)
                        .userType(UserType.PEGAWAI) // **Tambahkan userType**
                        .build();

                Branch pusatBranch = branchRepository.findByName("Pusat")
                        .orElseGet(() -> {
                            Branch newBranch = new Branch();
                            newBranch.setName("Pusat");
                            return branchRepository.save(newBranch);
                        });

                PegawaiDetails details = PegawaiDetails.builder()
                        .nip("20242751")
                        .statusPegawai(StatusPegawai.ACTIVE) // **Enum**
                        .user(superAdmin)
                        .branch(pusatBranch)
                        .build();

                // **3. Simpan User & PegawaiDetails**
                userRepository.save(superAdmin);
                pegawaiDetailsRepository.save(details);

                System.out.println("✅ Super Admin berhasil dibuat!");
            } else {
                System.out.println("⚠️ Super Admin sudah ada, tidak perlu membuat ulang.");
            }
        };
    }
}
