package com.sakuBCA.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sakuBCA.enums.StatusPegawai;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String nip;
    private Integer branchId;

    @Enumerated(EnumType.STRING)
    @Setter
    private StatusPegawai statusPegawai;
}
