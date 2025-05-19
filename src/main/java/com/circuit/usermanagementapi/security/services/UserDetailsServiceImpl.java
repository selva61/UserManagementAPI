package com.circuit.usermanagementapi.security.services;

import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user details for username: {}", username);

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

            logger.debug("User found in database: {}, roles: {}", username, 
                         user.getRoles().stream().map(r -> r.getName().name()).toList());

            UserDetails userDetails = UserDetailsImpl.build(user);
            logger.debug("UserDetails built successfully with authorities: {}", userDetails.getAuthorities());

            return userDetails;
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with username: {}", username);
            throw e;
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw new UsernameNotFoundException("Error loading user: " + e.getMessage(), e);
        }
    }
}
