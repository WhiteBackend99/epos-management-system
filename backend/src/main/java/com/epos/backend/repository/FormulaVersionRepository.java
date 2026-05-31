package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.FormulaVersion;

public interface FormulaVersionRepository extends JpaRepository<FormulaVersion, Long> {

    @Query(value = """
            SELECT mfv.*
            FROM mst_formula_version mfv
                JOIN mst_formula mf ON mf.id = mfv.formula_id
            WHERE mf.formula_code = :formulaCode
                AND mfv.active_flag = true
                AND mfv.effective_start <= CURRENT_TIMESTAMP
                AND (mfv.effective_end IS NULL OR mfv.effective_end >= CURRENT_TIMESTAMP)
            ORDER BY mfv.priority ASC, mfv.version_no DESC
            LIMIT 1
            """, nativeQuery = true)
    public Optional<FormulaVersion> findActiveVersion(@Param("formulaCode") String formulaCode);
}
