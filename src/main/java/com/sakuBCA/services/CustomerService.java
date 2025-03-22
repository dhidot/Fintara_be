package com.sakuBCA.services;

import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerDetailsRepository customerDetailsRepository;

    public CustomerDetails saveCustomerDetails(CustomerDetails customerDetails) {
        return customerDetailsRepository.save(customerDetails);
    }
}
