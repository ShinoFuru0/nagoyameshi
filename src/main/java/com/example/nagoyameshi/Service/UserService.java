package com.example.nagoyameshi.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.PasswordResetTokenRepository;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository,
                       VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public User create(SignupForm signupForm) {
        User user = new User();
        
        Role role = roleRepository.findByName("ROLE_GENERAL")
            .orElseThrow(() -> new RuntimeException("Role not found: ROLE_GENERAL"));
        
        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);
        
        return userRepository.save(user);
    }

    @Transactional
    public void update(UserEditForm userEditForm) {
        User user = userRepository.getReferenceById(userEditForm.getId());
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setEmail(userEditForm.getEmail());      
        userRepository.save(user);
    }

    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
        passwordResetTokenRepository.deleteByUser(user);
    }

    public void createPasswordResetTokenForUser(String email, String token) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません。");
        }
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.save(myToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Transactional
    public boolean updatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null) {
            return false;
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        return true;
    }

    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());
    }

    @Transactional
    public void saveStripeCustomerId(User user, String stripeCustomerId) {
        user.setCustomerId(stripeCustomerId);
        userRepository.save(user);
    }

    @Transactional
    public void updateRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.setRole(role);
        userRepository.save(user);
    }

    public void refreshAuthenticationByRole(String newRole) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(newRole));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                currentAuthentication.getPrincipal(), 
                currentAuthentication.getCredentials(), 
                simpleGrantedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}
