package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.dtos.customerDTO.CustomerDetailsDTO;
import com.sakuBCA.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.sakuBCA.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserService userService;

    public List<UserWithCustomerResponseDTO> getAllCustomer() {
        try {
            List<User> users = userService.getAllCustomers();

            if (users.isEmpty()) {
                throw new CustomException("Tidak ada data customer yang ditemukan", HttpStatus.NOT_FOUND);
            }

            return users.stream().map(user -> {
                UserWithCustomerResponseDTO response = new UserWithCustomerResponseDTO();
                response.setId(user.getId());
                response.setName(user.getName());
                response.setEmail(user.getEmail());

                // Pastikan role tidak null sebelum diakses
                if (user.getRole() != null) {
                    response.setRole(user.getRole().getName());
                } else {
                    response.setRole("ROLE_UNKNOWN"); // Default jika role null
                }

                // Set CustomerDetails jika ada
                response.setCustomerDetails(user.getCustomerDetails() != null ?
                        new CustomerDetailsDTO(user.getCustomerDetails()) : null);

                return response;
            }).collect(Collectors.toList());

        } catch (CustomException e) {
            throw e; // CustomException tetap dilempar agar bisa ditangani oleh controller
        } catch (Exception e) {
            // Tangani kesalahan tidak terduga dan log error
            throw new CustomException("Terjadi kesalahan saat mengambil data customer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
