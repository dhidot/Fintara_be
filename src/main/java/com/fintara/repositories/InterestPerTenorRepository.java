package com.fintara.repositories;

import com.fintara.models.InterestPerTenor;
import com.fintara.models.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterestPerTenorRepository extends JpaRepository<InterestPerTenor, UUID> {

    List<InterestPerTenor> findByPlafond(Plafond plafond);

    List<InterestPerTenor> findByPlafondAndTenor(Plafond plafond, int tenor);

    boolean existsByPlafondAndTenor(Plafond plafond, int tenor);

}
