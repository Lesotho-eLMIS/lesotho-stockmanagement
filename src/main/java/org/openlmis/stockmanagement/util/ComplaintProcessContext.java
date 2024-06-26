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

package org.openlmis.stockmanagement.util;

import java.util.UUID;
import lombok.Setter;

import org.openlmis.stockmanagement.dto.referencedata.FacilityDto;


/**
 * Before processing a pod event, one instance of this class will be created to hold all things
 * needed from ref-data service. By doing this, all network traffic is concentrated in one place,
 * not scattered around in different places. All resources use lazy loading so they are retrieved
 * only when there is a need.
 */
@Setter
public class ComplaintProcessContext {

  private LazyResource<UUID> currentUserId;
  private LazyResource<String> currentUserNames;
  private LazyResource<FacilityDto> facility;

  public UUID getCurrentUserId() {
    return currentUserId.get();
  }

  public String getCurrentUserNames() {
    return currentUserNames.get();
  }

  public FacilityDto getFacility() {
    return facility.get();
  }
}
