package com.sakuBCA.config;

import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.PegawaiDetailsRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
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
            List<String> roles = List.of("Super Admin", "Back Office", "Branch Manager", "Marketing", "Customer");

            for (String roleName : roles) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    roleRepository.save(new Role(null, roleName));
                }
            }
        };
    }

    @Bean
    CommandLineRunner initSuperAdmin(UserRepository userRepository, PegawaiDetailsRepository pegawaiDetailsRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // **1. Cek Role "Super Admin"**
            Role superAdminRole = roleRepository.findByName("Super Admin")
                    .orElseGet(() -> {
                        Role newRole = new Role(null, "Super Admin");
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

                PegawaiDetails details = PegawaiDetails.builder()
                        .statusPegawai(StatusPegawai.ACTIVE) // **Enum**
                        .user(superAdmin) // **Hubungkan ke user**
                        .branchId(null) // **Sesuaikan dengan database**
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
