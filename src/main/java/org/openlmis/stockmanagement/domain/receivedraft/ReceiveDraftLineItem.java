package org.openlmis.stockmanagement.domain.receivedraft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.BaseEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "receive_draft", schema = "stockmanagement")
@Builder
public class ReceiveDraftLineItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(nullable = false)
    private ReceiveDraft receiveDraft;
    @Column(nullable = false)
    private Integer quantity;
    private UUID orderableId;
    private UUID lotId;
    @Column(nullable = false)
    private UUID userId;
    private String reasonFreeText;
    private UUID sourceId;
    private String sourceFreeText;
    private UUID destinationId;
    private String destinationFreeText;
    private String referenceNumber;
    private String cartonNumber;
    private String invoiceNumber;
    private Double unitPrice;
    private Integer quantityRejected;
    private UUID rejectionReasonId;
    private String rejectionReasonFreeText;
    private Integer quantityShipped;
}
