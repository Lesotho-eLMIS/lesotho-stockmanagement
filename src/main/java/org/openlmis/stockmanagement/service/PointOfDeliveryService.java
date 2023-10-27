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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.openlmis.stockmanagement.domain.event.PointOfDeliveryEvent;
import org.openlmis.stockmanagement.dto.PointOfDeliveryEventDto;
import org.openlmis.stockmanagement.repository.PointOfDeliveryEventsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointOfDeliveryService {
  //private static final Logger LOGGER = LoggerFactory.getLogger(PointOfDeliveryService.class);

  @Autowired
  private PointOfDeliveryEventsRepository pointOfDeliveryEventsRepository;

  /**
   * Get a list of Point of Delivery events.
   *
   * @param destinationId destination id.
   * @return a list of pod events.
   */
  public List<PointOfDeliveryEventDto> getPointOfDeliveryEventsByDestinationId(UUID destinationId) {
    List<PointOfDeliveryEvent> pointOfDeliveryEvents =  pointOfDeliveryEventsRepository
        .findByDestinationId(destinationId);
    
    if (pointOfDeliveryEvents == null) {
      return Collections.emptyList();
    }
    return PointOfDeliveryEventDto.podToDto(pointOfDeliveryEvents);
  }
}
