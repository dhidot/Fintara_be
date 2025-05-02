package com.fintara.config.security;

import com.fintara.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrNip) throws UsernameNotFoundException {
        return userRepository.findByEmail(usernameOrNip)
                .or(() -> userRepository.findByPegawaiDetails_Nip(usernameOrNip)) // Cek berdasarkan NIP
                .map(UserDetailsImpl::build)
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan: " + usernameOrNip));
    }
}