/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.stockmanagement.domain.eventdraft;
//import org.openlmis.stockmanagement.dto.StockEventAdjustmentDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.BaseEntity;
import org.openlmis.stockmanagement.domain.ExtraDataConverter;
import org.openlmis.stockmanagement.domain.common.VvmApplicable;
import org.openlmis.stockmanagement.domain.identity.IdentifiableByOrderableLot;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_event_line_items_draft", schema = "stockmanagement")
public class StockEventLineItemDraft extends BaseEntity
    implements IdentifiableByOrderableLot, VvmApplicable {

  
  private UUID orderableId;

  private UUID lotId;

  private Integer quantity;

  @Column(name = "extradata", columnDefinition = "jsonb")
  @Convert(converter = ExtraDataConverter.class)
  private Map<String, String> extraData;

  private LocalDate occurredDate;

  private UUID reasonId;
  private String reasonFreeText;

  private UUID sourceId;
  private String sourceFreeText;

  private UUID destinationId;
  private String destinationFreeText;

  @ManyToOne()
  @JoinColumn(nullable = false)
  private StockEventDraft stockEventDraft;

  private String referenceNumber;
  private String cartonNumber;
  private String invoiceNumber;
  private Double unitPrice;

  private Integer quantityRejected;
  private UUID rejectionReasonId;
  private String rejectionReasonFreeText;

  private Integer quantityShipped;
  private Integer quantityOnDeliveryNote;

   // One-to-many relationship with Discrepancy
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "stock_event_line_item_draft_id") // foreign key in Draft Discrepancy table
   private List<DraftDiscrepancy> draftDiscrepancies;

}
