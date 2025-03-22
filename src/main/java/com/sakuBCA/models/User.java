package com.sakuBCA.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sakuBCA.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PegawaiDetails pegawaiDetails;

    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CustomerDetails customerDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;
}
