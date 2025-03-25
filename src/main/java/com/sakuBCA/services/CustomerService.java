package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.dtos.superAdminDTO.CustomerDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.UserWithCustomerResponse;
import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import com.sakuBCA.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerDetailsRepository customerDetailsRepository;
    private final UserRepository userRepository;

    public CustomerDetails saveCustomerDetails(CustomerDetails customerDetails) {
        return customerDetailsRepository.save(customerDetails);
    }

    public List<UserWithCustomerResponse> getAllCustomer() {
        try {
            List<User> users = userRepository.findAllWithCustomer();

            if (users.isEmpty()) {
                throw new CustomException("Tidak ada data customer yang ditemukan", HttpStatus.NOT_FOUND);
            }

            return users.stream().map(user -> {
                UserWithCustomerResponse response = new UserWithCustomerResponse();
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
