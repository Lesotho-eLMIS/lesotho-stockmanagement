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

import static org.openlmis.stockmanagement.service.PermissionService.STOCK_INVENTORIES_EDIT;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.openlmis.stockmanagement.dto.referencedata.FacilityDto;
import org.openlmis.stockmanagement.dto.referencedata.RightDto;
import org.openlmis.stockmanagement.dto.referencedata.UserDto;
import org.openlmis.stockmanagement.service.notification.NotificationService;
import org.openlmis.stockmanagement.service.referencedata.FacilityReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.RightReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.UserReferenceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PhysicalInventoryNotifier {

    private static final XLogger XLOGGER = XLoggerFactory.getXLogger(PhysicalInventoryNotifier.class);
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FacilityReferenceDataService facilityReferenceDataService;

    @Autowired
    UserReferenceDataService userReferenceDataService;

    @Autowired
    RightReferenceDataService rightReferenceDataService;

    @Autowired
    private NotificationService notificationService;

    /**
     * notify flo.
     */
    @Scheduled(cron = "${stockmanagement.monthBeforeApril.cron}", zone = "${time.zoneId}")
    public void notifyMonthBefore() {
        logger.error("Fetching right");
        RightDto right = rightReferenceDataService.findRight(STOCK_INVENTORIES_EDIT);
        UUID rightId = right.getId();
        logger.error("Right Id" + rightId.toString());
        Profiler profiler = new Profiler("NOTIFY_STOCK_EDITORS");
        profiler.setLogger(XLOGGER);

        profiler.start("GET_EDITORS");
        Collection<UserDto> recipients = getReceipients(rightId);

        profiler.start("NOTIFY_RECIPIENTS");
        for (UserDto recipient : recipients) {
            XLOGGER.error(recipient.getEmail());
            notificationService.notify(recipient, "Subject", "Message content here");
        }

        // find all facilities of type hospital or health center
        // Collection<FacilityDto> hospitals = getFacilities("hospital");
        // Collection<FacilityDto> healthCenters = getFacilities("health_center");

        // Collection<FacilityDto> allFacilities = new ArrayList<>(hospitals);
        // allFacilities.addAll(healthCenters);

        // for (FacilityDto facility : healthCenters) {
        // XLOGGER.debug(facility.toString());
        // XLOGGER.error(facility.toString());
        // XLOGGER.debug("Facility = {} ", facility.getName());
        // XLOGGER.error("Facility = {} ", facility.getName());
        // }
        // foreach facility, find users with Stock_Inventory_Edit right
        // send notification
    }

    private Collection<UserDto> getReceipients(UUID rightId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rightId", rightId.toString());
        return userReferenceDataService
                .findUsers(parameters);
    }

    private Collection<FacilityDto> getFacilities(String facilityTypeCode) {
        Map<UUID, FacilityDto> facilityMap = facilityReferenceDataService.findByType(facilityTypeCode);
        Collection<FacilityDto> facilityCollection = facilityMap.values();
        return facilityCollection;
    }

}
