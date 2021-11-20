package com.talon.sec.wireguardorchestrationservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PeerRegistrationRequestModel
{
    private String name;
    private String publicKey;

}
