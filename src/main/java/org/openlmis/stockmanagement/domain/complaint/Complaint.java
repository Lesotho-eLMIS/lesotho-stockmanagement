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

import static javax.persistence.CascadeType.ALL;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.BaseEntity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "complaints", schema = "stockmanagement")
public class Complaint extends BaseEntity {

  @Column(nullable = false)
  private UUID facilityId;
  

  @Column(nullable = false)
  private UUID userId;

  private String userNames;

  @Column(nullable = false, columnDefinition = "timestamp")
  private ZonedDateTime occurredDate;

  private String invoiceNumber;

  private String purchaseOrderNumber;

  @OneToMany(cascade = ALL, mappedBy = "complaint")
  private List<ComplaintLineItem> lineItems;
}
