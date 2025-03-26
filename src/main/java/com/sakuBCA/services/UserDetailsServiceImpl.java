package com.sakuBCA.services;

import com.sakuBCA.config.security.UserDetailsImpl;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.repositories.RoleFeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleFeatureRepository roleFeatureRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Ambil fitur dari role_feature berdasarkan role user
        List<String> features = roleFeatureRepository.findFeaturesByRoleId(user.getRole().getId());

        return new UserDetailsImpl(user.getEmail(), user.getPassword(), features);
    }
}
