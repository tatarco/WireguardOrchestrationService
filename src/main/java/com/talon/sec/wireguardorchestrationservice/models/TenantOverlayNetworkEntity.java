package com.talon.sec.wireguardorchestrationservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TenantOverlayNetworkEntity
{
    @Id
    Long id;
    @ElementCollection
    Set<String> allocatedIps;
    @ElementCollection
    Set<String> deallocatedIps;
}
