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

package org.openlmis.stockmanagement.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.openlmis.stockmanagement.domain.complaint.ComplaintLineItem;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintLineItemDto {
  private UUID id;
  private UUID orderableId;
  private UUID lotId;
  private Integer quantityAffected;
  private Integer quantityReturned;
  private String natureOfComplaint;
  private String comments;
  

  ComplaintLineItem toComplaintLineItem() {
    return new ComplaintLineItem(
        orderableId, lotId, quantityAffected, quantityReturned,
        natureOfComplaint, comments, null);
  }

  public boolean hasLotId() {
    return this.lotId != null;
  }
}
