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

import java.util.UUID;

import org.openlmis.stockmanagement.domain.complaint.Complaint;
import org.openlmis.stockmanagement.dto.ComplaintDto;
import org.openlmis.stockmanagement.repository.ComplaintsRepository;
import org.openlmis.stockmanagement.util.ComplaintProcessContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service that is in charge of saving point of delivery events
 * pod events.
 */
@Service
public class ComplaintProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(
          ComplaintProcessor.class);
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(
          ComplaintProcessor.class);

  @Autowired
  private ComplaintProcessContextBuilder contextBuilder;

  @Autowired
  private ComplaintsRepository complaintsRepository;

  /**
   * Validate and persist Complaint.
   *
   * @param complaintDto Complaint dto.
   * @return the persisted complaint ids.
   */
  public UUID process(ComplaintDto complaintDto) {
    XLOGGER.entry(complaintDto);
    Profiler profiler = new Profiler("PROCESS");
    profiler.setLogger(XLOGGER);

    profiler.start("BUILD_CONTEXT");
    ComplaintProcessContext context = contextBuilder.buildContext(
            complaintDto);
    complaintDto.setContext(context);

    //to do validations

    UUID eventId = saveEventAndGenerateLineItems(
        complaintDto, profiler.startNested("SAVE_AND_GENERATE_LINE_ITEMS")
    );

    return eventId;
  }

  private UUID saveEventAndGenerateLineItems(ComplaintDto complaintDto,
                                             Profiler profiler) {
    profiler.start("CONVERT_TO_COMPLAINT");
    Complaint complaint = complaintDto
            .toComplaint();

    profiler.start("DB_SAVE");
    UUID complaintId = complaintsRepository.save(
            complaint).getId();
    LOGGER.debug("Saved complaint with id " + complaintId);

    return complaintId;
  }

}
