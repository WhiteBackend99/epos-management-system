package com.epos.backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epos.backend.model.entity.SysSequence;
import com.epos.backend.repository.SysSequenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SequenceServices {

    private final SysSequenceRepository sequenceRepository;

    @Transactional
    public Long nextValue(String sequenceCode) {
        SysSequence data = sequenceRepository.findBySequenceCodeForUpdate(sequenceCode).orElseThrow(() -> new RuntimeException("Sequence tidak ditemukan"));
        Long nextValue = data.getCurrentValue() + 1;
        data.setCurrentValue(nextValue);
        data.setUpdatedAt(LocalDateTime.now());
        sequenceRepository.save(data);
        return nextValue;
    }

}
