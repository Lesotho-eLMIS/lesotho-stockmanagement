package org.openlmis.stockmanagement.repository;

import org.openlmis.stockmanagement.domain.receivedraft.ReceiveDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceiveDraftRepository extends JpaRepository<ReceiveDraft, UUID> {
}
