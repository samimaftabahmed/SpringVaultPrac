package com.samim.SpringVaultPrac.service;

import com.github.javafaker.Faker;
import com.samim.SpringVaultPrac.config.VaultConfig;
import com.samim.SpringVaultPrac.data.Secrets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CubbyHoleService {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private VaultTemplate vaultTemplate;
    @Autowired
    private VaultEndpoint vaultEndpoint;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            System.out.println("--Running init::CubbyHoleService");
            String cubbyholeToken = getCubbyholeToken().getToken().getToken();
            String uuid = UUID.randomUUID().toString();
            cubbyholeWrite(uuid, cubbyholeToken, true); // FAILS
            cubbyholeRead(uuid, cubbyholeToken, true); // FAILS

        } catch (Exception t) {
            System.err.println(t.getMessage());
        }
    }

    public void cubbyholeWrite(String uuid, String token, boolean isCubbyhole) {
        printDetails(uuid, token);
        VaultTemplate vaultTemplate = getVaultTemplate(token, isCubbyhole);
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate
                .opsForKeyValue("cubbyhole", VaultKeyValueOperationsSupport.KeyValueBackend.KV_1);
        Faker faker = Faker.instance();
        Secrets secret = new Secrets(faker.name().firstName(), faker.address().cityName());
        vaultKeyValueOperations.put(uuid, secret);
        System.out.println("--Cubbyhole Data Saved");
    }

    public void cubbyholeRead(String uuid, String token, boolean isCubbyhole) {
        printDetails(uuid, token);
        VaultTemplate vaultTemplate = getVaultTemplate(token, isCubbyhole);
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate
                .opsForKeyValue("cubbyhole", VaultKeyValueOperationsSupport.KeyValueBackend.KV_1);
        VaultResponseSupport<Secrets> responseSupport = vaultKeyValueOperations.get(uuid, Secrets.class);
        if (responseSupport == null) {
            System.out.println("--Cubbyhole Data not found");
            return;
        }
        System.out.println("--Cubbyhole Data found");
        Secrets secrets = responseSupport.getData();
        System.out.println(secrets.toString());
    }

    private VaultTemplate getVaultTemplate(String token, boolean isCubbyhole) {
        ClientAuthentication clientAuthentication;
        if (isCubbyhole) {
            // NOT WORKING
            clientAuthentication = VaultConfig.getCubbyholeAuthentication(token);
        } else {
            // WORKING
            clientAuthentication = VaultConfig.getTokenAuthentication(token);
        }

        return new VaultTemplate(vaultEndpoint, clientAuthentication);
    }

    public VaultTokenResponse getCubbyholeToken() {
        List<String> policies = Arrays.asList("default", "cubbyhole-policy", "cubbyhole-policy-token");
        VaultTokenRequest tokenRequest = VaultTokenRequest.builder()
                .ttl(10, TimeUnit.MINUTES).numUses(4).policies(policies).renewable(true)
                .build();
        return vaultTemplate.opsForToken().create(tokenRequest);
    }

    private void printDetails(String uuid, String token) {
        System.out.println("\nUUID: " + uuid);
        System.out.println("Token: " + token);
        System.out.println("Counter: " + counter.incrementAndGet());
        System.out.println("");
    }
}
