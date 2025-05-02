package com.fintara.repositories;

import com.fintara.models.PegawaiDetails;
import com.fintara.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PegawaiDetailsRepository extends JpaRepository<PegawaiDetails, Integer> {
    Optional<PegawaiDetails> findByUser(User user);
}
