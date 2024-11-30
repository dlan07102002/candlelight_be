package vn.duclan.candlelight_be.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import vn.duclan.candlelight_be.model.User;

public interface UserService extends UserDetailsService {
    public User findByUsername(String username);

    public User findById(int id);
}
