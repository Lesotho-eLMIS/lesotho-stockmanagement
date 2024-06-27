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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openlmis.stockmanagement.domain.complaint.Complaint;
import org.openlmis.stockmanagement.domain.complaint.ComplaintLineItem;
import org.openlmis.stockmanagement.dto.ComplaintDto;
import org.openlmis.stockmanagement.dto.ComplaintLineItemDto;
import org.openlmis.stockmanagement.dto.referencedata.RightDto;
import org.openlmis.stockmanagement.dto.referencedata.UserDto;
import org.openlmis.stockmanagement.exception.ResourceNotFoundException;
import org.openlmis.stockmanagement.repository.ComplaintsRepository;
import org.openlmis.stockmanagement.service.notifier.ComplaintNotifier;
import org.openlmis.stockmanagement.service.referencedata.RightReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.UserReferenceDataService;
import org.openlmis.stockmanagement.util.ComplaintProcessContext;
import org.openlmis.stockmanagement.util.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplaintService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ComplaintService.class);

  @Autowired
  private ComplaintsRepository complaintsRepository;

  @Autowired
  private ComplaintProcessContextBuilder contextBuilder;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  @Autowired
  private ComplaintNotifier complaintNotifier;

  @Autowired
  private RightReferenceDataService rightReferenceDataService;


  /**
   * Get a list of Complaints.
   *
   * @param destinationId destination id.
   * @return a list of complaints.
   */
  public List<ComplaintDto> getComplaintsByFacilityId(UUID destinationId, 
              ZonedDateTime startDate, ZonedDateTime endDate) {

    List<Complaint> complaints = new ArrayList<Complaint>();
    
    if (startDate == null && endDate == null) {
      complaints = complaintsRepository
        .findByFacilityId(destinationId);
    } else if (startDate == null && endDate != null) {
      complaints = complaintsRepository
        .findByFacilityIdBeforeEndDate(destinationId,endDate);
    } else if (startDate != null && endDate == null) {
      endDate = ZonedDateTime.now();
      complaints = complaintsRepository
        .findByFacilityIdAndOccuredDateBetween(destinationId, startDate, endDate);
    } else {
      complaints = complaintsRepository
        .findByFacilityIdAndOccuredDateBetween(destinationId, startDate, endDate);
    }
    
    if (complaints == null) {
      return Collections.emptyList();
    }
    return complaintToDto(complaints);
  }

  /**
   * Get a Complaint by id.
   *
   * @param id Complaint id.
   * @return a Complaint.
   */
  public ComplaintDto getComplaintById(UUID id) {
    Optional<Complaint> optionalComplaint =  complaintsRepository.findById(id);
    if (optionalComplaint.isPresent()) {
      return complaintToDto(optionalComplaint.get());
    }
    return null;
  }

  /**
   * Save or update Complaint.
   *
   * @param dto Complaint dto.
   * @return the saved Complaint.
   */
  public ComplaintDto updateComplaint(ComplaintDto dto, UUID id) {
    //LOGGER.info("update POS event");
    //physicalInventoryValidator.validateDraft(dto, id);
    //checkPermission(dto.getProgramId(), dto.getFacilityId());

    //checkIfDraftExists(dto, id);
    
    LOGGER.info("Attempting to fetch Complaint with id = " + id);
    Optional<Complaint> existingComplaintOpt = 
        complaintsRepository.findById(id);

    if (existingComplaintOpt.isPresent()) {
      Complaint existingComplaint = existingComplaintOpt.get();
      ComplaintProcessContext context = contextBuilder.buildContext(dto);
      dto.setContext(context);
      Complaint incomingComplaint = dto.toComplaint();

      // Update the Existing Complaint object with values incoming DTO data
      existingComplaint = copyAttributes(existingComplaint, incomingComplaint);
    
      //save updated complaint
      complaintsRepository.save(existingComplaint);
      return complaintToDto(existingComplaint);
    } else {
      return null;
    }
  }

  private Complaint copyAttributes(
      Complaint existingComplaint, Complaint incomingComplaint) {
  
    if (incomingComplaint.getFacilityId() != null) {
      existingComplaint.setFacilityId(incomingComplaint.getFacilityId());
    }
    if (incomingComplaint.getProgramId() != null) {
      existingComplaint.setProgramId(incomingComplaint.getProgramId());
    }
    if (incomingComplaint.getInvoiceNumber() != null) {
      existingComplaint.setInvoiceNumber(incomingComplaint.getInvoiceNumber());
    }
    if (incomingComplaint.getPurchaseOrderNumber() != null) {
      existingComplaint.setPurchaseOrderNumber(incomingComplaint.getPurchaseOrderNumber());
    }
    if (incomingComplaint.getOccurredDate() != null) {
      existingComplaint.setOccurredDate(incomingComplaint.getOccurredDate());
    }
    if (incomingComplaint.getLineItems() != null) {
      existingComplaint.setLineItems(incomingComplaint.getLineItems());
    }
    return existingComplaint;
  }

  /**
   * Delete Complaint.
   *
   * @param id Complaint id.
   */
  public void deleteComplaint(UUID id) {
    //LOGGER.info("update POS event");
    //physicalInventoryValidator.validateDraft(dto, id);
    //checkPermission(dto.getProgramId(), dto.getFacilityId());

    //checkIfDraftExists(dto, id);
    
    LOGGER.info("Attempting to fetch complaint with id = " + id);
    Optional<Complaint> existingComplaintOpt = 
        complaintsRepository.findById(id);

    if (existingComplaintOpt.isPresent()) {
      //delete complaint
      complaintsRepository.delete(existingComplaintOpt.get());
    } else {
      throw new ResourceNotFoundException(new Message("Complaint not found ", id));
    }
  }

  /**
   * Create from jpa model.
   *
   * @param complaints inventory jpa model.
   * @return created dto.
   */
  private List<ComplaintDto> complaintToDto(
        Collection<Complaint> complaints) {

    List<ComplaintDto> complaintDtos = new ArrayList<>(complaints.size());
    complaints.forEach(i -> complaintDtos.add(complaintToDto(i)));
    return complaintDtos;
  }

  /**
   * Create dto from jpa model.
   *
   * @param complaint inventory jpa model.
   * @return created dto.
   */
  private ComplaintDto complaintToDto(Complaint complaint) {
    
    return ComplaintDto.builder()
      .id(complaint.getId())
      .facilityId(complaint.getFacilityId())
      .programId(complaint.getProgramId())
      .purchaseOrderNumber(complaint.getPurchaseOrderNumber())
      .invoiceNumber(complaint.getInvoiceNumber())
      .userId(complaint.getUserId())
      .userNames(complaint.getUserNames())
      .occurredDate(complaint.getOccurredDate())
      .lineItems(complaint.getLineItems()
          .stream()
          .map(this::complaintLineItemToDto)
          .collect(Collectors.toList()))
      .build();
  }


  /**
   * Create dto from jpa model.
   *
   * @param complaintLineItem inventory jpa model.
   * @return created dto.
   */
  private ComplaintLineItemDto complaintLineItemToDto(ComplaintLineItem complaintLineItem) {

    return ComplaintLineItemDto.builder()
      .id(complaintLineItem.getId())
      .orderableId(complaintLineItem.getOrderableId())
      .lotId(complaintLineItem.getLotId())
      .quantityAffected(complaintLineItem.getQuantityAffected())
      .quantityReturned(complaintLineItem.getQuantityReturned())
      .natureOfComplaint(complaintLineItem.getNatureOfComplaint())
      .complaintReason(complaintLineItem.getComplaintReason())
      .reasonDetails(complaintLineItem.getReasonDetails())
      .comments(complaintLineItem.getComments())
      .build();
  }

  /**
   * Send a Complaint as a notification.
   *
   * @param id Commplaint id.
   */
  public void sendComplaint(UUID id) {
    Optional<Complaint> optionalComplaint =  complaintsRepository.findById(id);
    if (optionalComplaint.isPresent()) {
      Complaint complaint = optionalComplaint.get();
      RightDto right = rightReferenceDataService.findRight("STOCK_INVENTORIES_EDIT");
      if (right != null) {
        complaintNotifier.notifyRecipients(complaint, right.getId());
      }
      
      //Notify NDSO
      List<UserDto> supplierUsers = userReferenceDataService.findUsers("ndso");
      if (supplierUsers != null) {
        complaintNotifier.notifyRecipients(complaint, supplierUsers);
      } 
    }
  }
}
