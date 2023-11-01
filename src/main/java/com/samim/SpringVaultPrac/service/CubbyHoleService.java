package com.samim.SpringVaultPrac.service;

import com.samim.SpringVaultPrac.config.VaultConfig;
import com.samim.SpringVaultPrac.data.Secrets;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.UUID;

import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_1;
import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_2;

@Service
public class CubbyHoleService {

//    @EventListener(ApplicationReadyEvent.class)
    public void cubbyHoleWrite() {
        String uuid = UUID.randomUUID().toString();
        VaultTemplate vaultTemplate = getVaultTemplate();
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue("cubbyhole/", KV_2);
        vaultKeyValueOperations.put(uuid, new Secrets("john_cubby", "wick_value"));
        System.out.println("saved");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void cubbyHoleRead() {
        VaultTemplate vaultTemplate = getVaultTemplate();
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue("cubbyhole/", KV_1);
        VaultResponseSupport<Secrets> responseSupport = vaultKeyValueOperations.get("my_confidential", Secrets.class);
        if (responseSupport == null) {
            System.out.println("CubbyHole Data not found");
            return;
        }
        System.out.println("CubbyHole Data found");
        Secrets secrets = responseSupport.getData();
        System.out.println(secrets.toString());
    }

    private VaultTemplate getVaultTemplate() {
        return new VaultTemplate(VaultConfig.vaultEndpoint, VaultConfig.getCubbyholeAuthentication());
    }
}
