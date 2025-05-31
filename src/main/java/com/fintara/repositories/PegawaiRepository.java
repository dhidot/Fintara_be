package com.fintara.repositories;

import com.fintara.models.Branch;
import com.fintara.models.PegawaiDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PegawaiRepository extends JpaRepository<PegawaiDetails, Long> {
    boolean existsByNip(String nip);

    boolean existsByBranchAndRoleName(Branch branch, String roleName);
}
