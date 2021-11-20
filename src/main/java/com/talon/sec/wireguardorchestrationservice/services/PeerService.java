package com.talon.sec.wireguardorchestrationservice.services;

import com.talon.sec.wireguardorchestrationservice.clients.NetworkPeerUpdateRequestModel;
import com.talon.sec.wireguardorchestrationservice.clients.PeerAgentNotificationClient;
import com.talon.sec.wireguardorchestrationservice.entities.IpAllocation;
import com.talon.sec.wireguardorchestrationservice.models.*;
import com.talon.sec.wireguardorchestrationservice.models.enums.NetworkEvent;
import com.talon.sec.wireguardorchestrationservice.repositories.PeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PeerService
{
    private final PublicKeyService publicKeyService;
    private final IpAlocationService ipAlocationService;
    private final PeerRepository peerRepository;
    private final PeerAgentNotificationClient peerAgentNotificationClient;

    public PeerRegistrationResponseModel registerAndNotify(PeerRegistrationRequestModel peerRegistrationRequestModel)
    {
        Long tenantId = publicKeyService.getTanentId(peerRegistrationRequestModel.getPublicKey());
        IpAllocation allocatedIp = ipAlocationService.alocateIp(tenantId);
        PeerEntity peer = buildPeer(peerRegistrationRequestModel, allocatedIp);
        List<PeerEntity> peers = peerRepository.getPeersByTenantId(tenantId);
        PeerEntity newPeerAdded = peerRepository.save(peer);
        System.out.println("mainThread" + Thread.currentThread().getName());
        notifyAllPeers(peers, newPeerAdded, NetworkEvent.ADD_PEER);
        return PeerRegistrationResponseModel.builder()
                .peerList(peers)
                .id(peer.getId())
                .allocatedIp(allocatedIp.getIp())
                .build();
    }

    public void unRegisterPeer(Long id)
    {
        PeerEntity peer = peerRepository.getById(id);
        unRegisterPeer(peer);
    }

    public void unRegisterPeer(PeerEntity peer)
    {
        peerRepository.delete(peer);
        Long tenantId = peer.getTenantId();
        IpAllocation allocatedIp = peer.getAllocatedIp();
        ipAlocationService.deAlocateIp(allocatedIp);
        List<PeerEntity> peersByTenantId = peerRepository.getPeersByTenantId(tenantId);
        peersByTenantId.forEach(otherPeer -> notifyOtherPeer(otherPeer, peer, NetworkEvent.REMOVE_PEER));
    }
    @Async
    public void notifyAllPeers(List<PeerEntity> otherPeers, PeerEntity savedPeer, NetworkEvent networkEvent)
    {
        System.out.println("notifyingThread" + Thread.currentThread().getName());
        otherPeers.forEach(otherPeer -> notifyOtherPeer(otherPeer, savedPeer, networkEvent));
    }

    private void notifyOtherPeer(PeerEntity peerToNotify, PeerEntity notificationSubjectPeer, NetworkEvent networkEvent)
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

    private PeerEntity buildPeer(PeerRegistrationRequestModel peerRegistrationRequestModel, IpAllocation allocatedIp)
    {
        String name = peerRegistrationRequestModel.getName();
        String publicKey = peerRegistrationRequestModel.getPublicKey();
        return PeerEntity.builder()
                .publicKey(publicKey)
                .name(name)
                .allocatedIp(allocatedIp).build();
    }
}
