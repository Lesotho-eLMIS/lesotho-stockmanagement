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

package org.openlmis.stockmanagement.domain.event;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "point_of_delivery_event", schema = "stockmanagement")
public class PointOfDeliveryEvent extends BaseEntity {
    
  private UUID sourceId;
  private String sourceFreeText;

  private UUID destinationId;
  private String destinationFreeText;

  private UUID receivedByUserId;

  @Column(nullable = false, columnDefinition = "timestamp")
  private ZonedDateTime receivingDate;

  private String referenceNumber;

  @Column(nullable = false, columnDefinition = "timestamp")
  private LocalDate packingDate;

  private String packedBy;

  private Integer numberOfCartons;

  private Integer numberOfContainers;

  private String remarks;

}
