package com.sakuBCA.config;

import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.models.Branch;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.BranchRepository;
import com.sakuBCA.repositories.PegawaiDetailsRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
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
            List<String> roles = List.of("Super Admin", "Back Office", "Branch Manager", "Marketing", "Customer");

            for (String roleName : roles) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    roleRepository.save(new Role(null, roleName));
                }
            }
        };
    }

    @Transactional
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

    @Bean
    CommandLineRunner initSuperAdmin(UserRepository userRepository, PegawaiDetailsRepository pegawaiDetailsRepository,
                                     RoleRepository roleRepository, PasswordEncoder passwordEncoder, BranchRepository branchRepository) {
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
