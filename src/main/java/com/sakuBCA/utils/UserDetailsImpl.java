package com.sakuBCA.utils;

import com.sakuBCA.models.User;
import lombok.Getter;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails{

    @Getter
    private User user;
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(User user, String username, String password, List<GrantedAuthority> authorities) {
        this.user = user;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetails build(User user) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if(user.getRole() != null) {
            // Combine role-based authorities and feature-based authorities
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            // Extract features from the role
            if (user.getRole().getRoleFeatures() != null) {
                user.getRole().getRoleFeatures().forEach(roleFeature ->
                        grantedAuthorities.add(new SimpleGrantedAuthority("FEATURE_" + roleFeature.getFeature().getName()))
                );
            }
        }

        return new UserDetailsImpl(user, user.getEmail(), user.getPassword(), grantedAuthorities);
    }

    public List<String> getFeatures() {
        List<String> features = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().startsWith("FEATURE_")) {
                features.add(authority.getAuthority());
            }
        }
        return features;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}