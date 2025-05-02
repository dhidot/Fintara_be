package com.fintara.services;

import com.fintara.config.exceptions.CustomException;
import com.fintara.dtos.customerDTO.CustomerDetailsDTO;
import com.fintara.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.fintara.models.User;
import com.fintara.repositories.CustomerDetailsRepository;
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
