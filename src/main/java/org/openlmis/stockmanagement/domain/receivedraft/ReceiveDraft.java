package org.openlmis.stockmanagement.domain.receivedraft;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.openlmis.stockmanagement.domain.BaseEntity;
import org.openlmis.stockmanagement.domain.card.StockCardLineItem;
import org.openlmis.stockmanagement.domain.event.StockEvent;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;
import static org.hibernate.annotations.LazyCollectionOption.FALSE;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "receive_draft", schema = "stockmanagement")
public class ReceiveDraft extends BaseEntity {
    @Column(nullable = false)
    private UUID facilityId;

    @Column(nullable = false)
    private UUID programId;

    @Column
    private String status;

    @LazyCollection(FALSE)
    @OneToMany(cascade = ALL, mappedBy = "receiveDraft")
    private List<ReceiveDraftLineItem> lineItems;

}
