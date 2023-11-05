# SpringVaultPrac

This project has been created to help the people to check and provide a solution to the problem mentioned in the stackoverflow post: "https://stackoverflow.com/questions/77399157/unable-to-access-secrets-from-hashicorp-vault-cubbyhole-spring-boot".

A bounty of 50 reputations is also active :smiley:

Following are the Vault policies created:

### cubbyhole-policy.hcl:

>path "cubbyhole/data/*" {<br>
&nbsp;&nbsp;&nbsp;&nbsp;capabilities = ["read", "create", "update", "delete", "list"]<br>
}

### cubbyhole-policy-token.hcl:

>path "cubbyhole/token/*" {<br>
&nbsp;&nbsp;&nbsp;&nbsp;capabilities = ["read", "create", "update", "delete", "list"]<br>
}

#### Policy creation steps (in terminal/powershell/gitbash shell):

```bash
export VAULT_ADDR='http://127.0.0.1:8200'
export VAULT_TOKEN="mytoken"
echo 'path "cubbyhole/data/*" {
&nbsp;&nbsp;&nbsp;&nbsp;capabilities = ["read", "create", "update", "delete", "list"]
}' > cubbyhole-policy.hcl
vault policy write cubbyhole-policy cubbyhole-policy.hcl
```

