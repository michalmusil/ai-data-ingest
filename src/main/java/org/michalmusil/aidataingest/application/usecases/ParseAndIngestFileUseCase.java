package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.out.IngestedRecordDto;
import org.michalmusil.aidataingest.application.services.InputFileIngestProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParseAndIngestFileUseCase implements UseCase<MultipartFile, List<IngestedRecordDto>> {

    private final GetAllSchemasUseCase getAllSchemasUseCase;
    private final InputFileIngestProcessor ingestProcessor;
    private final IngestRecordUseCase ingestRecordUseCase;

    public ParseAndIngestFileUseCase(
            GetAllSchemasUseCase getAllSchemasUseCase,
            InputFileIngestProcessor ingestProcessor,
            IngestRecordUseCase ingestRecordUseCase) {
        this.getAllSchemasUseCase = getAllSchemasUseCase;
        this.ingestProcessor = ingestProcessor;
        this.ingestRecordUseCase = ingestRecordUseCase;
    }

    @Override
    @Transactional
    public List<IngestedRecordDto> execute(MultipartFile file) {
        var availableSchemas = getAllSchemasUseCase.execute(null);
        var parsedRecords = ingestProcessor.parseToCorrespondingSchema(file, availableSchemas);

        return parsedRecords.stream()
                .map(ingestRecordUseCase::execute)
                .collect(Collectors.toList());
    }
}
