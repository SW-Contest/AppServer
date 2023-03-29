package com.artfolio.artfolio.domain;

import com.artfolio.artfolio.domain.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Auction extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long artStartPrice;

    @Column(nullable = false, updatable = false)
    private Long artFinalPrice;

    @Setter
    @OneToOne
    @JoinColumn(name = "art_bidder_id")
    private Member bidder;

    @Builder
    public Auction(Long artStartPrice, Long artFinalPrice) {
        this.artStartPrice = artStartPrice;
        this.artFinalPrice = artFinalPrice;
    }
}
