package org.michalmusil.aidataingest.application.repositories;

import org.michalmusil.aidataingest.domain.entities.Schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemaRepository extends JpaRepository<Schema, Long> {
}
