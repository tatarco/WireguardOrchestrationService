package com.talon.sec.wireguardorchestrationservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Peer
{
    @Id
    private Long id;
    private String publicKey;
    private String name;
    private Long tenantId;
    private String allocatedIp;
    private LocalDateTime lastSeen;
}
