package com.epos.backend.model.entity;

import java.math.BigDecimal;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@Table(name = "trx_sales_promo", schema = "public")
public class TrxSalesPromo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sales_id")
    private Long salesId;

    @Column(name = "sales_no")
    private String salesNo;

    @Column(name = "promo_id")
    private Long promoId;

    @Column(name = "promo_no")
    private String promoNo;

    @Column(name = "promo_name")
    private String promoName;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "promo_snapshot", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> promoSnapshot;

}
