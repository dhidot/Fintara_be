package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.config.security.JwtUtils;
import com.sakuBCA.dtos.customerDTO.UpdateCustomerDetailsDTO;
import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CustomerDetailsService {
    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PegawaiDetailsService pegawaiDetailsService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private JwtUtils jwtUtils;


    // find customer details by user
    public CustomerDetails getCustomerDetailsByUser(User user) {
        return customerDetailsRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Customer details not found", HttpStatus.NOT_FOUND));
    }

    // find customer details by id
    public CustomerDetails getCustomerDetailsById(UUID id) {
        return customerDetailsRepository.findById(id)
                .orElseThrow(() -> new CustomException("Customer details not found", HttpStatus.NOT_FOUND));
    }

    // Save customer details
    @Transactional
    public CustomerDetails saveCustomerDetails(CustomerDetails customerDetails) {
        return customerDetailsRepository.save(customerDetails);
    }

    // Metode untuk mengisi data dari DTO ke entitas
    private void updateCustomerDetailsFromDTO(CustomerDetails customerDetails, UpdateCustomerDetailsDTO dto) {
        customerDetails.setTtl(LocalDate.parse(dto.getTtl()));
        customerDetails.setAlamat(dto.getAlamat());
        customerDetails.setNoTelp(dto.getNoTelp());
        customerDetails.setNik(dto.getNik());
        customerDetails.setNamaIbuKandung(dto.getNamaIbuKandung());
        customerDetails.setPekerjaan(dto.getPekerjaan());
        customerDetails.setGaji(BigDecimal.valueOf(dto.getGaji()));
        customerDetails.setNoRek(dto.getNoRek());
        customerDetails.setStatusRumah(dto.getStatusRumah());
    }

    @Transactional
    public String updateCustomerDetails(String token, UpdateCustomerDetailsDTO dto) {
        // Ambil email dari token
        String email = jwtUtils.getUsername(token.replace("Bearer ", ""));
        User user = userService.getPegawaiByEmail(email);

        // Pastikan hanya pengguna yang login yang bisa mengubah datanya
        if (user == null) {
            throw new CustomException("User tidak ditemukan atau tidak memiliki akses", HttpStatus.FORBIDDEN);
        }

        // Cek apakah user sudah punya detail customer
        CustomerDetails customerDetails = getCustomerDetailsByUser(user);

        if (customerDetails == null) {
            customerDetails = new CustomerDetails();
            customerDetails.setUser(user);
        }

        // Menggunakan DTO untuk mengisi data
        updateCustomerDetailsFromDTO(customerDetails, dto);

        // Simpan ke database
        saveCustomerDetails(customerDetails);
        return "Customer details updated successfully!";
    }
}
