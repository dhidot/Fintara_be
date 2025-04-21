package com.sakuBCA.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sakuBCA.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email") // ⬅️ Email harus unik di database
})
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(generator = "UUID", strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;
    private String name;
    private String password;

    @Column(name = "is_first_login", nullable = false)
    private boolean isFirstLogin = true; // Default true untuk pegawai baru

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PegawaiDetails pegawaiDetails;

    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CustomerDetails customerDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    //    boolean
    public boolean isBranchManager() {
        return this.role != null && this.role.getName().equals("BRANCH_MANAGER");
    }

    public boolean isBackOffice() {
        return this.role != null && this.role.getName().equals("BACK_OFFICE");
    }

    public boolean isMarketing() {
        return this.role != null && this.role.getName().equals("MARKETING");
    }
}
