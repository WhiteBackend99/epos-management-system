package com.epos.backend.model.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoStatus;
import com.epos.backend.enums.PromoType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper=true)
@Data
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mst_promo", schema = "public")
public class Promo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "promo_no", nullable = false, unique = true)
    private String promoNo;

    @Column(name = "promo_name", nullable = false)
    private String promoName;

    @Column(name = "promo_code")
    private String promoCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "apply_type", nullable = false)
    private PromoApplyType applyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "promo_type", nullable = false)
    private PromoType promoType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PromoStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Builder.Default
    @Column(name = "priority", nullable = false)
    private Long priority = 0L;

    @Builder.Default
    @Column(name = "stackable", nullable = false)
    private Boolean stackable = false;

    @Column(name = "max_usage")
    private Long maxUsage;

    @Builder.Default
    @Column(name = "current_usage", nullable = false)
    private Long currentUsage = 0L;

    @Column(name = "max_usage_per_customer")
    private Long maxUsagePerCustomer;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "promo", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<PromoRule> rules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "promo", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<PromoReward> rewards = new ArrayList<>();

}
