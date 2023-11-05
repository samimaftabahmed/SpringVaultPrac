package com.samim.SpringVaultPrac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    private static RestOperations restOperations;

    @Bean
    @Override
    public VaultTemplate vaultTemplate() {
        return super.vaultTemplate();
    }

    @Bean
    @Override
    public VaultEndpoint vaultEndpoint() {
        VaultEndpoint vaultEndpoint = VaultEndpoint.create("localhost", 8200);
        vaultEndpoint.setScheme("http");
        return vaultEndpoint;
    }

    @Bean
    @Override
    public RestOperations restOperations() {
        restOperations = super.restOperations();
        return restOperations;
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
