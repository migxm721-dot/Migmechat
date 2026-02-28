package com.migme.fusion.service;

import com.migme.fusion.model.entity.Contact;
import com.migme.fusion.model.entity.User;
import com.migme.fusion.repository.ContactRepository;
import com.migme.fusion.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Cacheable(value = "contactLists", key = "#userId")
    @Transactional(readOnly = true)
    public List<Contact> getContacts(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public byte[] getContactListData(Long userId) {
        List<Contact> contacts = getContacts(userId);
        StringBuilder sb = new StringBuilder();
        for (Contact contact : contacts) {
            if (!sb.isEmpty()) sb.append(",");
            sb.append(contact.getContactUser().getUsername())
              .append(":").append(contact.getContactUser().getPresenceStatus());
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    @CacheEvict(value = "contactLists", key = "#userId")
    public void addContact(Long userId, String contactUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User contactUser = userRepository.findByUsername(contactUsername)
                .orElseThrow(() -> new IllegalArgumentException("Contact user not found: " + contactUsername));

        if (contactRepository.existsByUserIdAndContactUserId(userId, contactUser.getId())) {
            log.debug("Contact already exists: {} -> {}", userId, contactUsername);
            return;
        }

        Contact contact = Contact.builder()
                .user(user)
                .contactUser(contactUser)
                .build();
        contactRepository.save(contact);
        log.info("Added contact {} for user {}", contactUsername, userId);
    }

    @Transactional
    @CacheEvict(value = "contactLists", key = "#userId")
    public void removeContact(Long userId, String contactUsername) {
        User contactUser = userRepository.findByUsername(contactUsername)
                .orElseThrow(() -> new IllegalArgumentException("Contact user not found: " + contactUsername));

        contactRepository.findByUserIdAndContactUserId(userId, contactUser.getId())
                .ifPresent(contactRepository::delete);

        log.info("Removed contact {} for user {}", contactUsername, userId);
    }
}
