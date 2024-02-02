package com.authorisation.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LinkedAccounts {
    private int linkedAccountsCount = 0;
    private boolean oneDrive;
    private boolean googleDrive;
}
