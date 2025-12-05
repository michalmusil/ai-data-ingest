package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.exceptions.ResourceNotFoundException;
import org.michalmusil.aidataingest.application.repositories.IngestedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteIngestedRecordUseCase implements UseCase<Long, Void> {

    private final IngestedRecordRepository recordRepository;

    @Autowired
    public DeleteIngestedRecordUseCase(IngestedRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public Void execute(Long recordId) {
        if (!recordRepository.existsById(recordId)) {
            throw new ResourceNotFoundException("IngestedRecord", recordId);
        }

        recordRepository.deleteById(recordId);

        return null;
    }
}
