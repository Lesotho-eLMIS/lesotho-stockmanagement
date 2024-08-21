package org.openlmis.stockmanagement.dto.receive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.physicalinventory.PhysicalInventoryLineItemAdjustment;
import org.openlmis.stockmanagement.domain.qualitychecks.Discrepancy;
import org.openlmis.stockmanagement.domain.receivedraft.ReceiveDraftLineItem;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiveDraftLineItemDto {
    private UUID orderableId;
    private UUID lotId;
    private Integer quantity;
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
   // private List<StockEventAdjustmentDto> stockAdjustments;
    //private List<DiscrepancyDto> discrepancies;

    ReceiveDraftLineItem toReceiveDraftLineItem() {
        return ReceiveDraftLineItem.builder()
                .orderableId(orderableId)
                .lotId(lotId)
                .quantity(quantity)
                .userId(userId)
                .reasonFreeText(reasonFreeText)
                .sourceId(sourceId)
                .sourceFreeText(sourceFreeText)
                .destinationId(destinationId)
                .destinationFreeText(destinationFreeText)
                .referenceNumber(referenceNumber)
                .cartonNumber(cartonNumber)
                .invoiceNumber(invoiceNumber)
                .unitPrice(unitPrice)
                .quantityRejected(quantityRejected)
                .rejectionReasonId(rejectionReasonId)
                .rejectionReasonFreeText(rejectionReasonFreeText)
                .quantityShipped(quantityShipped)
                .build();
    }
//    public ReceiveDraftLineItem build() {
//        return new ReceiveDraftLineItem(
//                orderableId, lotId, quantity, userId, reasonFreeText,
//                sourceId, sourceFreeText, destinationId, destinationFreeText,
//                referenceNumber, cartonNumber, invoiceNumber, unitPrice,
//                quantityRejected, rejectionReasonId, rejectionReasonFreeText,
//                quantityShipped);
//    }

    /**
     * Gets stock adjustments as {@link PhysicalInventoryLineItemAdjustment}.
     */
//    public List<PhysicalInventoryLineItemAdjustment> stockAdjustments() {
//        if (null == stockAdjustments) {
//            return emptyList();
//        }
//
//        return stockAdjustments
//                .stream()
//                .map(StockEventAdjustmentDto::toPhysicalInventoryLineItemAdjustment)
//                .collect(Collectors.toList());
//    }
    /**
     * Gets discrepancies as {@link Discrepancy}.
     */
//    public List<Discrepancy> discrepancies() {
//        if (null == discrepancies) {
//            return emptyList();
//        }
//
//        List<Discrepancy> discrepanciesList = new ArrayList<>();
//        for (DiscrepancyDto discrepancydto : discrepancies) {
//            discrepanciesList.add(discrepancydto.toDiscrepancy());
//        }
//        return discrepanciesList;
//    }

}
