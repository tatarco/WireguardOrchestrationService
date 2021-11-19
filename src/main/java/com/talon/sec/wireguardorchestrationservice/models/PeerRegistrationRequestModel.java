package com.talon.sec.wireguardorchestrationservice.models;

import lombok.Data;

@Data
public class PeerRegistrationRequestModel
{
    private String name;
    private String publicKey;

}
