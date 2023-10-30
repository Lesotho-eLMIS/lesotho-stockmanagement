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

package org.openlmis.stockmanagement.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.UUID;
import org.openlmis.stockmanagement.dto.PointOfDeliveryEventDto;
// import org.openlmis.stockmanagement.service.PermissionService;
import org.openlmis.stockmanagement.service.PointOfDeliveryEventProcessor;
import org.openlmis.stockmanagement.service.PointOfDeliveryService;
import org.openlmis.stockmanagement.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller used to create point of delivery event.
 */
@Controller
@RequestMapping("/api/podEvents")
public class PointOfDeliveryController extends BaseController {
  public static final String ID_PATH_VARIABLE = "/{id}";
  private static final Logger LOGGER = LoggerFactory.getLogger(PointOfDeliveryController.class);

  //   @Autowired
  //   private PermissionService permissionService;

  @Autowired
  private PointOfDeliveryEventProcessor pointOfDeliveryEventProcessor;

  @Autowired
  private PointOfDeliveryService pointOfDeliveryService;

  /**
   * Create point of delivery event.
   *
   * @param pointOfDeliveryEventDto a pod event bound to request body.
   * @return created pod event's ID.
   */
  @Transactional
  @RequestMapping(method = POST)
  public ResponseEntity<UUID> createPointOfDeliveryEvent(
        @RequestBody PointOfDeliveryEventDto pointOfDeliveryEventDto) {

    LOGGER.debug("Try to create a point of delivery event");

    Profiler profiler = getProfiler("CREATE_POD_EVENT", pointOfDeliveryEventDto);

    //checkPermission(pointOfDeliveryEventDto, profiler.startNested("CHECK_PERMISSION"));

    profiler.start("PROCESS");
    UUID createdPodId = pointOfDeliveryEventProcessor.process(pointOfDeliveryEventDto);

    profiler.start("CREATE_RESPONSE");
    ResponseEntity<UUID> response = new ResponseEntity<>(createdPodId, CREATED);

    return stopProfiler(profiler, response);
  }

  /**
   * List point of delivery event.
   *
   * @param destinationId a pod event bound to request body.
   * @return List of pod events.
   */
  @RequestMapping(method = GET)
  public ResponseEntity<List<PointOfDeliveryEventDto>> getPointOfDeliveryEvents(
      @RequestParam() UUID destinationId) {

    LOGGER.debug("Try to load point of delivery events");

    List<PointOfDeliveryEventDto> podsToReturn = 
        pointOfDeliveryService.getPointOfDeliveryEventsByDestinationId(destinationId);
    
    return new ResponseEntity<>(podsToReturn, OK);
    // Profiler profiler = getProfiler("LIST_POD_EVENTS", pointOfDeliveryEventDto);

    //checkPermission(pointOfDeliveryEventDto, profiler.startNested("CHECK_PERMISSION"));

    // profiler.start("PROCESS");
    // UUID createdPodId = pointOfDeliveryEventProcessor.process(pointOfDeliveryEventDto);

    // profiler.start("CREATE_RESPONSE");
    // ResponseEntity<UUID> response = new ResponseEntity<>(createdPodId, CREATED);

    //return stopProfiler(profiler, response);
  }

  /**
   * Update a POD event.
   *
   * @param id POD event id.
   * @param dto POD dto.
   * @return created POD dto.
   */
  @Transactional
  @PutMapping(ID_PATH_VARIABLE)
  @ResponseStatus(OK)
  @ResponseBody
  public ResponseEntity<PointOfDeliveryEventDto> updatePointOfDeliveryEvent(@PathVariable UUID id,
                                                    @RequestBody PointOfDeliveryEventDto dto) {
    PointOfDeliveryEventDto updatedPodEvent = pointOfDeliveryService
        .updatePointOfDeliveryEvent(dto, id);
    return new ResponseEntity<>(updatedPodEvent, OK);
  }

}
