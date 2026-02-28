package com.migme.fusion.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @Column(name = "guid", unique = true, length = 36)
    private String guid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", length = 20)
    @Builder.Default
    private DeliveryStatus deliveryStatus = DeliveryStatus.SENT;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    public enum DeliveryStatus {
        SENT, DELIVERED, READ
    }
}
