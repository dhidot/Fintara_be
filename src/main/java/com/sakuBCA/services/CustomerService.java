package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.dtos.customerDTO.CustomerDetailsDTO;
import com.sakuBCA.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public Long count() {
        return customerDetailsRepository.count();
    }
}
