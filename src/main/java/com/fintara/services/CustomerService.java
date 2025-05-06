package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.dtos.customerDTO.CustomerDetailsDTO;
import com.fintara.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.fintara.models.User;
import com.fintara.repositories.CustomerDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserService userService;
    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    public List<UserWithCustomerResponseDTO> getAllCustomer() {
        try {
            List<User> users = userService.getAllCustomers();

            return users.stream().map(user -> {
                UserWithCustomerResponseDTO response = new UserWithCustomerResponseDTO();
                response.setId(user.getId());
                response.setName(user.getName());
                response.setEmail(user.getEmail());

                // Pastikan role tidak null sebelum diakses
                if (user.getRole() != null) {
                    response.setRole(user.getRole().getName());
                } else {
                    response.setRole("ROLE_UNKNOWN");
                }

                // Set CustomerDetails jika ada
                response.setCustomerDetails(user.getCustomerDetails() != null ?
                        new CustomerDetailsDTO(user.getCustomerDetails()) : null);

                return response;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            // Tangani kesalahan tidak terduga dan log error
            throw new CustomException("Terjadi kesalahan saat mengambil data customer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserWithCustomerResponseDTO getMyProfile() {
        // Ambil data user yang sedang login
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Ambil user berdasarkan email/username
        User user = userService.findByEmail(username); // pastikan ini tidak null dan melempar exception kalau tidak ditemukan

        // Mapping ke DTO
        return UserWithCustomerResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .jenisKelamin(user.getJenisKelamin())
                .customerDetails(new CustomerDetailsDTO(user.getCustomerDetails()))
                .build();
    }


    public Long count() {
        return customerDetailsRepository.count();
    }
}
