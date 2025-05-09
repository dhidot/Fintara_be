package com.fintara.services;

import com.fintara.dtos.customerDTO.FirstTimeUpdateDTO;
import com.fintara.enums.JenisKelamin;
import com.fintara.exceptions.CustomException;
import com.fintara.utils.JwtUtils;
import com.fintara.dtos.customerDTO.CustomerProfileResponseDTO;
import com.fintara.dtos.customerDTO.CustomerProfileUpdateDTO;
import com.fintara.models.CustomerDetails;
import com.fintara.models.User;
import com.fintara.repositories.CustomerDetailsRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private RedisService redisService;

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
    private void updateCustomerDetailsFromDTO(CustomerDetails customerDetails, FirstTimeUpdateDTO dto) {
        customerDetails.setJenisKelamin(JenisKelamin.valueOf(String.valueOf(dto.getJenisKelamin())));
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
    public String updateOwnCustomerDetails(FirstTimeUpdateDTO dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        logger.info("Memulai proses update data customer untuk user: {}", email);

        User loggedInUser = userService.findByEmail(email);
        if (loggedInUser == null) {
            throw new CustomException("User tidak ditemukan", HttpStatus.UNAUTHORIZED);
        }

        if (!Boolean.TRUE.equals(loggedInUser.isFirstLogin())) {
            throw new CustomException("Anda sudah melakukan update pertama. Akses ditolak.", HttpStatus.FORBIDDEN);
        }

        CustomerDetails customerDetails = getCustomerDetailsByUser(loggedInUser);
        if (customerDetails == null) {
            customerDetails = new CustomerDetails();
            customerDetails.setUser(loggedInUser);
        }

        // Set semua data dari DTO
        updateCustomerDetailsFromDTO(customerDetails, dto);

        // Update first login status
        loggedInUser.setFirstLogin(false);
        userService.saveUser(loggedInUser);
        redisService.removeFirstLoginStatus(loggedInUser.getId().toString());

        // Simpan perubahan customer
        saveCustomerDetails(customerDetails);

        logger.info("Data customer berhasil diupdate untuk user ID: {}", loggedInUser.getId());
        return "Customer details updated successfully!";
    }

    public String uploadKtpPhoto(MultipartFile file) throws IOException {
        // Ambil user yang sedang login
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        logger.info("Memulai proses upload ktp untuk user: {}", email);

        User loggedInUser = userService.findByEmail(email);
        if (loggedInUser == null) {
            throw new CustomException("User tidak ditemukan", HttpStatus.UNAUTHORIZED);
        }

        CustomerDetails customerDetails = getCustomerDetailsByUser(loggedInUser);
        if (customerDetails == null) {
            customerDetails = new CustomerDetails();
            customerDetails.setUser(loggedInUser);
        }

        // Upload ke Cloudinary
        String uploadedUrl = cloudinaryService.uploadFile(file);

        // Simpan URL selfie ke entity
        customerDetails.setKtpUrl(uploadedUrl);
        saveCustomerDetails(customerDetails);

        logger.info("Upload selfie berhasil disimpan untuk user ID: {}", loggedInUser.getId());
        return uploadedUrl;
    }

    public String uploadSelfiePhoto(MultipartFile file) throws IOException {
        // Ambil user yang sedang login
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        logger.info("Memulai proses upload selfie untuk user: {}", email);

        User loggedInUser = userService.findByEmail(email);
        if (loggedInUser == null) {
            throw new CustomException("User tidak ditemukan", HttpStatus.UNAUTHORIZED);
        }

        CustomerDetails customerDetails = getCustomerDetailsByUser(loggedInUser);
        if (customerDetails == null) {
            customerDetails = new CustomerDetails();
            customerDetails.setUser(loggedInUser);
        }

        // Upload ke Cloudinary
        String uploadedUrl = cloudinaryService.uploadFile(file);

        // Simpan URL selfie ke entity
        customerDetails.setSelfieKtpUrl(uploadedUrl);
        saveCustomerDetails(customerDetails);

        logger.info("Upload selfie berhasil disimpan untuk user ID: {}", loggedInUser.getId());
        return uploadedUrl;
    }

//    @Transactional
//    public String updateOwnCustomerDetailsWithFile(CustomerProfileUpdateDTO dto, MultipartFile ktpPhoto, MultipartFile selfiePhoto) {
//        // Mengambil informasi user yang sedang login dari SecurityContext
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String email = userDetails.getUsername();
//
//        logger.info("Memulai proses update data customer untuk user yang sedang login dengan email: {}", email);
//
//        User loggedInUser = userService.findByEmail(email);
//        if (loggedInUser == null) {
//            logger.warn("User dengan email {} tidak ditemukan", email);
//            throw new CustomException("User tidak ditemukan", HttpStatus.UNAUTHORIZED);
//        }
//
//        if (!Boolean.TRUE.equals(loggedInUser.isFirstLogin())) {
//            logger.warn("User {} mencoba mengakses first time update setelah login pertama", loggedInUser.getId());
//            throw new CustomException("Anda sudah melakukan update pertama. Akses ditolak.", HttpStatus.FORBIDDEN);
//        }
//
//        CustomerDetails customerDetails = getCustomerDetailsByUser(loggedInUser);
//        if (customerDetails == null) {
//            customerDetails = new CustomerDetails();
//            customerDetails.setUser(loggedInUser);
//            logger.debug("CustomerDetails baru dibuat untuk user ID: {}", loggedInUser.getId());
//        }
//
//        // Upload KTP jika ada
//        if (ktpPhoto != null && !ktpPhoto.isEmpty()) {
//            try {
//                String ktpUrl = cloudinaryService.uploadImage(ktpPhoto.getBytes());
//                customerDetails.setKtpUrl(ktpUrl);
//            } catch (IOException e) {
//                logger.error("Terjadi kesalahan saat mengupload foto KTP", e);
//                throw new CustomException("Gagal mengupload foto KTP", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        if (selfiePhoto != null && !selfiePhoto.isEmpty()) {
//            try {
//                String selfieUrl = cloudinaryService.uploadImage(selfiePhoto.getBytes());
//                customerDetails.setSelfieKtpUrl(selfieUrl);
//            } catch (IOException e) {
//                throw new CustomException("Gagal mengupload selfie dengan KTP", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        // Update data lainnya dari DTO
//        updateCustomerDetailsFromDTO(customerDetails, dto);
//        // Update status isFirstLogin menjadi false setelah update berhasil
//        loggedInUser.setFirstLogin(false); // Asumsi bahwa ada properti `isFirstLogin` di entitas User
//        userService.saveUser(loggedInUser); // Simpan perubahan status isFirstLogin
//        redisService.removeFirstLoginStatus(loggedInUser.getId().toString());
//        // Simpan customer details yang telah diupdate
//        saveCustomerDetails(customerDetails);
//
//        logger.info("Data customer berhasil diupdate untuk user: {}", loggedInUser.getId());
//        return "Customer details updated successfully!";
//    }

//    @Transactional
//    public String updateCustomerDetails(UUID id, String token, CustomerProfileUpdateDTO dto, MultipartFile ktpPhoto) {
//        logger.info("Memulai proses update data customer untuk ID: {}", id);
//        String extractedToken = jwtUtils.extractToken(token);
//        String email = jwtUtils.getUsername(extractedToken);
//
//        logger.debug("Email dari token JWT: {}", email);
//
//        User loggedInUser = userService.findByEmail(email);
//        if (loggedInUser == null) {
//            logger.warn("User dengan email {} tidak ditemukan", email);
//            throw new CustomException("User tidak ditemukan", HttpStatus.UNAUTHORIZED);
//        }
//
//        User targetUser = userService.findById(id);
//        if (targetUser == null) {
//            logger.warn("Customer dengan ID {} tidak ditemukan", id);
//            throw new CustomException("Data customer tidak ditemukan", HttpStatus.NOT_FOUND);
//        }
//
//        boolean isSameUser = loggedInUser.getId().equals(targetUser.getId());
//        boolean isSuperAdmin = loggedInUser.getRole().getName().equalsIgnoreCase("SUPER_ADMIN");
//
//        if (!isSameUser && !isSuperAdmin) {
//            logger.warn("User {} mencoba mengakses data milik {} tanpa izin", loggedInUser.getId(), targetUser.getId());
//            throw new CustomException("Anda tidak memiliki izin untuk mengubah data ini", HttpStatus.FORBIDDEN);
//        }
//
//        CustomerDetails customerDetails = getCustomerDetailsByUser(targetUser);
//        if (customerDetails == null) {
//            customerDetails = new CustomerDetails();
//            customerDetails.setUser(targetUser);
//            logger.debug("CustomerDetails baru dibuat untuk user ID: {}", targetUser.getId());
//        }
//
//        // Meng-upload foto KTP ke Cloudinary
//        if (ktpPhoto != null && !ktpPhoto.isEmpty()) {
//            try {
//                String ktpUrl = cloudinaryService.uploadImage(ktpPhoto.getBytes()); // Mengupload foto ke Cloudinary
//                customerDetails.setKtpUrl(ktpUrl); // Menyimpan URL foto KTP ke entitas
//            } catch (IOException e) {
//                logger.error("Terjadi kesalahan saat mengupload foto KTP", e);
//                throw new CustomException("Gagal mengupload foto KTP", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        // Update data lainnya dari DTO
//        updateCustomerDetailsFromDTO(customerDetails, dto);
//        saveCustomerDetails(customerDetails);
//
//        logger.info("Data customer berhasil diupdate untuk ID: {}", id);
//        return "Customer details updated successfully!";
//    }
}

