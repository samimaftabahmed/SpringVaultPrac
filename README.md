# SpringVaultPrac

Following are the Vault policies created:

### cubbyhole-policy.hcl:

>path "cubbyhole/data/*" {<br>
&nbsp;&nbsp;&nbsp;&nbsp;capabilities = ["read", "create", "update", "delete", "list"]<br>
}

### cubbyhole-policy-token.hcl:

>path "cubbyhole/token/*" {<br>
&nbsp;&nbsp;&nbsp;capabilities = ["read", "create", "update", "delete", "list"]<br>
}
