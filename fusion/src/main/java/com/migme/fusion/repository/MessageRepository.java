package com.migme.fusion.repository;

import com.migme.fusion.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByConversationIdAndDeletedFalseOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    Optional<Message> findByGuid(String guid);
}
