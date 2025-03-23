package com.sakuBCA.services;

import com.sakuBCA.dtos.CustomerDetailsDTO;
import com.sakuBCA.dtos.UserWithCustomerResponse;
import com.sakuBCA.dtos.UserWithPegawaiResponse;
import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import com.sakuBCA.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
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
        List<User> users = userRepository.findAllWithCustomer();
        return users.stream().map(user -> {
            UserWithCustomerResponse response = new UserWithCustomerResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().getName());
            response.setCustomerDetails(user.getCustomerDetails() != null ?
                    new CustomerDetailsDTO(user.getCustomerDetails()) : null);
            return response;
        }).collect(Collectors.toList());
    }
}
