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

package org.openlmis.stockmanagement.service;

import java.util.UUID;
import java.util.stream.Collectors;

import org.openlmis.stockmanagement.domain.eventdraft.DraftDiscrepancy;
import org.openlmis.stockmanagement.domain.eventdraft.StockEventDraft;
import org.openlmis.stockmanagement.domain.eventdraft.StockEventLineItemDraft;
import org.openlmis.stockmanagement.dto.DraftDiscrepancyDto;
import org.openlmis.stockmanagement.dto.StockEventDraftDto;
import org.openlmis.stockmanagement.dto.StockEventLineItemDraftDto;
import org.openlmis.stockmanagement.repository.StockEventsDraftRepository;
import org.openlmis.stockmanagement.service.requisition.RejectionReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * A service that is in charge of saving stock events and generating stock cards and line items from
 * stock events.
 */
@Service
public class StockEventDraftService {

  @Autowired
  private StockEventsDraftRepository stockEventsDraftRepository;

  @Autowired
  private RejectionReasonService rejectionReasonService;

  /**
   * Get a page of draft stock events.
   *
   * @param programId  program id.
   * @param facilityId facility id.
   * @param pageable   page object.
   * @return page of draft stock events.
   */
  public Page<StockEventDraftDto> findDraftStockEvents(UUID programId, UUID facilityId, Pageable pageable) {
    Page<StockEventDraft> pageOfEvents = stockEventsDraftRepository
        .findByProgramIdAndFacilityId(programId, facilityId, pageable);

    return pageOfEvents.map(this::stockEventDraftToDto);  
  }

  /**
   * Create dto from jpa model.
   *
   * @param patient jpa model.
   * @return Patient created dto.
   */
  private StockEventDraftDto stockEventDraftToDto(StockEventDraft stockEventDraft) {
    return StockEventDraftDto.builder()
      .id(stockEventDraft.getId())
      .facilityId(stockEventDraft.getFacilityId())
      .programId(stockEventDraft.getProgramId())
      .documentNumber(stockEventDraft.getDocumentNumber())
      .signature(stockEventDraft.getSignature())
      .userId(stockEventDraft.getUserId())
      .processedDate(stockEventDraft.getProcessedDate().toLocalDateTime())
      .lineItems(stockEventDraft.getLineItemsDraft() != null
          ? stockEventDraft.getLineItemsDraft().stream()
                                       .map(this::stockEventLineItemDraftToDto)
                                       .collect(Collectors.toList())
          : null)
      .build();
  }

  private StockEventLineItemDraftDto stockEventLineItemDraftToDto(StockEventLineItemDraft entity) {
    if (entity == null) {
      return null;
    }

    return StockEventLineItemDraftDto.builder()
      .orderableId(entity.getOrderableId())
      .lotId(entity.getLotId())
      .quantity(entity.getQuantity())
      .extraData(entity.getExtraData())
      .occurredDate(entity.getOccurredDate())
      .reasonId(entity.getReasonId())
      .reasonFreeText(entity.getReasonFreeText())
      .sourceId(entity.getSourceId())
      .sourceFreeText(entity.getSourceFreeText())
      .destinationId(entity.getDestinationId())
      .destinationFreeText(entity.getDestinationFreeText())
      .referenceNumber(entity.getReferenceNumber())
      .cartonNumber(entity.getCartonNumber())
      .invoiceNumber(entity.getInvoiceNumber())
      .unitPrice(entity.getUnitPrice())
      .quantityRejected(entity.getQuantityRejected())
      .rejectionReasonId(entity.getRejectionReasonId())
      .rejectionReasonFreeText(entity.getRejectionReasonFreeText())
      .quantityShipped(entity.getQuantityShipped())
      .quantityOnDeliveryNote(entity.getQuantityOnDeliveryNote())
      .discrepancies(entity.getDraftDiscrepancies().stream()
          .map(this::draftDiscrepancyToDto)
          .collect(Collectors.toList()))
      .build();
  }

  private DraftDiscrepancyDto draftDiscrepancyToDto(DraftDiscrepancy draftDiscrepancy) {
    if (draftDiscrepancy == null) {
      return null;
    }

    return DraftDiscrepancyDto.builder()
      .quantityAffected(draftDiscrepancy.getQuantityAffected())
      .rejectionReason(rejectionReasonService.findOne(draftDiscrepancy.getRejectionReasonId()))
      .comments(draftDiscrepancy.getComments())
      .build();
  }
}
