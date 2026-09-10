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

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.openlmis.stockmanagement.domain.card.StockCardLineItem;
import org.openlmis.stockmanagement.domain.event.EventOrigin;
import org.openlmis.stockmanagement.domain.event.StockEvent;
import org.openlmis.stockmanagement.domain.physicalinventory.PhysicalInventoryLineItemAdjustment;
import org.openlmis.stockmanagement.testutils.StockCardLineItemDataBuilder;
import org.openlmis.stockmanagement.testutils.StockCardLineItemReasonDataBuilder;
import org.openlmis.stockmanagement.testutils.StockEventDataBuilder;

public class StockCardLineItemDtoTest {

  @Test
  public void createFromShouldExposeOriginEventIdAndType() {
    StockEvent event = new StockEventDataBuilder().withEventOrigin(EventOrigin.ISSUE).build();
    StockCardLineItem lineItem = new StockCardLineItemDataBuilder().withOriginEvent(event).build();

    StockCardLineItemDto dto = StockCardLineItemDto.createFrom(lineItem);

    assertThat(dto.getOriginEventId(), is(event.getId()));
    assertThat(dto.getEventOrigin(), is(EventOrigin.ISSUE));
  }

  @Test
  public void createFromShouldHandleNullOriginEvent() {
    StockCardLineItem lineItem = new StockCardLineItemDataBuilder().withOriginEvent(null).build();

    StockCardLineItemDto dto = StockCardLineItemDto.createFrom(lineItem);

    assertThat(dto.getOriginEventId(), is(nullValue()));
    assertThat(dto.getEventOrigin(), is(nullValue()));
  }

  @Test
  public void shouldSerializeAdjustmentsWithoutRecursingIntoTheirParents() throws Exception {
    // Hibernate populates the adjustment's back-reference to its owning line item on read. If
    // those back-references are visible to Jackson, the unwrapped line item recurses:
    // lineItem -> stockAdjustments -> adjustment -> stockCardLineItem -> stockAdjustments -> ...
    StockCardLineItem lineItem = new StockCardLineItemDataBuilder().build();
    PhysicalInventoryLineItemAdjustment adjustment = PhysicalInventoryLineItemAdjustment.builder()
        .reason(new StockCardLineItemReasonDataBuilder().build())
        .quantity(5)
        .stockCardLineItem(lineItem)
        .build();
    lineItem.setStockAdjustments(singletonList(adjustment));

    String json = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .writeValueAsString(StockCardLineItemDto.createFrom(lineItem));

    JsonNode adjustments = new ObjectMapper().readTree(json).get("stockAdjustments");
    assertThat(adjustments.size(), is(1));

    JsonNode serialized = adjustments.get(0);
    assertThat(serialized.has("stockCardLineItem"), is(false));
    assertThat(serialized.has("stockEventLineItem"), is(false));
    assertThat(serialized.has("physicalInventoryLineItem"), is(false));

    // the adjustment itself must still be part of the response
    assertThat(serialized.get("quantity").asInt(), is(5));
    assertThat(serialized.has("reason"), is(true));
  }
}
