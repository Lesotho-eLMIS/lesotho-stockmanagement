package org.openlmis.stockmanagement.dto.receive;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.receivedraft.ReceiveDraft;
import org.openlmis.stockmanagement.domain.receivedraft.ReceiveDraftLineItem;
import org.openlmis.stockmanagement.dto.StockEventLineItemDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveDraftDto {
    public UUID id;
    private UUID facilityId;
    private UUID programId;
    private UUID userId;
    private String status;
    private List<ReceiveDraftLineItemDto> lineItems;

    /**
     * Convert dto to jpa model.
     *
     * @return the converted jpa model object.
     */
    public ReceiveDraft toReceiveDraft(){
        List<ReceiveDraftLineItem> domainlines = this.lineItems
                .stream()
                .map(ReceiveDraftLineItemDto::toReceiveDraftLineItem)
                .collect(Collectors.toList());

        ReceiveDraft draft = new ReceiveDraft(facilityId,programId,status, domainlines);

        domainlines.forEach(lineItems-> {
            lineItems.setReceiveDraft(draft);
        });

        return draft;
    }

}
