package com.epos.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomationGeneratorServices {

    private final SequenceServices sequenceServices;

    public String generateMemberCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long seq = sequenceServices.nextValue("MEMBER_CODE");
        return "MBR-" + date + "-" + String.format("%06d", seq);
    }

}
