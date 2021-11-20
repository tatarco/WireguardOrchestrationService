package com.talon.sec.wireguardorchestrationservice.controllers;

import com.talon.sec.wireguardorchestrationservice.clients.PeerAgentNotificationClient;
import com.talon.sec.wireguardorchestrationservice.entities.IpAllocation;
import com.talon.sec.wireguardorchestrationservice.models.PeerEntity;
import com.talon.sec.wireguardorchestrationservice.models.PeerRegistrationRequestModel;
import com.talon.sec.wireguardorchestrationservice.models.PeerRegistrationResponseModel;
import com.talon.sec.wireguardorchestrationservice.repositories.IpAllocationRepository;
import com.talon.sec.wireguardorchestrationservice.repositories.PeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,properties = "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true")
class PeerControllerTest
{

    public static final String SOME_PUBLIC_KEY = "somePublicKey";
    public static final String SOME_NAME = "someName";
    public static final String AVAILABLE_IP_TANENT_1 = "212.1212.12212.1212";
    public static final String ASSIGNED_IP_TANENT_1 = "212.1212.12212.12123";
    public static final String ASSIGNED_IP_TANENT2 = "212.1212.1221332.12123";
    public static final String AVAILABLE_IP_TANENT2 = "212.1212.1233212.12123";
    public static final String EXISTING_PEER_IN_TENANT_1_NAME = "Existing Peer In Tenant1";
    public static final String EXISTING_PEER_IN_TENANT_2_NAME = "Existing Peer In Tenant2";
    @LocalServerPort
    private int port;
    @Autowired
    PeerRepository peerRepository;
    @Autowired
    IpAllocationRepository ipAllocationRepository;
    private RestTemplate restTemplate = new RestTemplateBuilder().build();
    private PeerRegistrationRequestModel request =PeerRegistrationRequestModel.builder().publicKey(SOME_PUBLIC_KEY).name(SOME_NAME).build();

    @BeforeEach
    void beforeAll(){
        ipAllocationRepository.save(IpAllocation.builder().ip(AVAILABLE_IP_TANENT_1).tenantId(1L).build());
        ipAllocationRepository.save(IpAllocation.builder().ip(AVAILABLE_IP_TANENT2).tenantId(2L).build());
        saveExistingPeerAndAssignIps(ASSIGNED_IP_TANENT_1, 1L, EXISTING_PEER_IN_TENANT_1_NAME);
        saveExistingPeerAndAssignIps(ASSIGNED_IP_TANENT2, 2L, EXISTING_PEER_IN_TENANT_2_NAME);
//        peerRepository.save(PeerEntity.builder().id(12L).tenantId(1L).allocatedIp(ip).build());
    }

    private void saveExistingPeerAndAssignIps(String ip, long tenantId, String name)
    {
        IpAllocation saved =
                ipAllocationRepository.save(IpAllocation.builder().ip(ip).tenantId(tenantId).build());
        peerRepository.save(PeerEntity.builder().tenantId(tenantId).name(name).allocatedIp(saved).build());
    }

    @Test
    void registerPeerHappyPath()
    {
        String url = buildTestUrl(PeerController.REGISTER_PATH);
        PeerRegistrationResponseModel peerRegistrationResponseModel = this.restTemplate.postForObject(url, request, PeerRegistrationResponseModel.class);
        String allocatedIp = peerRegistrationResponseModel.getAllocatedIp();
        assertThat(allocatedIp).isEqualTo(AVAILABLE_IP_TANENT_1);
        IpAllocation byId = ipAllocationRepository.getById(AVAILABLE_IP_TANENT_1);
        assertThat(byId.isAllocated()).isTrue();
        PeerEntity peerEntity = peerRepository.getById(peerRegistrationResponseModel.getId());
        assertThat(peerEntity).isNotNull();


    }

    private String buildTestUrl(String path)
    {
        return "http://localhost:" + port + path;
    }



    @Test
    void testUnregisterPeer()
    {
    }
}