package com.fintara.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

        import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {
    @Id
    @GeneratedValue(generator = "UUID", strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Nama cabang tidak boleh kosong")
    private String name;

    @NotBlank(message = "Alamat cabang tidak boleh kosong")
    private String address;

    @NotNull(message = "Latitude tidak boleh kosong")
    private Double latitude;

    @NotNull(message = "Longitude tidak boleh kosong")
    private Double longitude;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonBackReference
    private List<PegawaiDetails> pegawaiList;
}
