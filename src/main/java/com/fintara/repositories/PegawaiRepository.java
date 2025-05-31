package com.fintara.repositories;

import com.fintara.models.Branch;
import com.fintara.models.PegawaiDetails;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PegawaiRepository extends JpaRepository<PegawaiDetails, Long> {
    boolean existsByNip(String nip);

    @Query("""
    SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
    FROM PegawaiDetails p
    WHERE p.branch = :branch AND p.user.role.name = :roleName
    """)
    boolean existsByBranchAndRoleName(@Param("branch") Branch branch, @Param("roleName") String roleName);

}
