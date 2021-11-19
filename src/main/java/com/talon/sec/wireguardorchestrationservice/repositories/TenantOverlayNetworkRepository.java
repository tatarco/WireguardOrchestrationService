package com.talon.sec.wireguardorchestrationservice.repositories;

import com.talon.sec.wireguardorchestrationservice.models.TenantOverlayNetworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantOverlayNetworkRepository extends JpaRepository<TenantOverlayNetworkEntity,Long>
{
    TenantOverlayNetworkEntity getById(Long tenantId);
}
