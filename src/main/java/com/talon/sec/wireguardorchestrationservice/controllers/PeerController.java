package com.talon.sec.wireguardorchestrationservice.controllers;

import com.talon.sec.wireguardorchestrationservice.models.PeerRegistrationRequestModel;
import com.talon.sec.wireguardorchestrationservice.models.PeerRegistrationResponseModel;
import com.talon.sec.wireguardorchestrationservice.services.PeerService;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
public class PeerController
{
    private PeerService peerService;

    @PostMapping("/register")
    PeerRegistrationResponseModel registerPeer(@RequestBody PeerRegistrationRequestModel peerRegistrationRequestModel) {
        return peerService.registerAndNotify(peerRegistrationRequestModel);
    }
    @DeleteMapping("/register/{id}")
    void registerPeer(@PathVariable Long id) {
        peerService.unRegisterPeer(id);
    }
}
