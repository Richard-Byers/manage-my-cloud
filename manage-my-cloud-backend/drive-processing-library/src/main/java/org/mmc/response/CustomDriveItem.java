package org.mmc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomDriveItem {
    private String id;
    private String name;
    private String type;
    private OffsetDateTime createdDateTime;
    private OffsetDateTime lastModifiedDateTime;
    private String webUrl;
    private List<CustomDriveItem> children;
}
