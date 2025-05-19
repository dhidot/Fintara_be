package com.fintara.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "interest_per_tenor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"plafond_id", "tenor"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestPerTenor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plafond_id", nullable = false)
    private Plafond plafond;

    @Column(nullable = false)
    @NotNull(message = "Tenor tidak boleh kosong")
    private Integer tenor; // dalam bulan, misal 6, 12, 18, 24...

    @Column(nullable = false)
    @NotNull(message = "Interest rate tidak boleh kosong")
    private BigDecimal interestRate; // misalnya 0.05 = 5%
}
