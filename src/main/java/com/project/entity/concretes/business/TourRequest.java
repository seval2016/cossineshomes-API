package com.project.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.TourRequestEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tour_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TourRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate tourDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime tourTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private int status= TourRequestEnum.PENDING.getValue();


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDateTime updateAt;


    /**
     * İlişkiler
     */

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    @JsonIgnore
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "owner_user_id", nullable = false)
    @JsonIgnore
    private User ownerUser;

    @ManyToOne
    @JoinColumn(name = "guest_user_id", nullable = false)
    @JsonIgnore
    private User guestUser;

    @PrePersist
    private void onCreate() {
        createAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate(){
        updateAt= LocalDateTime.now();
    }
}
