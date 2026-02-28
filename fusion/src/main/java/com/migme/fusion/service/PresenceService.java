package com.migme.fusion.service;

import com.migme.fusion.model.entity.User;
import com.migme.fusion.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PresenceService {

    private static final Logger log = LoggerFactory.getLogger(PresenceService.class);

    private final UserRepository userRepository;

    public PresenceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(value = "userPresence", key = "#userId")
    public void setOnline(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPresenceStatus(User.PresenceStatus.ONLINE);
            userRepository.save(user);
            log.debug("User {} is now ONLINE", userId);
        });
    }

    @Transactional
    @CacheEvict(value = "userPresence", key = "#userId")
    public void setOffline(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPresenceStatus(User.PresenceStatus.OFFLINE);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            log.debug("User {} is now OFFLINE", userId);
        });
    }

    @Transactional
    @CacheEvict(value = "userPresence", key = "#userId")
    public void updatePresence(Long userId, String status) {
        try {
            User.PresenceStatus presenceStatus = User.PresenceStatus.valueOf(status.toUpperCase());
            userRepository.findById(userId).ifPresent(user -> {
                user.setPresenceStatus(presenceStatus);
                userRepository.save(user);
                log.debug("User {} presence updated to {}", userId, presenceStatus);
            });
        } catch (IllegalArgumentException e) {
            log.warn("Invalid presence status: {}", status);
        }
    }

    @Transactional
    @CacheEvict(value = "userPresence", key = "#userId")
    public void updateStatusMessage(Long userId, String statusMessage) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatusMessage(statusMessage);
            userRepository.save(user);
            log.debug("User {} status message updated", userId);
        });
    }

    @Cacheable(value = "userPresence", key = "#userId")
    @Transactional(readOnly = true)
    public Optional<User.PresenceStatus> getPresence(Long userId) {
        return userRepository.findById(userId).map(User::getPresenceStatus);
    }
}
