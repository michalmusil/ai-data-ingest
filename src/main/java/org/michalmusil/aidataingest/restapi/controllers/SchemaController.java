package org.michalmusil.aidataingest.restapi.controllers;

import org.michalmusil.aidataingest.application.dtos.in.CreateSchemaDto;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.michalmusil.aidataingest.application.usecases.CreateSchemaUseCase;
import org.michalmusil.aidataingest.application.usecases.DeleteSchemaUseCase;
import org.michalmusil.aidataingest.application.usecases.GetAllSchemasUseCase;
import org.michalmusil.aidataingest.application.usecases.GetSchemaByIdUseCase;
import org.michalmusil.aidataingest.domain.entities.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schemas")
public class SchemaController {

    private final CreateSchemaUseCase createSchemaUseCase;
    private final DeleteSchemaUseCase deleteSchemaUseCase;
    private final GetAllSchemasUseCase getAllSchemasUseCase;
    private final GetSchemaByIdUseCase getSchemaByIdUseCase;

    @Autowired
    public SchemaController(
            CreateSchemaUseCase createSchemaUseCase,
            DeleteSchemaUseCase deleteSchemaUseCase,
            GetAllSchemasUseCase getAllSchemasUseCase,
            GetSchemaByIdUseCase getSchemaByIdUseCase) {
        this.createSchemaUseCase = createSchemaUseCase;
        this.deleteSchemaUseCase = deleteSchemaUseCase;
        this.getAllSchemasUseCase = getAllSchemasUseCase;
        this.getSchemaByIdUseCase = getSchemaByIdUseCase;
    }

    /**
     * GET /api/schemas : Retrieves a list of all data schemas.
     *
     * @return A list of SchemaDto objects.
     */
    @GetMapping
    public ResponseEntity<List<SchemaDto>> getAllSchemas() {
        var schemas = getAllSchemasUseCase.execute(null);
        return ResponseEntity.ok(schemas);
    }

    /**
     * GET /api/schemas/dataTypes : Retrieves a list of all available DataType enum values.
     *
     * @return A list of string representations of the DataType constants.
     */
    @GetMapping("/dataTypes")
    public ResponseEntity<List<String>> getAllDataTypes() {
        var fieldTypes = Arrays.stream(DataType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fieldTypes);
    }

    /**
     * GET /api/schemas/{id} : Retrieves a single schema definition by ID.
     *
     * @param id The ID of the schema.
     * @return The SchemaDto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SchemaDto> getSchemaById(@PathVariable Long id) {
        var schema = getSchemaByIdUseCase.execute(id);
        return ResponseEntity.ok(schema);
    }

    /**
     * POST /api/schemas : Creates a new data schema definition.
     *
     * @param dto The schema definition details and nested fields.
     * @return The created SchemaDto with its ID.
     */
    @PostMapping
    public ResponseEntity<SchemaDto> createSchema(@RequestBody CreateSchemaDto dto) {
        var createdSchema = createSchemaUseCase.execute(dto);
        return new ResponseEntity<>(createdSchema, HttpStatus.CREATED);
    }

    /**
     * DELETE /api/schemas/{id} : Deletes a schema definition by ID.
     * Deletion cascades to all associated Fields and IngestedRecords.
     *
     * @param id The ID of the schema to delete.
     * @return HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteSchema(@PathVariable Long id) {
        deleteSchemaUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}