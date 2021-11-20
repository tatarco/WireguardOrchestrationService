package com.talon.sec.wireguardorchestrationservice.repositories;

import com.talon.sec.wireguardorchestrationservice.entities.IpAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface IpAllocationRepository extends JpaRepository<IpAllocation,String>
{
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<IpAllocation> findFirstByAllocatedAndTenantId(boolean allocated, Long tenantId);
}

