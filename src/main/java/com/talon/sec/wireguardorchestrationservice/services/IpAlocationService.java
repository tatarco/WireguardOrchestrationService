package com.talon.sec.wireguardorchestrationservice.services;

import com.talon.sec.wireguardorchestrationservice.models.TenantOverlayNetworkEntity;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class IpAlocationService
{
    public String alocateIp(TenantOverlayNetworkEntity tenantOverlayNetworkEntity)
    {
        Iterator<String> iterator = tenantOverlayNetworkEntity.getDeallocatedIps().iterator();
        String allocatedIp = iterator.next();
        tenantOverlayNetworkEntity.getDeallocatedIps().add(allocatedIp);
        iterator.remove();
        return allocatedIp;
    }
}
