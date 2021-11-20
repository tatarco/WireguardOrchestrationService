package com.talon.sec.wireguardorchestrationservice.clients;

import com.talon.sec.wireguardorchestrationservice.entities.IpAllocation;
import com.talon.sec.wireguardorchestrationservice.models.PeerEntity;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.springframework.http.HttpMethod.POST;

@Service
public class PeerAgentNotificationClient
{

    public void notify(PeerEntity otherPeer, NetworkPeerUpdateRequestModel networkPeerUpdate)
    {


        HttpEntity<NetworkPeerUpdateRequestModel> networkPeerUpdateRequestModelHttpEntity = new HttpEntity<>(networkPeerUpdate);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(buildUrl(otherPeer), POST, networkPeerUpdateRequestModelHttpEntity, Void.class);

    }

    @SneakyThrows
    public boolean isAlive(PeerEntity peer)
    {
        IpAllocation ipAllocation = peer.getAllocatedIp();
        InetAddress inet = null;
        try
        {
            inet = InetAddress.getByName(ipAllocation.getIp());
        }
        catch (UnknownHostException e)
        {
            return false;
        }
        return inet.isReachable(5000);
    }

    private String buildUrl(PeerEntity otherPeer)
    {
        return String.format("http://%s/api/peer", otherPeer.getAllocatedIp());
    }
}
