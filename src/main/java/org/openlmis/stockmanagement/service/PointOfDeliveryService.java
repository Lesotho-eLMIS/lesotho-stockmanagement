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
import java.util.Optional;
import java.util.UUID;

import org.openlmis.stockmanagement.domain.event.PointOfDeliveryEvent;
import org.openlmis.stockmanagement.dto.PointOfDeliveryEventDto;
import org.openlmis.stockmanagement.repository.PointOfDeliveryEventsRepository;
import org.openlmis.stockmanagement.util.PointOfDeliveryEventProcessContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointOfDeliveryService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PointOfDeliveryService.class);

  @Autowired
  private PointOfDeliveryEventsRepository pointOfDeliveryEventsRepository;

  @Autowired
  private PointOfDeliveryEventProcessContextBuilder contextBuilder;

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

  /**
   * Save or update POD.
   *
   * @param dto POD event dto.
   * @return the saved POD event.
   */
  public PointOfDeliveryEventDto updatePointOfDeliveryEvent(PointOfDeliveryEventDto dto, UUID id) {
    //LOGGER.info("update POS event");
    //physicalInventoryValidator.validateDraft(dto, id);
    //checkPermission(dto.getProgramId(), dto.getFacilityId());

    //checkIfDraftExists(dto, id);
    
    LOGGER.info("Attempting to fetch pod event with id = " + id);
    Optional<PointOfDeliveryEvent> existingPodEventOpt = 
        pointOfDeliveryEventsRepository.findById(id);


    if (existingPodEventOpt.isPresent()) {
      PointOfDeliveryEvent existingPodEvent = existingPodEventOpt.get();
      PointOfDeliveryEventProcessContext context = contextBuilder.buildContext(dto);
      dto.setContext(context);
      PointOfDeliveryEvent incomingPodEvent = dto.toPointOfDeliveryEvent();

      // Update the Existing PodEvent object with values from the DTO
      //existingPodEvent = copyAttributes(existingPodEvent, dto);
      existingPodEvent = copyAttributes(existingPodEvent, incomingPodEvent);
    
      //save updated pod event
      pointOfDeliveryEventsRepository.save(existingPodEvent);
      return PointOfDeliveryEventDto.podToDto(existingPodEvent);
    } else {
      return null;
    }
  }

  private PointOfDeliveryEvent copyAttributes(
      PointOfDeliveryEvent existingPodEvent, PointOfDeliveryEvent incomingPodEvent) {
    if (incomingPodEvent.getSourceId() != null) {
      existingPodEvent.setSourceId(incomingPodEvent.getSourceId());
    }
    if (incomingPodEvent.getSourceFreeText() != null) {
      existingPodEvent.setSourceFreeText(incomingPodEvent.getSourceFreeText());
    }
    if (incomingPodEvent.getDestinationId() != null) {
      existingPodEvent.setDestinationId(incomingPodEvent.getDestinationId());
    }
    if (incomingPodEvent.getDestinationFreeText() != null) {
      existingPodEvent.setDestinationFreeText(incomingPodEvent.getDestinationFreeText());
    }
    if (incomingPodEvent.getReferenceNumber() != null) {
      existingPodEvent.setReferenceNumber(incomingPodEvent.getReferenceNumber());
    }
    if (incomingPodEvent.getPackingDate() != null) {
      existingPodEvent.setPackingDate(incomingPodEvent.getPackingDate());
    }
    if (incomingPodEvent.getPackedBy() != null) {
      existingPodEvent.setPackedBy(incomingPodEvent.getPackedBy());
    }
    if (incomingPodEvent.getNumberOfCartons() != null) {
      existingPodEvent.setNumberOfCartons(incomingPodEvent.getNumberOfCartons());
    }
    if (incomingPodEvent.getNumberOfContainers() != null) {
      existingPodEvent.setNumberOfContainers(incomingPodEvent.getNumberOfContainers());
    }
    if (incomingPodEvent.getRemarks() != null) {
      existingPodEvent.setRemarks(incomingPodEvent.getRemarks());
    }
    return existingPodEvent;
  }

  // private PointOfDeliveryEvent copyAttributes(
  //     PointOfDeliveryEvent existingPodEvent, PointOfDeliveryEventDto dto) {
  //   try {
  //     BeanUtils.copyProperties(existingPodEvent, dto);
  //   } catch (IllegalAccessException | InvocationTargetException e) {
  //     // Handle exceptions as needed -- to finetune
  //     LOGGER.debug("There was an error in mapping dto to jpa attributes" + e);
  //   }
  //   return existingPodEvent;
  // }

  // private PointOfDeliveryEvent copyAttributes(
  //     PointOfDeliveryEvent existingPodEvent, PointOfDeliveryEvent incomingPodEvent) {
  //   try {
  //     BeanUtils.copyProperties(existingPodEvent, incomingPodEvent);
  //   } catch (IllegalAccessException | InvocationTargetException e) {
  //     // Handle exceptions as needed -- to finetune
  //     LOGGER.debug("There was an error in mapping incoming to existing pod event" + e);
  //   }
  //   return existingPodEvent;
  // }
}
