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

import java.time.LocalDate;
import java.util.UUID;

import org.openlmis.stockmanagement.dto.StockEventDraftDto;
import org.openlmis.stockmanagement.service.HomeFacilityPermissionService;
import org.openlmis.stockmanagement.service.PermissionService;
import org.openlmis.stockmanagement.service.StockEventDraftProcessor;
import org.openlmis.stockmanagement.service.StockEventDraftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller used to create stock event.
 */
@Controller
@Transactional
@RequestMapping("/api/stockEventsDraft")
public class StockEventsDraftController extends BaseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(StockEventsDraftController.class);

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private HomeFacilityPermissionService homeFacilityPermissionService;

  @Autowired
  private StockEventDraftProcessor stockEventDraftProcessor;

  @Autowired
  private StockEventDraftService stockEventDraftService;

  /**
   * Create draft stock event.
   *
   * @param eventDto a stock event bound to request body.
   * @return created stock event's ID.
   */
  @Transactional
  @RequestMapping(method = POST)
  public ResponseEntity<UUID> createStockEventDraft(@RequestBody StockEventDraftDto eventDto) {
    LOGGER.debug("Try to create a draft stock event");

    Profiler profiler = getProfiler("CREATE_STOCK_EVENT", eventDto);

    checkPermission(eventDto, profiler.startNested("CHECK_PERMISSION"));

    profiler.start("PROCESS");
    UUID createdEventId = stockEventDraftProcessor.process(eventDto);

    profiler.start("CREATE_RESPONSE");
    ResponseEntity<UUID> response = new ResponseEntity<>(createdEventId, CREATED);

    return stopProfiler(profiler, response);
  }

  /**
   * Get a draft stock events by program and facility.
   *
   * @return Stock event drafts.
   */
  @RequestMapping(method = GET)
  public ResponseEntity<Page<StockEventDraftDto>> getStockEventDrafts(
      @RequestParam() UUID programId,
      @RequestParam() UUID facilityId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    // LOGGER.debug("Try to find draft stock events");
    // permissionService.canViewStockCard(program, facility);
    Pageable pageable = PageRequest.of(page, size, Sort.by("processedDate").descending());
    Page<StockEventDraftDto> draftsPage = 
        stockEventDraftService.findDraftStockEvents(programId, facilityId, pageable);

    return new ResponseEntity<>(draftsPage, OK);
  }

  private void checkPermission(StockEventDraftDto eventDto, Profiler profiler) {
    OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder
        .getContext().getAuthentication();

    if (!authentication.isClientOnly()) {
      UUID programId = eventDto.getProgramId();
      UUID facilityId = eventDto.getFacilityId();

      profiler.start("CHECK_PROGRAM_SUPPORTED_BY_HOME_FACILITY");
      homeFacilityPermissionService.checkProgramSupported(programId);

      if (eventDto.isPhysicalInventory()) {
        profiler.start("CAN_EDIT_PHYSICAL_INVENTORY");
        permissionService.canEditPhysicalInventory(programId, facilityId);
      } else {
        //we check STOCK_ADJUST permission for both adjustment and issue/receive
        //this may change in the future
        profiler.start("CAN_ADJUST_STOCK");
        permissionService.canAdjustStock(programId, facilityId);
      }
    }
  }
}
