package com.samim.SpringVaultPrac.service;

import com.samim.SpringVaultPrac.data.Secrets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.UUID;

import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_2;

@Service
public class VaultService {

    @Autowired
    private VaultTemplate vaultTemplate;

    // WORKING FINE
//    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        String uuid = UUID.randomUUID().toString();
        save(uuid);
        delete(uuid);
    }

    public void save(String uuid) {
        Secrets secrets = new Secrets("John", "12345");
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue("secret", KV_2);
        vaultKeyValueOperations.put(uuid, secrets);
        System.out.println("saved");
    }

    public void delete(String uuid) {
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue("secret/", KV_2);
        VaultResponseSupport<Secrets> responseSupport = vaultKeyValueOperations.get(uuid, Secrets.class);
        if (responseSupport == null) {
            System.out.println("Secret Data not found");
            return;
        }
        System.out.println("Secret Data found");
        Secrets secrets = responseSupport.getData();
        System.out.println(secrets.toString());
        vaultKeyValueOperations.delete(uuid);
        System.out.println("deleted");
    }
}
