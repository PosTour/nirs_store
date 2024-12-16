package ru.bmstu.nirs.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.nirs.store.domain.User;
import ru.bmstu.nirs.store.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public void update(int id, User updatedUser) {
        var user = userRepository.findById(id);
        if (user.isPresent()) {
            updatedUser.setId(id);
            userRepository.save(updatedUser);
        }
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
