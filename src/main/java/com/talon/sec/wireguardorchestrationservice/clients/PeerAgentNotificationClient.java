package com.talon.sec.wireguardorchestrationservice.clients;

import com.talon.sec.wireguardorchestrationservice.models.Peer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.URI;

import static org.springframework.http.HttpMethod.POST;

@Service
public class PeerAgentNotificationClient
{

    public void notify(Peer otherPeer, NetworkPeerUpdateRequestModel networkPeerUpdate)
    {


        HttpEntity<NetworkPeerUpdateRequestModel> networkPeerUpdateRequestModelHttpEntity = new HttpEntity<>(networkPeerUpdate);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(buildUrl(otherPeer), POST, networkPeerUpdateRequestModelHttpEntity, Void.class);

    }

    @SneakyThrows
    public boolean isAlive(Peer peer)
    {
        String ipAddress = peer.getAllocatedIp();
        InetAddress inet = InetAddress.getByName(ipAddress);
        return inet.isReachable(5000);
    }

    private String buildUrl(Peer otherPeer)
    {
        return String.format("%s/api/peer", otherPeer.getAllocatedIp());
    }
}
