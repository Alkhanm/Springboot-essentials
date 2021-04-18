package com.github.alkhanm.service;

import com.github.alkhanm.repository.MyUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final MyUserRepository repository;

    public MyUserDetailsService(MyUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        return repository.findByUsername(s).stream()
                .findAny()
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
