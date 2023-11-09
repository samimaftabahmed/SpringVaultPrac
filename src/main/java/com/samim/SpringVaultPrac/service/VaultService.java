package com.samim.SpringVaultPrac.service;

import com.github.javafaker.Faker;
import com.samim.SpringVaultPrac.data.Secrets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;

import java.util.Map;
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
        String uuid2 = UUID.randomUUID().toString();
        save(uuid);
        save(uuid2);
        getAndDelete(uuid);
        responseWrappingTheSecrets(uuid2);
    }

    public void save(String uuid) {
        Faker faker = Faker.instance();
        Secrets secrets = new Secrets(faker.country().name(), faker.ancient().god());
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue("secret", KV_2);
        vaultKeyValueOperations.put(uuid, secrets);
        System.out.println("saved");
    }

    public void getAndDelete(String uuid) {
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

    public void responseWrappingTheSecrets(String uuid) {
        ResponseEntity<VaultResponse> response = vaultTemplate.doWithSession(restOperations -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Vault-Wrap-TTL", "10m");
            return restOperations.exchange("secret/data/" + uuid,
                    HttpMethod.GET, new HttpEntity<>(headers), VaultResponse.class);
        });

        Map<String, String> wrapInfo = response.getBody().getWrapInfo();
        // token to unwrap the response
        VaultToken vaultToken = VaultToken.of(wrapInfo.get("token"));
        VaultResponse vaultResponse = vaultTemplate.opsForWrapping().read(vaultToken);

        if (vaultResponse != null) {
            Map<String, String> map = (Map<String, String>) vaultResponse.getData().get("data");
            System.out.println(map.get("key"));
            System.out.println(map.get("value"));
        }
    }
}
