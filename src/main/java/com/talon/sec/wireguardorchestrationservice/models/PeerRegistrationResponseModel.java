package com.talon.sec.wireguardorchestrationservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PeerRegistrationResponseModel
{
    Long id;
    List<PeerEntity> peerList;
    String allocatedIp;
}
