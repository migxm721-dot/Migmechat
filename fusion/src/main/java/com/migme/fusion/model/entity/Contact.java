package com.migme.fusion.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contacts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "contact_user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_user_id", nullable = false)
    private User contactUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_status", length = 20)
    @Builder.Default
    private RelationshipStatus relationshipStatus = RelationshipStatus.FRIEND;

    public enum RelationshipStatus {
        FRIEND, BLOCKED, PENDING
    }
}
