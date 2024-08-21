package com.project.entity.concretes.business;

        import com.fasterxml.jackson.annotation.JsonFormat;
        import com.project.entity.concretes.user.User;
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
@Builder
public class TourRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime tourDate;

    @Column(nullable = false)
    private LocalDateTime tourTime;

    @Column(nullable = false)
    private Integer status = 0;

    // Advert tablosuna foreign key ile bağlanıyor
    @ManyToOne
    @JoinColumn(name = "advert_id",nullable = false)
    private Advert advert;

    // Owner User tablosuna foreign key ile bağlanıyor
    @ManyToOne
    @JoinColumn(name = "owner_user_id",nullable = false)
    private User ownerUser;

    // Guest User tablosuna foreign key ile bağlanıyor
    @ManyToOne
    @JoinColumn(name = "guest_user_id",nullable = false)
    private User guestUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime updateAt;
}
