package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.config.security.JwtUtils;
import com.sakuBCA.dtos.customerDTO.CustomerProfileResponseDTO;
import com.sakuBCA.dtos.customerDTO.CustomerProfileUpdateDTO;
import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Autowired
    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(CustomerDetailsService.class);

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

    private void validateAccessToProfile(User requester, User targetUser) {
        boolean isSuperAdmin = requester.getRole().getName().equalsIgnoreCase("SUPER_ADMIN");
        boolean isOwner = requester.getId().equals(targetUser.getId());

        if (!isSuperAdmin && !isOwner) {
            throw new CustomException("Anda tidak memiliki akses untuk melihat data ini", HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public CustomerProfileResponseDTO getCustomerProfile(String token, UUID id) {
        String extractedToken = jwtUtils.extractToken(token);
        String requesterEmail = jwtUtils.getUsername(extractedToken);

        User requester = userService.findByEmail(requesterEmail);
        User targetUser = userService.getPegawaiUserById(id); // bisa disesuaikan jadi getUserById jika lebih generik

        validateAccessToProfile(requester, targetUser);

        CustomerDetails customerDetails = customerDetailsRepository.findByUser(targetUser)
                .orElseThrow(() -> new CustomException("Customer tidak ditemukan", HttpStatus.NOT_FOUND));

        // Mapping otomatis dari entity ke DTO
        CustomerProfileResponseDTO response = modelMapper.map(customerDetails, CustomerProfileResponseDTO.class);

        // Set atribut yang berasal dari entitas `User`
        response.setName(targetUser.getName());
        response.setEmail(targetUser.getEmail());

        return response;
    }

    // Save customer details
    @Transactional
    public CustomerDetails saveCustomerDetails(CustomerDetails customerDetails) {
        return customerDetailsRepository.save(customerDetails);
    }

    // Metode untuk mengisi data dari DTO ke entitas
    private void updateCustomerDetailsFromDTO(CustomerDetails customerDetails, CustomerProfileUpdateDTO dto) {
        customerDetails.setTtl(LocalDate.parse(dto.getTtl()));
        customerDetails.setKtpUrl(dto.getKtpUrl());
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
    public String updateCustomerDetails(UUID id, String token, CustomerProfileUpdateDTO dto) {
        logger.info("Memulai proses update data customer untuk ID: {}", id);
        String extractedToken = jwtUtils.extractToken(token);
        String email = jwtUtils.getUsername(extractedToken);

        logger.debug("Email dari token JWT: {}", email);

        User loggedInUser = userService.findByEmail(email);
        if (loggedInUser == null) {
            logger.warn("User dengan email {} tidak ditemukan", email);
            throw new CustomException("User tidak ditemukan", HttpStatus.UNAUTHORIZED);
        }

        User targetUser = userService.findById(id);
        if (targetUser == null) {
            logger.warn("Customer dengan ID {} tidak ditemukan", id);
            throw new CustomException("Data customer tidak ditemukan", HttpStatus.NOT_FOUND);
        }

        boolean isSameUser = loggedInUser.getId().equals(targetUser.getId());
        boolean isSuperAdmin = loggedInUser.getRole().getName().equalsIgnoreCase("SUPER_ADMIN");

        if (!isSameUser && !isSuperAdmin) {
            logger.warn("User {} mencoba mengakses data milik {} tanpa izin", loggedInUser.getId(), targetUser.getId());
            throw new CustomException("Anda tidak memiliki izin untuk mengubah data ini", HttpStatus.FORBIDDEN);
        }

        CustomerDetails customerDetails = getCustomerDetailsByUser(targetUser);
        if (customerDetails == null) {
            customerDetails = new CustomerDetails();
            customerDetails.setUser(targetUser);
            logger.debug("CustomerDetails baru dibuat untuk user ID: {}", targetUser.getId());
        }

        updateCustomerDetailsFromDTO(customerDetails, dto);
        saveCustomerDetails(customerDetails);

        logger.info("Data customer berhasil diupdate untuk ID: {}", id);
        return "Customer details updated successfully!";
    }

}
