package org.mmc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DriveInformationReponse {

    // This can be the name of the drive or the name of the user
    private String displayName;

    // This is the email linked to the drive
    private String email;

    // This is the plan type of the drive e.g. personal.....
    private String driveType;

    // This is the total storage available on the drive
    private Double total;

    // This is the amount of storage used on the drive
    private Double used;

}
