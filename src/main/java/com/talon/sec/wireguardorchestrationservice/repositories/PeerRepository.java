package com.talon.sec.wireguardorchestrationservice.repositories;

import com.talon.sec.wireguardorchestrationservice.models.PeerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeerRepository extends JpaRepository<PeerEntity,Long>
{
    List<PeerEntity> getPeersByTenantId(Long tenantId);

    List<PeerEntity> findTop10ByOrderByLastSeenAsc();
}
