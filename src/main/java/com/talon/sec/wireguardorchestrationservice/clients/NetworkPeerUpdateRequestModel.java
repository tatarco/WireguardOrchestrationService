package com.talon.sec.wireguardorchestrationservice.clients;

import com.talon.sec.wireguardorchestrationservice.models.Peer;
import com.talon.sec.wireguardorchestrationservice.models.enums.NetworkEvent;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NetworkPeerUpdateRequestModel
{
    private final Peer peer;
    private final NetworkEvent networkEvent;

}
