package com.samim.SpringVaultPrac.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthenticationOptions;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultTokenRequest;
import org.springframework.vault.support.VaultTokenResponse;
import org.springframework.web.client.RestOperations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class VaultConfig extends AbstractVaultConfiguration {

    public static VaultEndpoint vaultEndpoint;
    private static RestOperations restOperations;
    private static VaultTemplate vaultTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        VaultConfig.restOperations = restOperations();
        VaultConfig.vaultEndpoint = vaultEndpoint();
        VaultConfig.vaultTemplate = vaultTemplate();
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        VaultEndpoint vaultEndpoint = VaultEndpoint.create("localhost", 8200);
        vaultEndpoint.setScheme("http");
        return vaultEndpoint;
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication("mytoken");
    }

    public static CubbyholeAuthentication getCubbyholeAuthentication() {
        List<String> policies = Arrays.asList("default", "cubbyhole-policy", "cubbyhole-policy-token");
        VaultTokenRequest tokenRequest = VaultTokenRequest.builder()
                .ttl(120, TimeUnit.MINUTES).numUses(100).policies(policies).renewable(true)
                .build();
        VaultTokenResponse vaultTokenResponse = VaultConfig.vaultTemplate.opsForToken().create(tokenRequest);
        CubbyholeAuthenticationOptions options = CubbyholeAuthenticationOptions.builder()
                .initialToken(vaultTokenResponse.getToken())
                .path("cubbyhole/token").build();
        return new CubbyholeAuthentication(options, VaultConfig.restOperations);
    }
}
