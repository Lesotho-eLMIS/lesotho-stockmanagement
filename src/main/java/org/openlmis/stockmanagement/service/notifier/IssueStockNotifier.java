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

package org.openlmis.stockmanagement.service.notifier;

import static org.openlmis.stockmanagement.i18n.MessageKeys.NOTIFICATION_ISSUE_CONTENT;
import static org.openlmis.stockmanagement.i18n.MessageKeys.NOTIFICATION_ISSUE_SUBJECT;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.openlmis.stockmanagement.domain.card.StockCard;
import org.openlmis.stockmanagement.domain.sourcedestination.Node;
import org.openlmis.stockmanagement.dto.StockEventLineItemDto;
import org.openlmis.stockmanagement.dto.referencedata.LotDto;
import org.openlmis.stockmanagement.i18n.MessageService;
import org.openlmis.stockmanagement.repository.NodeRepository;
import org.openlmis.stockmanagement.service.referencedata.LotReferenceDataService;
import org.openlmis.stockmanagement.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IssueStockNotifier {

  @Autowired
  private LotReferenceDataService lotReferenceDataService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private StockCardNotifier stockCardNotifier;

  @Autowired
  private NodeRepository nodeRepository;

  // @Value("${email.urlToInitiateRequisition}")
  // private String urlToInitiateRequisition;

  /**
   * Notify users with a certain right for the facility/program that facility has
   * low stock of a
   * product.
   *
   * @param stockCard StockCard for a product
   * @param rightId   right UUID
   */
  public void notifyStockEditors(StockCard stockCard, UUID rightId, StockEventLineItemDto eventLine) {
    NotificationMessageParams params = new NotificationMessageParams(
        getMessage(NOTIFICATION_ISSUE_SUBJECT),
        getMessage(NOTIFICATION_ISSUE_CONTENT),
        constructSubstitutionMap(stockCard, eventLine));
    stockCardNotifier.notifyStockEditors(stockCard, rightId, params);
  }

  Map<String, String> constructSubstitutionMap(StockCard stockCard, StockEventLineItemDto eventLine) {
    UUID facilityId = null;
    Optional<Node> nodeOptional = nodeRepository.findById(eventLine.getDestinationId());
    if (nodeOptional.isPresent()) {
      Node node = nodeOptional.get();
      if (node.isRefDataFacility()) {
        facilityId = node.getReferenceId();
      }
    }

    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("facilityName", stockCardNotifier.getFacilityName(stockCard.getFacilityId()));
    if (facilityId != null) {
      valuesMap.put("destinationName", stockCardNotifier.getFacilityName(facilityId));
    } else {
      valuesMap.put("destinationName", " some facility");
    }
    valuesMap.put("orderableName", stockCardNotifier.getOrderableName(stockCard.getOrderableId()));
    valuesMap.put("orderableNameLotInformation",
        getOrderableNameLotInformation(valuesMap.get("orderableName"), stockCard.getLotId()));
    valuesMap.put("programName",
        stockCardNotifier.getProgramName(stockCard.getProgramId()));
    valuesMap.put("quantity", String.valueOf(eventLine.getQuantity()));

    return valuesMap;
  }

  private String getOrderableNameLotInformation(String orderableName, UUID lotId) {
    if (lotId != null) {
      LotDto lot = lotReferenceDataService.findOne(lotId);
      return orderableName + " " + lot.getLotCode();
    }
    return orderableName;
  }

  private long getNumberOfDaysOfStockout(LocalDate stockoutDate) {
    return ChronoUnit.DAYS.between(stockoutDate, LocalDate.now());
  }

  // private String getUrlToInitiateRequisition(StockCard stockCard) {
  // return MessageFormat.format(urlToInitiateRequisition,
  // stockCard.getFacilityId(), stockCard.getProgramId(), "true", "false");
  // }

  private String getMessage(String key) {
    return messageService
        .localize(new Message(key))
        .getMessage();
  }
}
