package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.repositories.UserRepository;
import com.cdhi.projectivbackend.security.UserSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email);
        if (user == null)
            throw new UsernameNotFoundException(email);
        return new UserSS(user.getId(), user.getEmail(), user.getPassword(), user.getProfiles(), user.getEnabled());
    }
}
