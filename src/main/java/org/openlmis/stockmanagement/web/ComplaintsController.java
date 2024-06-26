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
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.openlmis.stockmanagement.dto.ComplaintDto;
import org.openlmis.stockmanagement.service.ComplaintProcessor;
import org.openlmis.stockmanagement.service.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller used to perform CRUD operations on point of delivery event.
 */
@Controller
@RequestMapping("/api/complaints")
public class ComplaintsController extends BaseController {
  public static final String ID_PATH_VARIABLE = "/{id}";
  private static final Logger LOGGER = LoggerFactory.getLogger(ComplaintsController.class);

  //   @Autowired
  //   private PermissionService permissionService;

  @Autowired
  private ComplaintProcessor complaintProcessor;

  @Autowired
  private ComplaintService complaintService;

  /**
   * Create complaint.
   *
   * @param complaintDto a complaint bound to request body.
   * @return created complaint's ID.
   */
  @Transactional
  @RequestMapping(method = POST)
  public ResponseEntity<UUID> createComplaint(
        @RequestBody ComplaintDto complaintDto) {

    LOGGER.debug("Try to create a complaint");

    Profiler profiler = getProfiler("CREATE_COMPLAINT", complaintDto);

    //checkPermission(pointOfDeliveryEventDto, profiler.startNested("CHECK_PERMISSION"));

    profiler.start("PROCESS");
    UUID createdComplaintId = complaintProcessor.process(complaintDto);

    profiler.start("CREATE_RESPONSE");
    ResponseEntity<UUID> response = new ResponseEntity<>(createdComplaintId, CREATED);

    return stopProfiler(profiler, response);
  }

  /**
   * List Complaints.
   *
   * @param facilityId a facility id.
   * @param startDate for filtering records.
   * @param endDate for filtering records.
   * @return List of pod events.
   */
  @RequestMapping(method = GET)
  public ResponseEntity<List<ComplaintDto>> getComplaints(
      @RequestParam() UUID facilityId, @RequestParam(required = false) ZonedDateTime startDate, 
      @RequestParam(required = false) ZonedDateTime endDate) {

    LOGGER.debug("Try to load point of delivery events");

    List<ComplaintDto> complaintsToReturn = 
      complaintService.getComplaintsByFacilityId(facilityId,startDate,endDate);
    
    return new ResponseEntity<>(complaintsToReturn, OK);
    // Profiler profiler = getProfiler("LIST_POD_EVENTS", pointOfDeliveryEventDto);

    //checkPermission(pointOfDeliveryEventDto, profiler.startNested("CHECK_PERMISSION"));

    // profiler.start("PROCESS");
    // UUID createdPodId = pointOfDeliveryEventProcessor.process(pointOfDeliveryEventDto);

    // profiler.start("CREATE_RESPONSE");
    // ResponseEntity<UUID> response = new ResponseEntity<>(createdPodId, CREATED);

    //return stopProfiler(profiler, response);
  }

  /**
   * Update a Complaint.
   *
   * @param id POD event id.
   * @param dto POD dto.
   * @return created POD dto.
   */
  @Transactional
  @PutMapping(ID_PATH_VARIABLE)
  @ResponseStatus(OK)
  @ResponseBody
  public ResponseEntity<ComplaintDto> updateComplaint(@PathVariable UUID id,
                                                    @RequestBody ComplaintDto dto) {
    ComplaintDto updatedPodEvent = complaintService
        .updateComplaint(dto, id);
    return new ResponseEntity<>(updatedPodEvent, OK);
  }

  /**
   * Delete a Complaint.
   *
   * @param id Complaint id.
   */
  @DeleteMapping(ID_PATH_VARIABLE)
  @ResponseStatus(NO_CONTENT)
  public void deleteComplaint(@PathVariable UUID id) {
    complaintService.deleteComplaint(id);
  }

  /**
   * Get a Complaint.
   *
   * @param id Complaint id.
   */
  @GetMapping(ID_PATH_VARIABLE)
  @ResponseStatus(NO_CONTENT)
  public ComplaintDto getComplaint(@PathVariable UUID id) {
    return complaintService.getComplaintById(id);
  }
}
