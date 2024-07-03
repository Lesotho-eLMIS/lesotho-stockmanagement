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

import static org.openlmis.stockmanagement.i18n.MessageKeys.NOTIFICATION_STOCK_COUNT_CONTENT;
import static org.openlmis.stockmanagement.i18n.MessageKeys.NOTIFICATION_STOCK_COUNT_SUBJECT;
import static org.openlmis.stockmanagement.service.PermissionService.LOTS_MANAGE;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.text.StrSubstitutor;
import org.openlmis.stockmanagement.dto.referencedata.RightDto;
import org.openlmis.stockmanagement.dto.referencedata.UserDto;
import org.openlmis.stockmanagement.i18n.MessageService;
import org.openlmis.stockmanagement.service.notification.NotificationService;
import org.openlmis.stockmanagement.service.referencedata.RightReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.UserReferenceDataService;
import org.openlmis.stockmanagement.util.Message;
import org.openlmis.stockmanagement.util.RequestParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PhysicalInventoryNotifier {

    @Autowired
    UserReferenceDataService userReferenceDataService;

    @Autowired
    RightReferenceDataService rightReferenceDataService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private NotificationService notificationService;

    private static final String ZONE_ID = "${time.zoneId}";

    /**
     * notify users with LOTS_MANAGE right.
     */
    @Scheduled(cron = "${stockmanagement.monthBeforeApril.cron}", zone = ZONE_ID)
    public void notifyMonthBeforeApril() {
        notifyMonthBefore();
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    @Scheduled(cron = "${stockmanagement.monthBeforeSeptember.cron}", zone = ZONE_ID)
    public void notifyMonthBeforeSeptember() {
        notifyMonthBefore();
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    public void notifyMonthBefore() {
        RightDto right = rightReferenceDataService.findRight(LOTS_MANAGE);
        UUID rightId = right.getId();
        Collection<UserDto> users = getReceipients();
        for (UserDto user : users) {
            System.out.println("----------------------------------");
            System.out.println(userReferenceDataService.hasRight(user.getId(), rightId, null, null, null).getResult());
            if (userReferenceDataService.hasRight(user.getId(), rightId, null, null, null).getResult().booleanValue()) {
                Map<String, String> valuesMap = constructSubstitutionMap(user.getUsername(), " next month");
                StrSubstitutor sub = new StrSubstitutor(valuesMap);
                System.out.println("Sending notification to: " + user.getUsername());
                notificationService.notify(user, getMessage(NOTIFICATION_STOCK_COUNT_SUBJECT),
                        sub.replace(getMessage(NOTIFICATION_STOCK_COUNT_CONTENT)));
            } else {
                System.out.println("User " + user.getUsername() + " Cannot receive notifications");
            }
        }
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    @Scheduled(cron = "${stockmanagement.twoWeeksBeforeApril.cron}", zone = ZONE_ID)
    public void notifyTwoWeeksBeforeApril() {
        notifyTwoWeeksBefore();
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    @Scheduled(cron = "${stockmanagement.twoWeeksBeforeSeptember.cron}", zone = ZONE_ID)
    public void notifyTwoWeeksBeforeSeptember() {
        notifyTwoWeeksBefore();
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    public void notifyTwoWeeksBefore() {
        RightDto right = rightReferenceDataService.findRight(LOTS_MANAGE);
        UUID rightId = right.getId();
        Collection<UserDto> users = getReceipients();
        for (UserDto user : users) {
            System.out.println("----------------------------------");
            System.out.println(userReferenceDataService.hasRight(user.getId(), rightId, null, null, null).getResult());
            if (userReferenceDataService.hasRight(user.getId(), rightId, null, null, null).getResult().booleanValue()) {
                Map<String, String> valuesMap = constructSubstitutionMap(user.getUsername(), "in two weeks time");
                StrSubstitutor sub = new StrSubstitutor(valuesMap);
                System.out.println("Sending notification to: " + user.getUsername());
                notificationService.notify(user, getMessage(NOTIFICATION_STOCK_COUNT_SUBJECT),
                        sub.replace(getMessage(NOTIFICATION_STOCK_COUNT_CONTENT)));
            } else {
                System.out.println("User " + user.getUsername() + " Cannot receive notifications");
            }
        }
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    @Scheduled(cron = "${stockmanagement.firstDayApril.cron}", zone = ZONE_ID)
    public void notifyFirstDayOfApril() {
        notifyFirstDay();
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    @Scheduled(cron = "${stockmanagement.firstDaySeptember.cron}", zone = ZONE_ID)
    public void notifyFirstDayOfSeptember() {
        notifyFirstDay();
    }

    /**
     * notify users with LOTS_MANAGE right.
     */
    public void notifyFirstDay() {
        RightDto right = rightReferenceDataService.findRight(LOTS_MANAGE);
        UUID rightId = right.getId();
        Collection<UserDto> users = getReceipients();
        for (UserDto user : users) {
            System.out.println("----------------------------------");
            System.out.println(userReferenceDataService.hasRight(user.getId(), rightId, null, null, null).getResult());
            if (userReferenceDataService.hasRight(user.getId(), rightId, null, null, null).getResult().booleanValue()) {
                Map<String, String> valuesMap = constructSubstitutionMap(user.getUsername(),
                        "starting today until month end");
                StrSubstitutor sub = new StrSubstitutor(valuesMap);
                System.out.println("Sending notification to: " + user.getUsername());
                notificationService.notify(user, getMessage(NOTIFICATION_STOCK_COUNT_SUBJECT),
                        sub.replace(getMessage(NOTIFICATION_STOCK_COUNT_CONTENT)));
            } else {
                System.out.println("User " + user.getUsername() + " Cannot receive notifications");
            }
        }
    }

    private Collection<UserDto> getReceipients() {
        RequestParameters parameters = RequestParameters.init()
                .set("active", true);
        return userReferenceDataService
                .search(parameters);
    }

    Map<String, String> constructSubstitutionMap(String username, String startTime) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("username", username);
        valuesMap.put("startTime", startTime);
        return valuesMap;
    }

    private String getMessage(String key) {
        return messageService
                .localize(new Message(key))
                .getMessage();
    }
}
