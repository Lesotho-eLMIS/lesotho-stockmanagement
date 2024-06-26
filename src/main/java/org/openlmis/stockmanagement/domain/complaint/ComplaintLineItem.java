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

package org.openlmis.stockmanagement.domain.complaint;
//import org.openlmis.stockmanagement.dto.StockEventAdjustmentDto;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.BaseEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "complaint_line_items", schema = "stockmanagement")
public class ComplaintLineItem extends BaseEntity {

  @Column(nullable = false)
  private UUID orderableId;

  private UUID lotId;

  @Column(nullable = false)
  private Integer quantityAffected;

  @Column(nullable = false)
  private Integer quantityReturned;

  private String natureOfComplaint;

  private String complaintReason;

  private String reasonDetails;

  private String comments;

  @ManyToOne()
  @JoinColumn(nullable = false)
  private Complaint complaint;

}
