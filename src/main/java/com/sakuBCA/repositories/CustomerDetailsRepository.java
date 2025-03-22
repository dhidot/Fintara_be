package com.sakuBCA.repositories;

import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Integer> {
    Optional<CustomerDetails> findByUser(User user);
}
