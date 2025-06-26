package com.simpleecom.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleecom.userservice.model.Role;
import com.simpleecom.userservice.model.User;
import com.simpleecom.userservice.repository.RoleRepository;
import com.simpleecom.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
public void setup() {
    userRepository.deleteAll();
    roleRepository.deleteAll();

}

    @Test
    public void testUserRegistration() throws Exception {
        com.simpleecom.userservice.payload.RegistrationRequest registrationRequest = new com.simpleecom.userservice.payload.RegistrationRequest();
        registrationRequest.setUsername("testuser");
        registrationRequest.setEmail("testuser@example.com");
        registrationRequest.setPassword(passwordEncoder.encode("password"));
        registrationRequest.setRoles(null); // or set roles as needed

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserLogin() throws Exception {
        // First register user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("password"));

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        // Login request payload
        String loginPayload = "{ \"username\": \"testuser\", \"password\": \"password\" }";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isOk());
    }
}
