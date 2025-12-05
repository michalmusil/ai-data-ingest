package org.michalmusil.aidataingest.restapi.controllers;

import org.michalmusil.aidataingest.application.dtos.in.CreateIngestedRecordDto;
import org.michalmusil.aidataingest.application.dtos.out.IngestedRecordDto;
import org.michalmusil.aidataingest.application.usecases.DeleteIngestedRecordUseCase;
import org.michalmusil.aidataingest.application.usecases.GetIngestedRecordByIdUseCase;
import org.michalmusil.aidataingest.application.usecases.GetIngestedRecordsOfSchemaUseCase;
import org.michalmusil.aidataingest.application.usecases.IngestRecordUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class IngestedRecordController {
    private final IngestRecordUseCase ingestRecordUseCase;
    private final DeleteIngestedRecordUseCase deleteIngestedRecordUseCase;
    private final GetIngestedRecordsOfSchemaUseCase getIngestedRecordsOfSchemaUseCase;
    private final GetIngestedRecordByIdUseCase getIngestedRecordByIdUseCase;

    public IngestedRecordController(
            IngestRecordUseCase ingestRecordUseCase,
            DeleteIngestedRecordUseCase deleteIngestedRecordUseCase,
            GetIngestedRecordsOfSchemaUseCase getIngestedRecordsOfSchemaUseCase, // Changed parameter name
            GetIngestedRecordByIdUseCase getIngestedRecordByIdUseCase) {
        this.ingestRecordUseCase = ingestRecordUseCase;
        this.deleteIngestedRecordUseCase = deleteIngestedRecordUseCase;
        this.getIngestedRecordsOfSchemaUseCase = getIngestedRecordsOfSchemaUseCase;
        this.getIngestedRecordByIdUseCase = getIngestedRecordByIdUseCase;
    }

    /**
     * GET /api/records/bySchema/{schemaId} : Retrieves a list of all ingested records for a specific schema.
     * Note: This is nested under /api/records for simplicity, but logically serves a schema context.
     *
     * @param schemaId The ID of the Schema to filter records by.
     * @return A list of IngestedRecordDto objects.
     */
    @GetMapping("/bySchema/{schemaId}")
    public ResponseEntity<List<IngestedRecordDto>> getRecordsBySchemaId(@PathVariable Long schemaId) {
        var records = getIngestedRecordsOfSchemaUseCase.execute(schemaId);
        return ResponseEntity.ok(records);
    }

    /**
     * GET /api/records/{id} : Retrieves a single ingested record by ID.
     *
     * @param id The ID of the record.
     * @return The IngestedRecordDto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IngestedRecordDto> getRecordById(@PathVariable Long id) {
        var record = getIngestedRecordByIdUseCase.execute(id);
        return ResponseEntity.ok(record);
    }

    /**
     * POST /api/records : Ingests a new record based on a known schema.
     *
     * @param dto The record data, including the schema ID and field values.
     * @return The created IngestedRecordDto with its ID.
     */
    @PostMapping
    public ResponseEntity<IngestedRecordDto> ingestRecord(@RequestBody CreateIngestedRecordDto dto) {
        var createdRecord = ingestRecordUseCase.execute(dto);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    /**
     * DELETE /api/records/{id} : Deletes an ingested record by ID.
     *
     * @param id The ID of the record to delete.
     * @return HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteRecord(@PathVariable Long id) {
        deleteIngestedRecordUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}