package org.openlmis.stockmanagement.service;

import org.openlmis.stockmanagement.dto.receive.ReceiveDraftDto;
import org.openlmis.stockmanagement.repository.OrganizationRepository;
import org.openlmis.stockmanagement.service.referencedata.FacilityReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.LotReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.OrderableReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class ReceiveDraftService {
    @Autowired
    private OrderableReferenceDataService orderableRefDataService;

    @Autowired
    private FacilityReferenceDataService facilityRefDataService;
    @Autowired
    private LotReferenceDataService lotReferenceDataService;
    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * Generate stock card line items and stock cards based on event, and persist them.
     *
     * @param receivedDraftDto the origin event.
     * @param savedDraftId  saved event id.
     */
    @Transactional
    public void safeDraft(ReceiveDraftDto receivedDraftDto, UUID savedDraftId ){

    }
}
