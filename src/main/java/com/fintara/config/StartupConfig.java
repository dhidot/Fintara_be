package com.fintara.config;

import com.fintara.enums.JenisKelamin;
import com.fintara.enums.StatusPegawai;
import com.fintara.enums.UserType;
import com.fintara.models.*;
import com.fintara.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class StartupConfig {
    private static final Logger logger = LoggerFactory.getLogger(StartupConfig.class);

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
            List<Branch> branches = List.of(
                    new Branch(null, "Pusat", "Alamat Pusat", -6.2088, 106.8456, null),
                    new Branch(null, "Jakarta Selatan", "Alamat Jakarta Selatan", -6.2615, 106.8101, null),
                    new Branch(null, "Surabaya", "Alamat Surabaya", -7.2575, 112.7521, null),
                    new Branch(null, "Bandung", "Alamat Bandung", -6.9147, 107.6098, null),
                    new Branch(null, "Medan", "Alamat Medan", 3.5952, 98.6722, null)
            );

            for (Branch branch : branches) {
                if (branchRepository.findByName(branch.getName()).isEmpty()) {
                    branchRepository.save(branch);
                }
            }

            logger.info("✅ Branches berhasil diinisialisasi!");
        };
    }

    @Transactional
    @Bean
    CommandLineRunner initRolesAndFeatures(RoleRepository roleRepository, FeatureRepository featureRepository, RoleFeatureRepository roleFeatureRepository) {
        return args -> {
            List<String> roleNames = List.of("SUPER_ADMIN", "BACK_OFFICE", "BRANCH_MANAGER", "MARKETING", "CUSTOMER");

            for (String roleName : roleNames) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role newRole = Role.builder().name(roleName).build();
                    roleRepository.save(newRole);
                }
            }

            initFeatures(featureRepository, roleRepository, roleFeatureRepository);
        };
    }

    private void initFeatures(FeatureRepository featureRepository, RoleRepository roleRepository, RoleFeatureRepository roleFeatureRepository) {
        List<Map<String, Object>> featureRoles = List.of(
                // ===== Branch =====
                Map.of("name", "FEATURE_ADD_BRANCHES", "role", "SUPER_ADMIN", "category", "Branch"),
                Map.of("name", "FEATURE_GET_ALL_BRANCHES", "role", "SUPER_ADMIN", "category", "Branch"),
                Map.of("name", "FEATURE_GET_BRANCHES_BY_ID", "role", "SUPER_ADMIN", "category", "Branch"),
                Map.of("name", "FEATURE_UPDATE_BRANCHES", "role", "SUPER_ADMIN", "category", "Branch"),
                Map.of("name", "FEATURE_DELETE_BRANCHES", "role", "SUPER_ADMIN", "category", "Branch"),

                // ===== Customer =====
                Map.of("name", "FEATURE_GET_ALL_CUSTOMER", "role", "SUPER_ADMIN", "category", "Customer"),
                Map.of("name", "FEATURE_GET_CUSTOMER_BY_ID", "role", "SUPER_ADMIN", "category", "Customer"),
                Map.of("name", "FEATURE_GET_PROFILE_CUSTOMER", "role", "CUSTOMER", "category", "Customer"),

                // ===== Dashboard ====
                Map.of("name", "FEATURE_DASHBOARD", "role", "SUPER_ADMIN", "category", "Dashboard"),
                Map.of("name", "FEATURE_DASHBOARD", "role", "BACK_OFFICE", "category", "Dashboard"),
                Map.of("name", "FEATURE_DASHBOARD", "role", "BRANCH_MANAGER", "category", "Dashboard"),
                Map.of("name", "FEATURE_DASHBOARD", "role", "MARKETING", "category", "Dashboard"),

                // ===== Role & Fitur Akses =====
                Map.of("name", "FEATURE_GET_ALL_ROLE", "role", "SUPER_ADMIN", "category", "Role"),
                Map.of("name", "FEATURE_GET_ROLE_BY_ID", "role", "SUPER_ADMIN", "category", "Role"),
                Map.of("name", "FEATURE_ADD_ROLE", "role", "SUPER_ADMIN", "category", "Role"),
                Map.of("name", "FEATURE_UPDATE_ROLE", "role", "SUPER_ADMIN", "category", "Role"),
                Map.of("name", "FEATURE_DELETE_ROLE", "role", "SUPER_ADMIN", "category", "Role"),
                Map.of("name", "FEATURE_ASSIGN_ROLE_FEATURE", "role", "SUPER_ADMIN", "category", "Role"),
                Map.of("name", "FEATURE_GET_FEATURES_BY_ROLE_ID", "role", "SUPER_ADMIN", "category", "Role"),

                // ===== Feature ======
                Map.of("name", "FEATURE_GET_ALL_FEATURES", "role", "SUPER_ADMIN", "category", "Feature"),
                Map.of("name", "FEATURE_GET_FEATURES_BY_ID", "role", "SUPER_ADMIN", "category", "Feature"),

                // ===== Loan Request =====
                Map.of("name", "FEATURE_CREATE_LOAN_REQUEST", "role", "CUSTOMER", "category", "Loan Request"),
                Map.of("name", "FEATURE_APPROVAL_MARKETING", "role", "MARKETING", "category", "Loan Request"),
                Map.of("name", "FEATURE_APPROVAL_BM", "role", "BRANCH_MANAGER", "category", "Loan Request"),
                Map.of("name", "FEATURE_DISBURSE", "role", "BACK_OFFICE", "category", "Loan Request"),
                Map.of("name", "FEATURE_REVIEW_LOAN_REQUEST", "role", "MARKETING", "category", "Loan Request"),
                Map.of("name", "FEATURE_REVIEW_LOAN_REQUEST", "role", "BRANCH_MANAGER", "category", "Loan Request"),
                Map.of("name", "FEATURE_REVIEW_LOAN_REQUEST", "role", "BACK_OFFICE", "category", "Loan Request"),

                // ===== Approval History =====
                Map.of("name", "FEATURE_APPROVAL_HISTORY", "role", "BACK_OFFICE", "category", "Approval History"),
                Map.of("name", "FEATURE_APPROVAL_HISTORY", "role", "BRANCH_MANAGER", "category", "Approval History"),
                Map.of("name", "FEATURE_APPROVAL_HISTORY", "role", "MARKETING", "category", "Approval History"),
                Map.of("name", "FEATURE_APPROVAL_HISTORY", "role", "CUSTOMER", "category", "Approval History"),

                // ===== Pegawai =====
                Map.of("name", "FEATURE_ADD_EMPLOYEE", "role", "SUPER_ADMIN", "category", "Pegawai"),
                Map.of("name", "FEATURE_GET_ALL_EMPLOYEE", "role", "SUPER_ADMIN", "category", "Pegawai"),
                Map.of("name", "FEATURE_GET_EMPLOYEE_BY_ID", "role", "SUPER_ADMIN", "category", "Pegawai"),
                Map.of("name", "FEATURE_DELETE_EMPLOYEE", "role", "SUPER_ADMIN", "category", "Pegawai"),
                Map.of("name", "FEATURE_PROFILE_EMPLOYEE", "role", "MARKETING", "category", "Pegawai"),
                Map.of("name", "FEATURE_PROFILE_EMPLOYEE", "role", "BRANCH_MANAGER", "category", "Pegawai"),
                Map.of("name", "FEATURE_PROFILE_EMPLOYEE", "role", "BACK_OFFICE", "category", "Pegawai"),
                Map.of("name", "FEATURE_PROFILE_EMPLOYEE", "role", "SUPER_ADMIN", "category", "Pegawai"),

                Map.of("name", "FEATURE_UPDATE_EMPLOYEE_PROFILE", "role", "MARKETING", "category", "Pegawai"),
                Map.of("name", "FEATURE_UPDATE_EMPLOYEE_PROFILE", "role", "BRANCH_MANAGER", "category", "Pegawai"),
                Map.of("name", "FEATURE_UPDATE_EMPLOYEE_PROFILE", "role", "BACK_OFFICE", "category", "Pegawai"),
                Map.of("name", "FEATURE_UPDATE_EMPLOYEE_PROFILE", "role", "SUPER_ADMIN", "category", "Pegawai"),

                Map.of("name", "FEATURE_CHANGE_PASSWORD_EMPLOYEE", "role", "MARKETING", "category", "Pegawai"),
                Map.of("name", "FEATURE_CHANGE_PASSWORD_EMPLOYEE", "role", "BRANCH_MANAGER", "category", "Pegawai"),
                Map.of("name", "FEATURE_CHANGE_PASSWORD_EMPLOYEE", "role", "BACK_OFFICE", "category", "Pegawai"),
                Map.of("name", "FEATURE_CHANGE_PASSWORD_EMPLOYEE", "role", "SUPER_ADMIN", "category", "Pegawai"),


                Map.of("name", "FEATURE_UPDATE_CUSTOMER_PROFILE", "role", "CUSTOMER", "category", "Pegawai"),
                // ===== Plafond =====
                Map.of("name", "FEATURE_GET_ALL_PLAFOND", "role", "SUPER_ADMIN", "category", "Plafond"),
                Map.of("name", "FEATURE_GET_PLAFOND_BY_ID", "role", "SUPER_ADMIN", "category", "Plafond"),
                Map.of("name", "FEATURE_ADD_PLAFOND", "role", "SUPER_ADMIN", "category", "Plafond"),
                Map.of("name", "FEATURE_UPDATE_PLAFOND", "role", "SUPER_ADMIN", "category", "Plafond"),

                // ===== Loan Status (opsional jika status dikontrol via controller terpisah) =====
                Map.of("name", "FEATURE_GET_ALL_LOAN_STATUS", "role", "SUPER_ADMIN", "category", "Loan Status"),
                Map.of("name", "FEATURE_ADD_LOAN_STATUS", "role", "SUPER_ADMIN", "category", "Loan Status"),
                Map.of("name", "FEATURE_UPDATE_LOAN_STATUS", "role", "SUPER_ADMIN", "category", "Loan Status"),
                Map.of("name", "FEATURE_DELETE_LOAN_STATUS", "role", "SUPER_ADMIN", "category", "Loan Status")
        );

        for (Map<String, Object> map : featureRoles) {
            String roleName = (String) map.get("role");
            String featureName = (String) map.get("name");
            String category = (String) map.get("category");

            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

            Feature feature = featureRepository.findByName(featureName)
                    .map(existing -> {
                        existing.setCategory(category); // update category kalau null
                        return featureRepository.save(existing);
                    })
                    .orElseGet(() -> featureRepository.save(
                            Feature.builder()
                                    .name(featureName)
                                    .category(category)
                                    .build()
                    ));

            boolean exists = roleFeatureRepository.existsByRoleAndFeature(role, feature);
            if (!exists) {
                RoleFeature roleFeature = RoleFeature.builder()
                        .role(role)
                        .feature(feature)
                        .build();
                roleFeatureRepository.save(roleFeature);
            }
        }
        logger.info("✅ Semua fitur berhasil diinisialisasi dan diberikan ke peran yang sesuai!");
    }


    @Transactional
    private void assignFeatureToRole(Feature feature, String roleName, RoleRepository roleRepository, RoleFeatureRepository roleFeatureRepository) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            RoleFeature roleFeature = RoleFeature.builder().role(role).feature(feature).build();
            roleFeatureRepository.save(roleFeature);
            logger.info("✅ Fitur " + feature.getName() + " diberikan ke role " + roleName);
        }
    }

    @Bean
    CommandLineRunner initSuperAdmin(UserRepository userRepository, PegawaiDetailsRepository pegawaiDetailsRepository,
                                     RoleRepository roleRepository, PasswordEncoder passwordEncoder, BranchRepository branchRepository) {
        return args -> {
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("SUPER_ADMIN")));

            Optional<User> existingAdmin = userRepository.findByEmail("superadmin@example.com");
            if (existingAdmin.isEmpty()) {
                User superAdmin = User.builder()
                        .name("Super Admin")
                        .email("superadmin@example.com")
                        .password(passwordEncoder.encode("superadmin123"))
                        .role(superAdminRole)
                        .userType(UserType.PEGAWAI)
                        .isFirstLogin(false)
                        .build();

                Branch pusatBranch = branchRepository.findByName("Pusat")
                        .orElseGet(() -> branchRepository.save(new Branch(null, "Pusat", "Alamat Pusat", -6.2088, 106.8456, null)));

                PegawaiDetails details = PegawaiDetails.builder()
                        .nip("20242751")
                        .statusPegawai(StatusPegawai.ACTIVE)
                        .user(superAdmin)
                        .branch(pusatBranch)
                        .build();

                userRepository.save(superAdmin);
                pegawaiDetailsRepository.save(details);

                logger.info("✅ Super Admin berhasil dibuat!");
            } else {
                logger.warn("⚠️ Super Admin sudah ada, tidak perlu membuat ulang.");
            }
        };
    }

    @Bean
    CommandLineRunner initTestUsers(UserRepository userRepository, PegawaiDetailsRepository pegawaiDetailsRepository,
                                    RoleRepository roleRepository, PasswordEncoder passwordEncoder, BranchRepository branchRepository) {
        return args -> {
            // Cabang Pusat
            createTestUser("marketing_pusat@example.com", "Marketing Pusat", "MARKETING", "MKT2024", "Pusat", JenisKelamin.LAKI_LAKI,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("marketing1_pusat@example.com", "Marketing 1 Pusat", "MARKETING", "MKT12024", "Pusat", JenisKelamin.PEREMPUAN,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bm_pusat@example.com", "Branch Manager Pusat", "BRANCH_MANAGER", "BM2024", "Pusat", JenisKelamin.LAKI_LAKI,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bo_pusat@example.com", "Back Office Pusat", "BACK_OFFICE", "BO2024", "Pusat", JenisKelamin.PEREMPUAN,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);

            // Cabang Jakarta
            createTestUser("marketing_jakarta@example.com", "Marketing Jakarta", "MARKETING", "MKT2025", "Jakarta", JenisKelamin.LAKI_LAKI,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("marketing1_jakarta@example.com", "Marketing 1 Jakarta", "MARKETING", "MKT12025", "Jakarta", JenisKelamin.PEREMPUAN,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bm_jakarta@example.com", "Branch Manager Jakarta", "BRANCH_MANAGER", "BM2025", "Jakarta", JenisKelamin.LAKI_LAKI,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bo_jakarta@example.com", "Back Office Jakarta", "BACK_OFFICE", "BO2025", "Jakarta", JenisKelamin.PEREMPUAN,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);

            // Cabang Surabaya
            createTestUser("marketing_surabaya@example.com", "Marketing Surabaya", "MARKETING", "MKT2026", "Surabaya", JenisKelamin.PEREMPUAN,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bm_surabaya@example.com", "Branch Manager Surabaya", "BRANCH_MANAGER", "BM2026", "Surabaya", JenisKelamin.LAKI_LAKI,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bo_surabaya@example.com", "Back Office Surabaya", "BACK_OFFICE", "BO2026", "Surabaya", JenisKelamin.PEREMPUAN,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);

            // Cabang Jakarta Selatan
            createTestUser("marketing_jaksel@example.com", "Marketing Jakarta Selatan", "MARKETING", "MKT2027", "Jakarta Selatan", JenisKelamin.LAKI_LAKI,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bm_jakse@example.com", "Branch Manager Jakarta Selatan", "BRANCH_MANAGER", "BM2027", "Jakarta Selatan", JenisKelamin.PEREMPUAN,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
            createTestUser("bo_jaksel@example.com", "Back Office Jakarta Selatan", "BACK_OFFICE", "BO2027", "Jakarta Selatan", JenisKelamin.LAKI_LAKI,
                    userRepository, pegawaiDetailsRepository, roleRepository, passwordEncoder, branchRepository);
        };
    }

    private void createTestUser(String email, String name, String roleName, String nip, String branchName, JenisKelamin jenisKelamin,
                                UserRepository userRepository, PegawaiDetailsRepository pegawaiDetailsRepository,
                                RoleRepository roleRepository, PasswordEncoder passwordEncoder, BranchRepository branchRepository) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode("test1234")) // 🔹 Password awal
                    .role(role)
                    .userType(UserType.PEGAWAI)
                    .isFirstLogin(true) // 🔹 Set flag agar pegawai wajib ganti password saat login pertama
                    .build();

            Branch assignedBranch = branchRepository.findByName(branchName)
                    .orElseGet(() -> branchRepository.save(new Branch(null, branchName, "Alamat " + branchName, -6.2088, 106.8456, null)));

            PegawaiDetails details = PegawaiDetails.builder()
                    .nip(nip)
                    .statusPegawai(StatusPegawai.ACTIVE)
                    .user(user)
                    .branch(assignedBranch)
                    .build();

            userRepository.save(user);
            pegawaiDetailsRepository.save(details);

            logger.info("✅ {} berhasil dibuat di cabang {} dengan email {}", roleName, branchName, email);
        } else {
            logger.warn("⚠️ {} dengan email {} sudah ada, tidak perlu membuat ulang.", roleName, email);
        }
    }




    @Bean
    CommandLineRunner initPlafonds(PlafondRepository plafondRepository) {
        return args -> {
            List<Plafond> plafonds = List.of(
                    new Plafond(null, "Bronze", new BigDecimal("2000000"), new BigDecimal("0.10"), 6, 12),
                    new Plafond(null, "Silver", new BigDecimal("5000000"), new BigDecimal("0.08"), 6, 24),
                    new Plafond(null, "Gold", new BigDecimal("10000000"), new BigDecimal("0.07"), 12, 36),
                    new Plafond(null, "Platinum", new BigDecimal("200000000"), new BigDecimal("0.05"), 12, 60)
            );

            for (Plafond plafond : plafonds) {
                // Misalnya, jika sudah ada plafond berdasarkan nama, maka lewati penyimpanan
                if (plafondRepository.findByName(plafond.getName()).isEmpty()) {
                    plafondRepository.save(plafond);
                    System.out.println("✅ Plafond " + plafond.getName() + " berhasil ditambahkan.");
                }
            }

            logger.info("✅ Semua data Plafond berhasil diinisialisasi!");
        };
    }

    @Bean
    CommandLineRunner seedLoanStatuses(LoanStatusRepository loanStatusRepository) {
        return args -> {
            List<String> statuses = List.of(
                    "REVIEW", "DIREKOMENDASIKAN_MARKETING", "DITOLAK_MARKETING",
                    "DITOLAK_BM", "DISETUJUI_BM", "DISBURSED"
            );

            for (String status : statuses) {
                if (loanStatusRepository.findByName(status).isEmpty()) {
                    loanStatusRepository.save(LoanStatus.builder().name(status).build());
                }
            }
        };
    }
}
