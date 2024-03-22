package org.mmc.response;

import lombok.*;
import org.mmc.pojo.CustomEmail;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomDriveItem {
    private String id;
    private String name;
    private String type;
    private OffsetDateTime createdDateTime;
    private OffsetDateTime lastModifiedDateTime;
    private String webUrl;
    private List<CustomDriveItem> children;
    private List<CustomEmail> emails;
    private boolean gaveGmailPermissions;
}
