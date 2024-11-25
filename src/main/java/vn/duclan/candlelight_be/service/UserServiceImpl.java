package vn.duclan.candlelight_be.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.duclan.candlelight_be.repository.RoleRepository;
import vn.duclan.candlelight_be.repository.UserRepository;
import vn.duclan.candlelight_be.model.Role;
import vn.duclan.candlelight_be.model.User;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User: " + username + " not found"));

        if (user == null) {
            throw new UsernameNotFoundException("Account does not exists!");
        }
        org.springframework.security.core.userdetails.User userDetail = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), rolesToAuthorities(user.getRoleList()));
        return userDetail;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username is not valid"));
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Username is not valid"));
    }

    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }

}
