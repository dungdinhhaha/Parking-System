package com.parking.system.entity;

import com.parking.system.enums.FeedbackStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
public class Feedback extends BaseEntity {

    @Column(length = 2000)
    private String content;

    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private FeedbackStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_session_id")
    private ParkingSession parkingSession;

    public void markRead() {
        status = FeedbackStatus.READ;
    }

    public void resolve() {
        status = FeedbackStatus.RESOLVED;
    }
}
