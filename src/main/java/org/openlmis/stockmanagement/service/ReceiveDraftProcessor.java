package org.openlmis.stockmanagement.service;

import org.openlmis.stockmanagement.domain.receivedraft.ReceiveDraft;
import org.openlmis.stockmanagement.dto.receive.ReceiveDraftDto;
import org.openlmis.stockmanagement.repository.ReceiveDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReceiveDraftProcessor {
    @Autowired
    private ReceiveDraftRepository receiveDraftRepository;


    /**
     * Validate and persist event and create stock card and line items from it.
     *
     * @param receiveDraftDto stock event dto.
     * @return the persisted draft ids.
     */
    public UUID process(ReceiveDraftDto receiveDraftDto){
        UUID receiveDraftId = saveReceiveDraftAndGenerateLineItems(receiveDraftDto);
        return receiveDraftId;
    }

    public UUID saveReceiveDraftAndGenerateLineItems(ReceiveDraftDto receiveDraftDto){
        ReceiveDraft receiveDraft =receiveDraftDto.toReceiveDraft();

        UUID savedDraftId = receiveDraftRepository.save(receiveDraft).getId();
        return savedDraftId;
    }
}
