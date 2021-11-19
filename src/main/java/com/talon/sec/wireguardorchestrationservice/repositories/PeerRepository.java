package com.talon.sec.wireguardorchestrationservice.repositories;

import com.talon.sec.wireguardorchestrationservice.models.Peer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeerRepository extends JpaRepository<Peer,Long>
{
    List<Peer> getPeersByTenantId(Long tenantId);

    List<Peer> findTop10ByOrderByLastSeenAsc();
}
