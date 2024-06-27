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

import static org.openlmis.stockmanagement.i18n.MessageKeys.NOTIFICATION_COMPLAINT_CONTENT;
import static org.openlmis.stockmanagement.i18n.MessageKeys.NOTIFICATION_COMPLAINT_SUBJECT;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.text.StrSubstitutor;
import org.openlmis.stockmanagement.domain.complaint.Complaint;
import org.openlmis.stockmanagement.dto.referencedata.FacilityDto;
import org.openlmis.stockmanagement.dto.referencedata.SupervisoryNodeDto;
import org.openlmis.stockmanagement.dto.referencedata.UserDto;
import org.openlmis.stockmanagement.i18n.MessageService;
import org.openlmis.stockmanagement.service.notification.NotificationService;
import org.openlmis.stockmanagement.service.referencedata.FacilityReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.ProgramReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.SupervisingUsersReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.SupervisoryNodeReferenceDataService;
import org.openlmis.stockmanagement.util.Message;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ComplaintNotifier extends BaseNotifier {

  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(StockCardNotifier.class);

  @Autowired
  private SupervisingUsersReferenceDataService supervisingUsersReferenceDataService;

  @Autowired
  private SupervisoryNodeReferenceDataService supervisoryNodeReferenceDataService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private ProgramReferenceDataService programReferenceDataService;

  @Autowired
  private MessageService messageService;

  @Value("${email.urlToViewComplaint}")
  private String urlToViewCompaint;

  /**
   * Notify users with a certain right for the facility/program that facility has stocked out of a
   * product.
   *
   * @param complaint Complaint
   * @param rightId right UUID
   */
  @Async
  public void notifyRecipients(Complaint complaint, UUID rightId) {
    Profiler profiler = new Profiler("NOTIFY_STOCK_RECIPEINTS");
    profiler.setLogger(XLOGGER);

    profiler.start("GET_RECIPEINTS");
    Collection<UserDto> recipients = getRecipients(complaint, rightId);

    NotificationMessageParams params = new NotificationMessageParams(
        getMessage(NOTIFICATION_COMPLAINT_SUBJECT),
        getMessage(NOTIFICATION_COMPLAINT_CONTENT),
        constructSubstitutionMap(complaint));

    Map<String, String> valuesMap = params.getSubstitutionMap();
    StrSubstitutor sub = new StrSubstitutor(valuesMap);

    profiler.start("NOTIFY_RECIPIENTS");
    for (UserDto recipient : recipients) {
      // if (complaint.getFacilityId().equals(recipient.getHomeFacilityId()) || isSupervisingUser(recipient)) {
      if (isSupervisingUser(recipient)) {
        valuesMap.put("username", recipient.getUsername());
        XLOGGER.debug("Recipient username = {}", recipient.getUsername());
        notificationService.notify(recipient,
            sub.replace(params.getMessageSubject()), sub.replace(params.getMessageContent()));
      }
    }

    profiler.stop().log();
  }

  /**
   * Notify users with a certain right for the facility/program that facility has stocked out of a
   * product.
   *
   * @param complaint Complaint
   * @param recipients Recipient users
   */
  @Async
  public void notifyRecipients(Complaint complaint, Collection<UserDto> recipients) {
    Profiler profiler = new Profiler("NOTIFY_STOCK_RECIPEINTS");
    profiler.setLogger(XLOGGER);

    NotificationMessageParams params = new NotificationMessageParams(
        getMessage(NOTIFICATION_COMPLAINT_SUBJECT),
        getMessage(NOTIFICATION_COMPLAINT_CONTENT),
        constructSubstitutionMap(complaint));

    Map<String, String> valuesMap = params.getSubstitutionMap();
    StrSubstitutor sub = new StrSubstitutor(valuesMap);

    profiler.start("NOTIFY_RECIPIENTS");
    for (UserDto recipient : recipients) {
      // if (complaint.getFacilityId().equals(recipient.getHomeFacilityId()) || isSupervisingUser(recipient)) {
      if (isSupervisingUser(recipient)) {
        valuesMap.put("username", recipient.getUsername());
        XLOGGER.debug("Recipient username = {}", recipient.getUsername());
        notificationService.notify(recipient,
            sub.replace(params.getMessageSubject()), sub.replace(params.getMessageContent()));
      }
    }

    profiler.stop().log();
  }

  private Collection<UserDto> getRecipients(Complaint complaint, UUID rightId) {
    SupervisoryNodeDto supervisoryNode = supervisoryNodeReferenceDataService
        .findSupervisoryNode(complaint.getProgramId(), complaint.getFacilityId());

    if (supervisoryNode == null) {
      throw new IllegalArgumentException(
          String.format("There is no supervisory node for program %s and facility %s",
              complaint.getProgramId(), complaint.getFacilityId()));
    }
    
    XLOGGER.debug("Supervisory node ID = {}", supervisoryNode.getId());

    return supervisingUsersReferenceDataService
        .findAll(supervisoryNode.getId(), rightId, complaint.getProgramId());
  }

  private Boolean isSupervisingUser(UserDto user) {
    if (user.getHomeFacilityId() == null) {
        return true;
    } else {
        FacilityDto homeFacility = facilityReferenceDataService
            .findByIds(Collections.singleton(user.getHomeFacilityId()))
            .get(user.getHomeFacilityId());
        
        if (homeFacility != null && homeFacility.getName() != null) {
            return homeFacility.getName().toLowerCase().contains("dhmt");
        }
    }
    return false;
  }

   private String getMessage(String key) {
    return messageService
        .localize(new Message(key))
        .getMessage();
  }

  Map<String, String> constructSubstitutionMap(Complaint complaint) {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("facilityName", getFacilityName(complaint.getFacilityId()));
    valuesMap.put("programName", getProgramName(complaint.getProgramId()));
    valuesMap.put("urlToViewComplaint", getUrlToViewComplaint(complaint.getId()));
    return valuesMap;
  }

  String getFacilityName(UUID facilityId) {
    return facilityReferenceDataService.findOne(facilityId).getName();
  }

  String getProgramName(UUID programId) {
    return programReferenceDataService.findOne(programId).getName();
  }

  String getUrlToViewComplaint(UUID complaintId) {
    return MessageFormat.format(urlToViewCompaint, complaintId);
  }

}
