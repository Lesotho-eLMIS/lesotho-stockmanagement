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


package org.openlmis.stockmanagement.dto;

import static java.time.ZonedDateTime.now;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.openlmis.stockmanagement.domain.event.PointOfDeliveryEvent;
import org.openlmis.stockmanagement.util.PointOfDeliveryEventProcessContext;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfDeliveryEventDto {

  private UUID sourceId;
  private String sourceFreeText;

  private UUID destinationId;
  private String destinationFreeText;

  private UUID receivedByUserId;

  private ZonedDateTime receivingDate;

  private String referenceNumber;

  private LocalDate packingDate;

  private String packedBy;

  private Integer numberOfCartons;

  private Integer numberOfContainers;

  private String remarks;

  private PointOfDeliveryEventProcessContext context;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */
  public PointOfDeliveryEvent toPointOfDeliveryEvent() {
    PointOfDeliveryEvent pointOfDeliveryEvent = new PointOfDeliveryEvent(
        sourceId, sourceFreeText, destinationId, destinationFreeText, 
        context.getCurrentUserId(), now(), referenceNumber, packingDate,
        packedBy, numberOfCartons, numberOfContainers, remarks);
    return pointOfDeliveryEvent;
  }

  public boolean hasSourceId() {
    return this.sourceId != null;
  }

  public boolean hasDestinationId() {
    return this.destinationId != null;
  }


}
