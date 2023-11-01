package com.samim.SpringVaultPrac.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Secrets {
    private String key;
    private String value;
}
