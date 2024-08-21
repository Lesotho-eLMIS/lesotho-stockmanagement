package org.openlmis.stockmanagement.web.receivedraft;

import org.openlmis.stockmanagement.dto.receive.ReceiveDraftDto;
import org.openlmis.stockmanagement.service.HomeFacilityPermissionService;
import org.openlmis.stockmanagement.service.PermissionService;
import org.openlmis.stockmanagement.service.ReceiveDraftProcessor;
import org.openlmis.stockmanagement.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Transactional
@RequestMapping("/api")
public class ReceiveDraftController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveDraftController.class);

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ReceiveDraftProcessor receiveDraftProcessor;

    @Autowired
    private HomeFacilityPermissionService homeFacilityPermissionService;
    /**
     * Create receive draft.
     *
     * @param receiveDraftDto a stock event bound to request body.
     * @return created draft event's ID.
     */
    @RequestMapping(value = "receiveDraft", method = POST)
    public ResponseEntity<UUID> createReceiveDraft (@RequestBody ReceiveDraftDto receiveDraftDto) {
        UUID receiveDraftId = receiveDraftProcessor.process(receiveDraftDto);
        ResponseEntity<UUID> response = new ResponseEntity<>(receiveDraftId, CREATED);
        return response;
    }
}
