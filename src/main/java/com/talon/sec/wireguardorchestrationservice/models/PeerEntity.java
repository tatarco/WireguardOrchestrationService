package com.talon.sec.wireguardorchestrationservice.models;

import com.sun.istack.NotNull;
import com.talon.sec.wireguardorchestrationservice.entities.IpAllocation;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PeerEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String publicKey;
    private String name;
    private Long tenantId;
    @NonNull
    @OneToOne
    @JoinColumn(name = "allocated_ip_ip")
    private IpAllocation allocatedIp;
    private LocalDateTime lastSeen;
}
