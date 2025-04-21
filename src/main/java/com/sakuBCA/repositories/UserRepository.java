package com.sakuBCA.repositories;

import com.sakuBCA.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // FindMarketingByBranch
    @Query("SELECT u FROM User u " +
            "JOIN PegawaiDetails p ON u.id = p.user.id " +
            "WHERE p.branch.id = :branchId AND u.role.name = 'MARKETING'")
    List<User> findMarketingByBranch(@Param("branchId") UUID branchId);

    @Query("SELECT u FROM User u WHERE u.role.name = 'CUSTOMER'")
    List<User> findAllWithCustomer();

    @Query("SELECT u FROM User u WHERE u.pegawaiDetails IS NOT NULL")
    List<User> findAllWithPegawai();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.pegawaiDetails WHERE u.id = :userId")
    Optional<User> findByIdWithPegawai(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.customerDetails WHERE u.id = :userId")
    Optional<User> findByIdWithCustomer(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {"customerDetails", "customerDetails.plafond"})
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.pegawaiDetails WHERE u.id = :id")
    Optional<User> findUserWithPegawaiDetailsById(@Param("id") UUID id);

    Optional<User> findByEmail(String email);

    // find by NIP
    Optional<User> findByPegawaiDetails_Nip(String nip);

    @Query("""
    SELECT u FROM User u
    LEFT JOIN u.pegawaiDetails pd
    WHERE u.email = :username OR pd.nip = :username
""")
    Optional<User> findByEmailOrNip(String username);

    boolean existsByEmail(String email);
}
