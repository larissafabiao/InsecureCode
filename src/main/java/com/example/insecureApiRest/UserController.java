package com.example.insecureApiRest;

import org.apache.tomcat.util.bcel.classfile.Constant;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado :: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/users")
    public User createUser(@Validated @RequestBody User user) {
        return userRepository.save(user);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser (@PathVariable(value = "id") Long userId, @Validated @RequestBody User userDetails) throws  ResourceNotFoundException {
        User user= userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        user.setEmail(userDetails.getEmail());
        user.setLastName(userDetails.getLastName());
        user.setFirstName(userDetails.getFirstName());
        user.setUpdatedAt(new Date());
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/{id}")
    public Map<String,Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @PostMapping("/user/generateToken/{id}")
    public String generateToken(@PathVariable(value = "id") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        String value = user.getFirstName() + user.getLastName() + user.getId() + user.getCreatedAt();
        String token = Base64.getEncoder().encodeToString(value.getBytes());
        return token;
    }

    @GetMapping("/user/getUser")
    public List<User> getOrdersUsingWhereClause(EntityManager em, String whereClause) {
        TypedQuery<User> query = em.createQuery(
                "SELECT * FROM users WHERE " + whereClause,
                User.class);
        return query.getResultList();
    }
}
