package com.talon.sec.wireguardorchestrationservice.services;

import com.talon.sec.wireguardorchestrationservice.entities.IpAllocation;
import com.talon.sec.wireguardorchestrationservice.exceptions.IpNotFoundException;
import com.talon.sec.wireguardorchestrationservice.repositories.IpAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IpAlocationService
{
    private final IpAllocationRepository ipAllocationRepository;

    @Transactional
    public IpAllocation alocateIp(Long tenantId)
    {
        Supplier<IpAllocation> longIpAllocationFunction = () -> {
            IpAllocation firstByAllocatedAndTenantId1 =
                    ipAllocationRepository.findFirstByAllocatedAndTenantId(false, tenantId)
                            .orElseThrow(()->new IpNotFoundException("for tenantId:"+tenantId));

            firstByAllocatedAndTenantId1.setAllocated(true);
            return firstByAllocatedAndTenantId1;
        };
        return actionWithRetryOverOptemisticLocking(longIpAllocationFunction);
    }

    public void deAlocateIp(IpAllocation allocatedIp)
    {
        Supplier<IpAllocation> ipAllocationCallable = () -> {
            String ip = allocatedIp.getIp();
            IpAllocation byId = ipAllocationRepository.getById(ip);
            byId.setAllocated(false);
            return byId;
        };
        actionWithRetryOverOptemisticLocking(ipAllocationCallable);
    }

    private IpAllocation actionWithRetryOverOptemisticLocking(Supplier<IpAllocation> longIpAllocationFunction)
    {
        IpAllocation firstByAllocatedAndTenantId;
        while (true)
        {
            firstByAllocatedAndTenantId = longIpAllocationFunction.get();
            boolean success = saveAndFlushCatchingOptemisticLockingException(firstByAllocatedAndTenantId);
            if (success)
            {
                break;
            }
        }
        return firstByAllocatedAndTenantId;
    }

    private boolean saveAndFlushCatchingOptemisticLockingException(IpAllocation firstByAllocatedAndTenantId)
    {
        try
        {
            ipAllocationRepository.saveAndFlush(firstByAllocatedAndTenantId);
            return true;
        }
        catch (OptimisticEntityLockException e)
        {
            return false;
        }
    }
}
