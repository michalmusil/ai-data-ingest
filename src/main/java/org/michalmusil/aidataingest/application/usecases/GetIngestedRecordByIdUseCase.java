package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.out.IngestedRecordDto;
import org.michalmusil.aidataingest.application.exceptions.ResourceNotFoundException;
import org.michalmusil.aidataingest.application.repositories.IngestedRecordRepository;
import org.springframework.stereotype.Component;

@Component
public class GetIngestedRecordByIdUseCase implements UseCase<Long, IngestedRecordDto> {

    private final IngestedRecordRepository recordRepository;

    public GetIngestedRecordByIdUseCase(IngestedRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public IngestedRecordDto execute(Long recordId) {
        return recordRepository.findById(recordId)
                .map(IngestedRecordDto::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Ingested Record", recordId));
    }
}