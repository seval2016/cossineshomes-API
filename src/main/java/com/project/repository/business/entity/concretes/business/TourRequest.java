package com.project.repository.business.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.repository.business.entity.concretes.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TourRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tour_date", nullable = false)
    private LocalDateTime tourDate;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "tour_time", nullable = false)
    private LocalDateTime tourTime;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(nullable = false)
    private int status;

    @ManyToOne
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUser;

    @ManyToOne
    @JoinColumn(name = "guest_user_id", nullable = false)
    private User guestUser;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;
}
