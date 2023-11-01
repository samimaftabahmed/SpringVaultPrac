package com.samim.SpringVaultPrac.service;

import com.samim.SpringVaultPrac.config.VaultConfig;
import com.samim.SpringVaultPrac.data.Secrets;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultTokenRequest;
import org.springframework.vault.support.VaultTokenResponse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CubbyHoleService {

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        String cubbyholeToken = getCubbyholeToken().getToken().getToken();
        String uuid = UUID.randomUUID().toString();
        cubbyHoleWrite(uuid, cubbyholeToken);
        cubbyHoleRead(uuid, cubbyholeToken);
    }

    public void cubbyHoleWrite(String uuid, String token) {
        VaultTemplate vaultTemplate = getVaultTemplate(token);
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate
                .opsForKeyValue("cubbyhole", VaultKeyValueOperationsSupport.KeyValueBackend.KV_1);
        vaultKeyValueOperations.put(uuid, new Secrets("john_cubby", "wick_value"));
        System.out.println("CubbyHole Data Saved");
    }

    public void cubbyHoleRead(String uuid, String token) {
        VaultTemplate vaultTemplate = getVaultTemplate(token);
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate
                .opsForKeyValue("cubbyhole", VaultKeyValueOperationsSupport.KeyValueBackend.KV_1);
        VaultResponseSupport<Secrets> responseSupport = vaultKeyValueOperations.get(uuid, Secrets.class);
        if (responseSupport == null) {
            System.out.println("CubbyHole Data not found");
            return;
        }
        System.out.println("CubbyHole Data found");
        Secrets secrets = responseSupport.getData();
        System.out.println(secrets.toString());
    }

    private VaultTemplate getVaultTemplate(String token) {
        return new VaultTemplate(VaultConfig.vaultEndpoint, VaultConfig.getCubbyholeAuthentication(token));
    }

    private VaultTokenResponse getCubbyholeToken(){
        List<String> policies = Arrays.asList("default", "cubbyhole-policy", "cubbyhole-policy-token");
        VaultTokenRequest tokenRequest = VaultTokenRequest.builder()
                .ttl(10, TimeUnit.MINUTES).numUses(2).policies(policies).renewable(true)
                .build();
        return VaultConfig.vaultTemplate.opsForToken().create(tokenRequest);
    }
}
