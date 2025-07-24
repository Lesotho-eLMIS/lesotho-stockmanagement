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

package org.openlmis.stockmanagement.service.referencedata;

import java.util.Collection;
import java.util.UUID;

public class ApprovedProductQueryDto {
  private Collection<UUID> programId;
  private Collection<UUID> orderableId;
  private String orderableCode;
  private String orderableName;

  // Getters and Setters
  public Collection<UUID> getProgramId() {
    return programId;
  }

  public void setProgramId(Collection<UUID> programId) {     
    this.programId = programId;
  }

  public Collection<UUID> getOrderableId() {
    return orderableId;
  }

  public void setOrderableId(Collection<UUID> orderableId) {
    this.orderableId = orderableId;
  }

  public String getOrderableCode() {
    return orderableCode;
  }

  public void setOrderableCode(String orderableCode) {
    this.orderableCode = orderableCode;
  }

  public String getOrderableName() {
    return orderableName;
  }

  public void setOrderableName(String orderableName) {
    this.orderableName = orderableName;
  }
  
}

