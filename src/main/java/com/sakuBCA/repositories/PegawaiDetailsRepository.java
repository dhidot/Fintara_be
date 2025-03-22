package com.sakuBCA.repositories;

import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PegawaiDetailsRepository extends JpaRepository<PegawaiDetails, Integer> {
    Optional<PegawaiDetails> findByUser(User user);
}
