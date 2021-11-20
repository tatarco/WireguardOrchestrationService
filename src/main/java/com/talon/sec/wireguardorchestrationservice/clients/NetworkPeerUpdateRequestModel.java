package com.talon.sec.wireguardorchestrationservice.clients;

import com.talon.sec.wireguardorchestrationservice.models.PeerEntity;
import com.talon.sec.wireguardorchestrationservice.models.enums.NetworkEvent;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NetworkPeerUpdateRequestModel
{
    private final PeerEntity peer;
    private final NetworkEvent networkEvent;

}
