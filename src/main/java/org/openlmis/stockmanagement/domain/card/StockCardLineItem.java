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

package org.openlmis.stockmanagement.domain.card;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static org.openlmis.stockmanagement.domain.adjustment.StockCardLineItemReason.physicalBalance;
import static org.openlmis.stockmanagement.domain.adjustment.StockCardLineItemReason.physicalCredit;
import static org.openlmis.stockmanagement.domain.adjustment.StockCardLineItemReason.physicalDebit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.openlmis.stockmanagement.domain.BaseEntity;
import org.openlmis.stockmanagement.domain.adjustment.ReasonType;
import org.openlmis.stockmanagement.domain.adjustment.StockCardLineItemReason;
import org.openlmis.stockmanagement.domain.event.StockEvent;
import org.openlmis.stockmanagement.domain.event.StockEvent2;
import org.openlmis.stockmanagement.domain.event.StockEventLineItem;
import org.openlmis.stockmanagement.domain.movement.Node;
import org.openlmis.stockmanagement.dto.StockEventDto;
import org.openlmis.stockmanagement.dto.StockEventDto2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({
    "stockCard", "originEvent",
    "source", "destination",
    "processedDate",
    "userId"})
@Table(name = "stock_card_line_items", schema = "stockmanagement")
public class StockCardLineItem extends BaseEntity {

  private static final Logger LOGGER = LoggerFactory.getLogger(StockCardLineItem.class);

  @ManyToOne()
  @JoinColumn(nullable = false)
  private StockCard stockCard;

  @ManyToOne()
  @JoinColumn(nullable = true)
  private StockEvent originEvent;

  @ManyToOne()
  @JoinColumn(nullable = true)
  private StockEvent2 originEvent2;

  @Column(nullable = false)
  private Integer quantity;

  @ManyToOne()
  @JoinColumn()
  private StockCardLineItemReason reason;

  private String sourceFreeText;
  private String destinationFreeText;
  private String documentNumber;
  private String reasonFreeText;
  private String signature;

  @ManyToOne()
  @JoinColumn()
  private Node source;

  @ManyToOne()
  @JoinColumn()
  private Node destination;

  @Column(nullable = false, columnDefinition = "timestamp")
  @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  private ZonedDateTime occurredDate;

  @Column(nullable = false, columnDefinition = "timestamp")
  private ZonedDateTime processedDate;

  @Column(nullable = false)
  private UUID userId;

  @Transient
  private Integer stockOnHand;

  /**
   * Create line item from eventDto.
   *
   * @param eventDto     stock eventDto.
   * @param stockCard    the card that this line item belongs to.
   * @param savedEventId saved event id.
   * @param userId       user who performed the operation.  @return created line item.
   * @throws InstantiationException InstantiationException.
   * @throws IllegalAccessException IllegalAccessException.
   */
  public static List<StockCardLineItem> createLineItemsFrom(
      StockEventDto eventDto, StockCard stockCard, UUID savedEventId, UUID userId)
      throws InstantiationException, IllegalAccessException {
    StockCardLineItem lineItem = new StockCardLineItem(
        stockCard,
        fromId(savedEventId, StockEvent.class),
        null,
        eventDto.getQuantity(),
        fromId(eventDto.getReasonId(), StockCardLineItemReason.class),
        eventDto.getSourceFreeText(), eventDto.getDestinationFreeText(),
        eventDto.getDocumentNumber(), eventDto.getReasonFreeText(), eventDto.getSignature(),
        fromId(eventDto.getSourceId(), Node.class),
        fromId(eventDto.getDestinationId(), Node.class),
        eventDto.getOccurredDate(), now(),//processed date generated by server
        userId, 0);
    stockCard.getLineItems().add(lineItem);
    return asList(lineItem);
  }

  /**
   * Create line item from eventDto.
   *
   * @param eventDto     stock eventDto.
   * @param stockCard    the card that this line item belongs to.
   * @param savedEventId saved event id.
   * @param userId       user who performed the operation.  @return created line item.
   * @throws InstantiationException InstantiationException.
   * @throws IllegalAccessException IllegalAccessException.
   */
  public static StockCardLineItem createLineItemFrom2(
      StockEventDto2 eventDto, StockEventLineItem eventLineItem,
      StockCard stockCard, UUID savedEventId, UUID userId)
      throws InstantiationException, IllegalAccessException {
    StockCardLineItem cardLineItem = new StockCardLineItem(
        stockCard,
        null,
        fromId(savedEventId, StockEvent2.class),
        eventLineItem.getQuantity(),
        fromId(eventDto.getReasonId(), StockCardLineItemReason.class),
        eventDto.getSourceFreeText(), eventDto.getDestinationFreeText(),
        eventDto.getDocumentNumber(), eventDto.getReasonFreeText(), eventDto.getSignature(),
        fromId(eventDto.getSourceId(), Node.class),
        fromId(eventDto.getDestinationId(), Node.class),
        eventDto.getOccurredDate(), now(),//processed date generated by server
        userId, 0);
    stockCard.getLineItems().add(cardLineItem);
    return cardLineItem;
  }

  /**
   * Calculate stock on hand with previous stock on hand.
   *
   * @param previousStockOnHand previous stock on hand.
   */
  public void calculateStockOnHand(int previousStockOnHand) {
    if (isPhysicalInventory()) {
      setReason(determineReasonByQuantity(previousStockOnHand));
      setStockOnHand(quantity);
      setQuantity(Math.abs(getStockOnHand() - previousStockOnHand));
      LOGGER.debug("Physical inventory: " + getStockOnHand());
    } else if (shouldIncrease()) {
      setStockOnHand(previousStockOnHand + quantity);
      LOGGER.debug(previousStockOnHand + " + " + quantity + " = " + getStockOnHand());
    } else {
      setStockOnHand(previousStockOnHand - quantity);
      LOGGER.debug(previousStockOnHand + " - " + quantity + " = " + getStockOnHand());
    }
  }

  private StockCardLineItemReason determineReasonByQuantity(int previousStockOnHand) {
    if (quantity > previousStockOnHand) {
      return physicalCredit();
    } else if (quantity < previousStockOnHand) {
      return physicalDebit();
    } else {
      return physicalBalance();
    }
  }

  private boolean isPhysicalInventory() {
    return source == null && destination == null && reason == null;
  }

  private boolean shouldIncrease() {
    boolean hasSource = source != null;
    boolean isCredit = reason != null && reason.getReasonType() == ReasonType.CREDIT;
    return hasSource || isCredit;
  }
}
