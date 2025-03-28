package com.example.barun.services;

import com.example.barun.dto.LoginUserDto;
import com.example.barun.dto.RegisterUserDto;
import com.example.barun.entities.userEntities.User;
import com.example.barun.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public User registerTheUser(RegisterUserDto registerUserDto){
        try{
            User newUser = new User();
            newUser.setFullName(registerUserDto.getFullName());
            newUser.setUsername(registerUserDto.getUsername());
            newUser.setEmail(registerUserDto.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
            return userRepository.save(newUser);
        } catch (Exception e){
            throw new RuntimeException("User valid input! " + e);
        }

    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public String verifyTheUser(LoginUserDto user){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
            ));
            if(authentication.isAuthenticated()){
                return jwtService.generateToken(user.getUsername());
            } else {
                return "Fail!";
            }
        } catch (Exception e){
            throw new RuntimeException("User valid input! " + e);
        }
    }


    public User updateOrAddTheImage(Long userId, MultipartFile imageFile) throws IOException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setImageData(imageFile.getBytes());
            user.setImageType(imageFile.getContentType());
            user.setImageName(imageFile.getOriginalFilename());
            return userRepository.save(user);
        } else {
            throw new IOException("User not found!");
        }
    }

    public User updateImage(Long userId, MultipartFile imageFile) throws IOException {
        return updateOrAddTheImage(userId, imageFile);
    }

    public User addImage(Long userId, MultipartFile imageFile) throws IOException {
        return updateOrAddTheImage(userId, imageFile);
    }

    public User getUserById(Long id) {
         return userRepository.findById(id).orElse(null);
    }

    public User patchUserInfo(Long id, User user) throws IOException {
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            User updateUser = existingUser.get();
            if (user.getUsername()!=null){
                updateUser.setUsername(user.getUsername());
            }
            if (user.getEmail()!=null){
                updateUser.setEmail(user.getEmail());
            }
            if (user.getFullName()!=null){
                updateUser.setFullName(user.getFullName());
            }
            if (user.getPassword()!=null){
                updateUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userRepository.save(updateUser);
        } else{
            throw new IOException("User not found!");
        }
    }

    public User updateUserInfo(Long id, User user) throws IOException {
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setUsername(user.getUsername());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setFullName(user.getFullName());
            updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(updatedUser);
        } else{
            throw new IOException("User not found!");
        }
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

}
