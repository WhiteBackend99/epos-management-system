package com.epos.backend.model.dto.context;

import java.math.BigDecimal;
import java.util.List;

import com.epos.backend.enums.PaymentMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoCalculationContext {

    private BigDecimal subtotal;
    private PaymentMethod paymentMethod;
    private String promoCode;
    private String customerName;
    private List<PromoCalculationItem> items;
 
    @Data
    @Builder
    public static class PromoCalculationItem {
        
        private Long productId;
        private Long categoryId;
        private Long qty;
        private BigDecimal price;
        private BigDecimal subtotal;
        
    }
}
