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
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestOperations;

@Configuration
public class VaultConfig extends AbstractVaultConfiguration {

    public static VaultEndpoint vaultEndpoint;
    private static RestOperations restOperations;
    public static VaultTemplate vaultTemplate;

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

    public static ClientAuthentication getTokenAuthentication(String token) {
        return new TokenAuthentication(token);
    }

    public static CubbyholeAuthentication getCubbyholeAuthentication(String token) {
        CubbyholeAuthenticationOptions options = CubbyholeAuthenticationOptions.builder()
                .initialToken(VaultToken.of(token))
                .path("cubbyhole/token")
                .build();
        return new CubbyholeAuthentication(options, VaultConfig.restOperations);
    }
}
