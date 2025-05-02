package com.sakuBCA.repositories;

import com.sakuBCA.models.PegawaiDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PegawaiRepository extends JpaRepository<PegawaiDetails, Long> {
    boolean existsByNip(String nip);

}
