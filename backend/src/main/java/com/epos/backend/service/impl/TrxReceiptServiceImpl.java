package com.epos.backend.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.enums.ReceiptAction;
import com.epos.backend.enums.ReceiptReferenceType;
import com.epos.backend.enums.ReceiptStatus;
import com.epos.backend.enums.ReceiptType;
import com.epos.backend.enums.SalesReturnStatus;
import com.epos.backend.enums.SalesStatus;
import com.epos.backend.model.dto.request.PrintReceiptRequest;
import com.epos.backend.model.dto.request.ReprintReceiptRequest;
import com.epos.backend.model.dto.response.TrxReceiptResponse;
import com.epos.backend.model.entity.TrxReceipt;
import com.epos.backend.model.entity.TrxReceiptLog;
import com.epos.backend.model.entity.TrxSales;
import com.epos.backend.model.entity.TrxSalesReturn;
import com.epos.backend.repository.TrxReceiptLogRepository;
import com.epos.backend.repository.TrxReceiptRepository;
import com.epos.backend.repository.TrxSalesRepository;
import com.epos.backend.repository.TrxSalesReturnRepository;
import com.epos.backend.service.TrxReceiptService;
import com.epos.backend.template.impl.ReceiptTemplateImpl;
import com.epos.backend.util.GeneratorUtil;
import com.epos.backend.util.ParseUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrxReceiptServiceImpl extends Services implements TrxReceiptService {
    
    private final TrxReceiptRepository trxReceiptRepository;
    private final TrxReceiptLogRepository trxReceiptLogRepository;
    private final TrxSalesRepository trxSalesRepository;
    private final TrxSalesReturnRepository trxSalesReturnRepository;
    private final ReceiptTemplateImpl receiptTemplateImpl;

    private TrxReceiptResponse sectionPrintSalesReceipt(PrintReceiptRequest request, HttpServletRequest servletRequest) {
        TrxSales dataSales = trxSalesRepository.findBySalesNo(request.getReferenceNo()).orElseThrow(() -> new EntityNotFoundException("Penjualan tidak ditemukan"));
        if (SalesStatus.CANCELLED.equals(dataSales.getStatus())) {
            throw new IllegalArgumentException("Penjualan yang sudah dibatalkan tidak bisa cetak struk");
        }

        String receiptNo = GeneratorUtil.generateReceiptNo();
        String tempalteVersion = receiptTemplateImpl.templateVersion();
        
        Map<String, Object> payload = buildDataSalesPayload(receiptNo, tempalteVersion, dataSales);
        String receiptText = receiptTemplateImpl.renderText(payload);

        TrxReceipt newData = TrxReceipt.builder()
            .receiptNo(receiptNo)
            .referenceType(ReceiptReferenceType.POS_SALES)
            .referenceId(dataSales.getId())
            .referenceNo(dataSales.getSalesNo())
            .receiptType(ReceiptType.SALES_RECEIPT)
            .templateVersion(tempalteVersion)
            .receiptPayload(payload)
            .receiptText(receiptText)
            .printCount(1l)
            .status(ReceiptStatus.ACTIVE)
            .firstPrintedBy(getCurrentUsername())
            .firstPrintedAt(LocalDateTime.now())
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .build();
        
        TrxReceipt dataSave = trxReceiptRepository.save(newData);
        saveLog(dataSave, ReceiptAction.PRINT, request.getNotes(), servletRequest);

        return buildEntityToResponse(dataSave);
    }

    private TrxReceiptResponse sectionPrintReturnReceipt(PrintReceiptRequest request, HttpServletRequest servletRequest) {
        TrxSalesReturn dataSalesReturn = trxSalesReturnRepository.findByReturnNo(request.getReferenceNo()).orElseThrow(() -> new EntityNotFoundException("Pengembalian penjualan tidak ditemukan"));
        if (SalesReturnStatus.CANCELLED.equals((dataSalesReturn.getReturnStatus()))) {
            throw new IllegalArgumentException("Pengembalian yang sudah dibatalkan tidak bisa cetak struk");
        }

        String receiptNo = GeneratorUtil.generateReceiptNo();
        String tempalteVersion = receiptTemplateImpl.templateVersion();
        
        Map<String, Object> payload = buildDataReturnPayload(receiptNo, tempalteVersion, dataSalesReturn);
        String receiptText = receiptTemplateImpl.renderText(payload);

        TrxReceipt newData = TrxReceipt.builder()
            .receiptNo(receiptNo)
            .referenceType(ReceiptReferenceType.SALES_RETURN)
            .referenceId(dataSalesReturn.getId())
            .referenceNo(dataSalesReturn.getReturnNo())
            .receiptType(ReceiptType.RETURN_RECEIPT)
            .templateVersion(tempalteVersion)
            .receiptPayload(payload)
            .receiptText(receiptText)
            .printCount(1l)
            .status(ReceiptStatus.ACTIVE)
            .firstPrintedBy(getCurrentUsername())
            .firstPrintedAt(LocalDateTime.now())
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .build();
        
        TrxReceipt dataSave = trxReceiptRepository.save(newData);
        saveLog(dataSave, ReceiptAction.PRINT, request.getNotes(), servletRequest);

        return buildEntityToResponse(dataSave);
    }

    private TrxReceiptResponse sectionReprintExisting(TrxReceipt data, String notes, HttpServletRequest servletRequest) {
        if (ReceiptStatus.VOID.equals(data.getStatus())) {
            throw new IllegalArgumentException("Struk yang sudah VOID tidak bisa dicetak ulang");
        }

        data.setPrintCount(data.getPrintCount() + 1);
        data.setLastReprintedBy(getCurrentUsername());
        data.setLastReprintedAt(LocalDateTime.now());
        data.setUpdatedBy(getCurrentUsername());
        data.setUpdatedAt(now());

        TrxReceipt dataSave = trxReceiptRepository.save(data);

        return buildEntityToResponse(dataSave);
    }

    private TrxReceiptResponse buildEntityToResponse(TrxReceipt data) {
        return TrxReceiptResponse.builder()
            .id(data.getId())
            .receiptNo(data.getReceiptNo())
            .referenceType(data.getReferenceType())
            .referenceId(data.getReferenceId())
            .referenceNo(data.getReferenceNo())
            .receiptType(data.getReceiptType())
            .templateVersion(data.getTemplateVersion())
            .receiptPayload(data.getReceiptPayload())
            .receiptText(data.getReceiptText())
            .printCount(data.getPrintCount())
            .status(data.getStatus())
            .firstPrintedBy(data.getFirstPrintedBy())
            .firstPrintedAt(data.getFirstPrintedAt())
            .lastReprintedBy(data.getLastReprintedBy())
            .lastReprintedAt(data.getLastReprintedAt())
            .createdBy(data.getCreatedBy())
            .createdAt(data.getCreatedAt())
            .updatedBy(data.getUpdatedBy())
            .updatedAt(data.getUpdatedAt())
            .build();
    }

    private Map<String, Object> buildDataSalesPayload(String receiptNo, String templateVersion, TrxSales data) {
        List<Map<String, Object>> items = data.getDetails().stream()
            .map(dataDetail -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productName", dataDetail.getProductNameSnapshot());
                item.put("qty", dataDetail.getQty());
                item.put("price", dataDetail.getPriceSnapshot());
                item.put("subtotal", data.getSubtotal());
                return item;
            })
            .toList();

        List<Map<String, Object>> payments = data.getPayments().stream()
            .map(dataPayment -> {
                Map<String, Object> payment = new LinkedHashMap<>();
                payment.put("method", dataPayment.getPaymentMethod().name());
                payment.put("amount", dataPayment.getPaidAmount());
                return payment;
            })
            .toList();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("receiptNo", receiptNo);
        payload.put("templateVersion", templateVersion);
        payload.put("receiptType", ReceiptType.SALES_RECEIPT.name());
        payload.put("storeName", "EPOS STORE");
        payload.put("cashier", data.getCreatedBy());
        payload.put("referenceNo", data.getSalesNo());
        payload.put("transactionDate", ParseUtil.formatDateToStr(data.getCreatedAt()));
        payload.put("customerName", data.getCustomerName());
        payload.put("items", items);
        payload.put("payments", payments);
        payload.put("subtotal", data.getSubtotal());
        payload.put("discountAmount", data.getDiscountAmount());
        payload.put("taxAmount", data.getTaxAmount());
        payload.put("grandTotal", data.getGrandTotal());
        payload.put("paidAmount", data.getPaidAmount());
        payload.put("changeAmount", data.getChangeAmount());

        return payload;
    }

    private Map<String, Object> buildDataReturnPayload(String receiptNo, String templateVersion, TrxSalesReturn data) {
        List<Map<String, Object>> items = data.getDetails().stream()
            .map(dataDetail -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productName", dataDetail.getProductNameSnapshot());
                item.put("qty", dataDetail.getQtyReturn());
                item.put("price", dataDetail.getPriceSnapshot());
                item.put("subtotal", dataDetail.getSubtotalReturn());
                return item;
            })
            .toList();

        List<Map<String, Object>> payments = data.getRefundPayments().stream()
            .map(dataPayment -> {
                Map<String, Object> payment = new LinkedHashMap<>();
                payment.put("method", dataPayment.getRefundMethod().name());
                payment.put("amount", dataPayment.getRefundAmount());
                return payment;
            })
            .toList();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("receiptNo", receiptNo);
        payload.put("templateVersion", templateVersion);
        payload.put("receiptType", ReceiptType.RETURN_RECEIPT.name());
        payload.put("storeName", "EPOS STORE");
        payload.put("cashier", data.getCreatedBy());
        payload.put("referenceNo", data.getReturnNo());
        payload.put("originalSalesNo", data.getSalesNo());
        payload.put("transactionDate", ParseUtil.formatDateToStr(data.getCreatedAt()));
        payload.put("customerName", data.getCustomerName());
        payload.put("items", items);
        payload.put("payments", payments);
        payload.put("subtotal", data.getTotalReturnAmount());
        payload.put("discountAmount", 0);
        payload.put("taxAmount", 0);
        payload.put("grandTotal", data.getTotalReturnAmount());
        payload.put("paidAmount", data.getRefundAmount());
        payload.put("changeAmount", 0);

        return payload;
    }

    private void saveLog(TrxReceipt data, ReceiptAction action, String notes, HttpServletRequest servletRequest) {
        TrxReceiptLog newData = TrxReceiptLog.builder()
            .receipt(data)
            .receiptNo(data.getReceiptNo())
            .action(action)
            .printedBy(getCurrentUsername())
            .printedAt(LocalDateTime.now())
            .ipAddress(getClientIp(servletRequest))
            .userAgent(servletRequest.getHeader("User-Agent"))
            .notes(notes)
            .createdBy(getCurrentUsername())
            .createdAt(now())
            .build();
        trxReceiptLogRepository.save(newData);
    }

    @Transactional
    @Override
    public TrxReceiptResponse printReceipt(PrintReceiptRequest request, HttpServletRequest servletRequest) {
        Optional<TrxReceipt> dataExistingReceipt = trxReceiptRepository.findByReferenceTypeAndReferenceNo(request.getReferenceType(), request.getReferenceNo());

        if (dataExistingReceipt.isPresent()) {
            return sectionReprintExisting(dataExistingReceipt.get(), getCurrentUsername(), servletRequest);
        }

        if (ReceiptReferenceType.POS_SALES.equals(request.getReferenceType())) {
            return sectionPrintSalesReceipt(request, servletRequest);
        }

        if (ReceiptReferenceType.SALES_RETURN.equals(request.getReferenceType())) {
            return sectionPrintReturnReceipt(request, servletRequest);
        }

        throw new IllegalArgumentException("Tipe referensi tidak didukung");
    }

    @Transactional
    @Override
    public TrxReceiptResponse reprintReceipt(ReprintReceiptRequest request, HttpServletRequest servletRequest) {
        TrxReceipt data = trxReceiptRepository.findByReceiptNo(request.getReceiptNo()).orElseThrow(() -> new EntityNotFoundException("Struk tidak ditemukan"));
        return sectionReprintExisting(data, getCurrentUsername(), servletRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public TrxReceiptResponse getByReceiptNo(String receiptNo) {
        TrxReceipt data = trxReceiptRepository.findByReceiptNo(receiptNo).orElseThrow(() -> new EntityNotFoundException("Struk tidak ditemukan"));
        return buildEntityToResponse(data);
    }

}
