package com.sakuBCA.repositories;

import com.sakuBCA.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.customerDetails IS NOT NULL")
    List<User> findAllWithCustomer();

    @Query("SELECT u FROM User u WHERE u.pegawaiDetails IS NOT NULL")
    List<User> findAllWithPegawai();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.pegawaiDetails WHERE u.id = :userId")
    Optional<User> findByIdWithPegawai(@Param("userId") Long userId);

    Optional<User> findByEmail(String email);
}
