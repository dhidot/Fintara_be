package com.sakuBCA.services;

import com.sakuBCA.dtos.superAdminDTO.CustomerDetailsRequest;
import com.sakuBCA.dtos.superAdminDTO.PegawaiDetailsRequest;
import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.dtos.exceptions.CustomException;
import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import com.sakuBCA.repositories.PegawaiDetailsRepository;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final PegawaiDetailsRepository pegawaiDetailsRepository;
    private final CustomerDetailsRepository customerDetailsRepository;
    private final JwtUtil jwtUtil;

    public String updatePegawaiDetails(String token, PegawaiDetailsRequest request) {
        // Ambil email dari token
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        // Cek apakah user sudah punya detail pegawai
        PegawaiDetails pegawaiDetails = pegawaiDetailsRepository.findByUser(user)
                .orElseGet(() -> PegawaiDetails.builder().user(user).build());

        // Set data baru
        pegawaiDetails.setNip(request.getNip());
        pegawaiDetails.setBranchId(request.getBranchId());
        pegawaiDetails.setStatusPegawai(StatusPegawai.valueOf(request.getStatusPegawai()));

        // Simpan ke database
        pegawaiDetailsRepository.save(pegawaiDetails);
        return "Pegawai details updated successfully!";
    }

    public String updateCustomerDetails(String token, CustomerDetailsRequest request) {
        // Ambil email dari token
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        // Cek apakah user sudah punya detail customer
        CustomerDetails customerDetails = customerDetailsRepository.findByUser(user)
                .orElseGet(() -> CustomerDetails.builder().user(user).build());

        // Set data baru
        customerDetails.setTtl(request.getTtl());
        customerDetails.setAlamat(request.getAlamat());
        customerDetails.setNoTelp(request.getNoTelp());
        customerDetails.setNik(request.getNik());
        customerDetails.setNamaIbuKandung(request.getNamaIbuKandung());
        customerDetails.setPekerjaan(request.getPekerjaan());
        customerDetails.setGaji(request.getGaji());
        customerDetails.setNoRek(request.getNoRek());
        customerDetails.setStatusRumah(request.getStatusRumah());
        customerDetails.setPlafond(request.getPlafond());

        // Simpan ke database
        customerDetailsRepository.save(customerDetails);
        return "Customer details updated successfully!";
    }
}

