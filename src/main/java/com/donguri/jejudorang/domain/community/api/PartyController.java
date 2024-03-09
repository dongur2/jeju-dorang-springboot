package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.api.swagger.PartyControllerDocs;
import com.donguri.jejudorang.domain.community.service.PartyService;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/community/parties")
public class PartyController implements PartyControllerDocs {

    @Autowired
    private PartyService partyService;

    /*
    * 모집상태 변경
    * /community/parties/{communityId}/state
    *
    * */
    @PutMapping("/{communityId}/state")
    public ResponseEntity<?> modifyBoardJoinState(@PathVariable("communityId") Long communityId) {
        try {
            partyService.changePartyJoinState(communityId);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
