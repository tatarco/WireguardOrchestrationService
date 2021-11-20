package com.talon.sec.wireguardorchestrationservice.schedulers;

import com.talon.sec.wireguardorchestrationservice.clients.PeerAgentNotificationClient;
import com.talon.sec.wireguardorchestrationservice.models.PeerEntity;
import com.talon.sec.wireguardorchestrationservice.repositories.PeerRepository;
import com.talon.sec.wireguardorchestrationservice.services.PeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OffboarderScheduler
{

    private final PeerRepository peerRepository;
    private final PeerAgentNotificationClient peerAgentNotificationClient;
    private final PeerService peerService;

    @Scheduled(fixedDelay = 10000)
    public void searchForDetachedClientsAndOffBoardThem()
    {
        List<PeerEntity> peerBatch = pullBatchByLastSeenDateAsc();
        updateSeen(peerBatch);
        peerBatch.stream()
                .filter(this::notAlive)
                .forEach(this::handleLostClient);
    }

    private void handleLostClient(PeerEntity peer)
    {
        peerService.unRegisterPeer(peer);
    }

    private boolean notAlive(PeerEntity peer)
    {
        boolean isAlive = peerAgentNotificationClient.isAlive(peer);
        return !isAlive;
    }

    private void updateSeen(List<PeerEntity> peerBatch)
    {
        peerBatch.forEach(peer -> {
            peer.setLastSeen(LocalDateTime.now());
            peerRepository.save(peer);
        });
    }

    private List<PeerEntity> pullBatchByLastSeenDateAsc()
    {
        return peerRepository.findTop10ByOrderByLastSeenAsc();
    }
}
