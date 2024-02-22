package com.authorisation.entities;

import com.authorisation.pojo.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LinkedAccounts {
    private int linkedAccountsCount = 0;

    @ElementCollection
    @CollectionTable(name = "linked_drive_accounts", joinColumns = @JoinColumn(name = "linked_accounts_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "accountEmail", column = @Column(name = "account_email")),
            @AttributeOverride(name = "accountType", column = @Column(name = "account_type"))
    })
    List<Account> linkedDriveAccounts;
}
