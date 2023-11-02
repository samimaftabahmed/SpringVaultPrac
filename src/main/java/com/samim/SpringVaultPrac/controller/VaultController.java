package com.samim.SpringVaultPrac.controller;

import com.samim.SpringVaultPrac.service.CubbyHoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class VaultController {

    @Autowired
    private CubbyHoleService cubbyHoleService;

    @GetMapping("/run-cubby/{token}")
    public void runCubby(@PathVariable String token) {
        System.out.println("\nToken: " + token);
        System.out.println("\n");

        String uuid = UUID.randomUUID().toString();
        cubbyHoleService.cubbyHoleWrite(uuid, token);
        cubbyHoleService.cubbyHoleRead(uuid, token);
    }

}
