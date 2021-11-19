package com.talon.sec.wireguardorchestrationservice.services;

import com.talon.sec.wireguardorchestrationservice.clients.NetworkPeerUpdateRequestModel;
import com.talon.sec.wireguardorchestrationservice.clients.PeerAgentNotificationClient;
import com.talon.sec.wireguardorchestrationservice.models.*;
import com.talon.sec.wireguardorchestrationservice.models.enums.NetworkEvent;
import com.talon.sec.wireguardorchestrationservice.repositories.PeerRepository;
import com.talon.sec.wireguardorchestrationservice.repositories.TenantOverlayNetworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PeerService
{
    private final PublicKeyService publicKeyService;
    private final IpAlocationService ipAlocationService;
    private final TenantOverlayNetworkRepository tenantOverlayNetworkRepository;
    private final PeerRepository peerRepository;
    private final PeerAgentNotificationClient peerAgentNotificationClient;

    public PeerRegistrationResponseModel registerAndNotify(PeerRegistrationRequestModel peerRegistrationRequestModel)
    {
        Long tenantId = publicKeyService.getTanentId(peerRegistrationRequestModel.getPublicKey());
        TenantOverlayNetworkEntity tenantOverlayNetworkEntity = tenantOverlayNetworkRepository.getById(tenantId);
        String allocatedIp = ipAlocationService.alocateIp(tenantOverlayNetworkEntity);
        Peer peer = buildPeer(peerRegistrationRequestModel, allocatedIp);
        List<Peer> peers = peerRepository.getPeersByTenantId(tenantId);
        Peer newPeerAdded = peerRepository.save(peer);
        notifyAllPeers(peers, newPeerAdded, NetworkEvent.ADD_PEER);
        return PeerRegistrationResponseModel.builder()
                .peerList(peers)
                .allocatedIp(allocatedIp)
                .build();
    }

    public void unRegisterPeer(Long id)
    {
        Peer peer = peerRepository.getById(id);
        unRegisterPeer(peer);
    }

    public void unRegisterPeer(Peer peer)
    {
        peerRepository.delete(peer);
        Long tenantId = peer.getTenantId();
        TenantOverlayNetworkEntity tenantOverlayNetworkEntity = tenantOverlayNetworkRepository.getById(tenantId);
        String allocatedIp = peer.getAllocatedIp();
        tenantOverlayNetworkEntity.getAllocatedIps().remove(allocatedIp);
        tenantOverlayNetworkEntity.getDeallocatedIps().add(allocatedIp);
        List<Peer> peersByTenantId = peerRepository.getPeersByTenantId(tenantId);
        peersByTenantId.forEach(otherPeer -> notifyOtherPeer(otherPeer, peer, NetworkEvent.REMOVE_PEER));
    }

    private void notifyAllPeers(List<Peer> otherPeers, Peer savedPeer, NetworkEvent networkEvent)
    {
        otherPeers.forEach(otherPeer -> notifyOtherPeer(otherPeer, savedPeer, networkEvent));
    }

    private void notifyOtherPeer(Peer peerToNotify, Peer notificationSubjectPeer, NetworkEvent networkEvent)
    {
        try
        {
            NetworkPeerUpdateRequestModel networkPeerUpdateRequestModel = NetworkPeerUpdateRequestModel.builder()
                    .networkEvent(networkEvent)
                    .peer(notificationSubjectPeer)
                    .build();
            peerAgentNotificationClient.notify(peerToNotify, networkPeerUpdateRequestModel);
        }
        catch (ResourceAccessException e)
        {
            unRegisterPeer(peerToNotify);
        }
    }

    private Peer buildPeer(PeerRegistrationRequestModel peerRegistrationRequestModel, String allocatedIp)
    {
        String name = peerRegistrationRequestModel.getName();
        String publicKey = peerRegistrationRequestModel.getPublicKey();
        return Peer.builder()
                .publicKey(publicKey)
                .name(name)
                .allocatedIp(allocatedIp).build();
    }
}
