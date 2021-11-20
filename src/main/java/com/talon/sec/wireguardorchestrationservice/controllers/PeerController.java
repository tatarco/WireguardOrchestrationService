package com.talon.sec.wireguardorchestrationservice.controllers;

import com.talon.sec.wireguardorchestrationservice.models.PeerRegistrationRequestModel;
import com.talon.sec.wireguardorchestrationservice.models.PeerRegistrationResponseModel;
import com.talon.sec.wireguardorchestrationservice.services.PeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class PeerController
{
    public static final String REGISTER_PATH = "/register";
    private final PeerService peerService;
    @PostMapping(REGISTER_PATH)
    PeerRegistrationResponseModel registerPeer(@RequestBody PeerRegistrationRequestModel peerRegistrationRequestModel) {
        return peerService.registerAndNotify(peerRegistrationRequestModel);
    }
    @DeleteMapping("/register/{id}")
    void registerPeer(@PathVariable Long id) {
        peerService.unRegisterPeer(id);
    }
}
