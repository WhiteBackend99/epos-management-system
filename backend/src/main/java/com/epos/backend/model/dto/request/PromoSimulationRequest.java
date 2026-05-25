package com.epos.backend.model.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.epos.backend.enums.PaymentMethod;

import lombok.Data;

@Data
public class PromoSimulationRequest {

    private BigDecimal subtotal;
    private PaymentMethod paymentMethod;
    private String promoCode;
    
    private List<PromoSimulationItemRequest> items;

    @Data
    public static class PromoSimulationItemRequest {

        private Long productId;
        private Long categoryId;
        private Long qty;
        private BigDecimal price;
        private BigDecimal subtotal;

    }
}
