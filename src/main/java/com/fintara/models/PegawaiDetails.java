package com.fintara.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fintara.enums.StatusPegawai;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pegawai_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PegawaiDetails {
    @Id
    @GeneratedValue(generator = "UUID", strategy = GenerationType.AUTO)
    private UUID id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //unique
    @Column(unique = true, nullable = false)
    private String nip;

    @ManyToOne // ✅ Relasi dengan Branch
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch; // 🔥

    @Enumerated(EnumType.STRING)
    private StatusPegawai statusPegawai;
}
