package com.talon.sec.wireguardorchestrationservice.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IpAllocation
{
    @Id
    String ip;
    boolean allocated = false;
    @NonNull
    Long tenantId;
    @Version
    private Integer version;

}
