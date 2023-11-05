package com.samim.SpringVaultPrac.controller;

import com.samim.SpringVaultPrac.service.CubbyHoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class VaultController {

    @Autowired
    private CubbyHoleService cubbyHoleService;

    /**
     * Write and then Read a key-value pair.
     *
     * @param isCubbyhole - Set 'true' for CubbyholeAuthentication.
     */
    @PostMapping("/write-read-cubby")
    public void writeReadCubby(@RequestParam boolean isCubbyhole) {
        String token = cubbyHoleService.getCubbyholeToken().getToken().getToken();
        System.out.println("\nToken: " + token);
        System.out.println("\n");

        try {
            String uuid = UUID.randomUUID().toString();
            cubbyHoleService.cubbyholeWrite(uuid, token, isCubbyhole);
            cubbyHoleService.cubbyholeRead(uuid, token, isCubbyhole);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
    }

    /**
     * Write and then Read a key-value pair.
     *
     * @param token       - path of the key-value(KV) pair.
     * @param isCubbyhole - Set 'true' for CubbyholeAuthentication.
     */
    @GetMapping("/write-read-cubby-with-token")
    public void writeReadCubbyholeWithToken(@RequestParam String token, @RequestParam boolean isCubbyhole) {
        System.out.println("\nToken: " + token);
        System.out.println("\n");

        try {
            String uuid = UUID.randomUUID().toString();
            cubbyHoleService.cubbyholeWrite(uuid, token, isCubbyhole);
            cubbyHoleService.cubbyholeRead(uuid, token, isCubbyhole);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
    }

    /**
     * View a key-value pair.
     *
     * @param uuid        - path of the key-value(KV) pair.
     * @param token       - token used to read a KV.
     * @param isCubbyhole - Set 'true' for CubbyholeAuthentication.
     * @return
     */
    @PostMapping("/view-cubby")
    public String viewCubby(@RequestParam String uuid, @RequestParam String token, @RequestParam boolean isCubbyhole) {
        try {
            cubbyHoleService.cubbyholeRead(uuid, token, isCubbyhole);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            return "Error: " + t.getMessage();
        }

        return "OK";
    }

    /**
     * Write a key-value pair.
     *
     * @param uuid        - path of the key-value(KV) pair.
     * @param token       - token to be used to write the KV.
     * @param isCubbyhole - Set 'true' for CubbyholeAuthentication.
     * @return
     */
    @PostMapping("/write-cubby")
    public String writeCubby(@RequestParam String uuid, @RequestParam String token, @RequestParam boolean isCubbyhole) {
        try {
            cubbyHoleService.cubbyholeWrite(uuid, token, isCubbyhole);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            return "Error: " + t.getMessage();
        }

        return "OK";
    }
}
