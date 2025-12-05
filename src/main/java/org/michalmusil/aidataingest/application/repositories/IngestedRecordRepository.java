package org.michalmusil.aidataingest.application.repositories;

import org.michalmusil.aidataingest.domain.entities.IngestedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngestedRecordRepository extends JpaRepository<IngestedRecord, Long> {
    List<IngestedRecord> findBySchemaId(Long schemaId);
}