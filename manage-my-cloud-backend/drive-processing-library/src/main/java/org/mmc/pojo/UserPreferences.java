package org.mmc.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserPreferences {

    boolean deleteVideos;
    boolean deleteImages;
    boolean deleteDocuments;
    boolean deleteEmails;
    private int deleteItemsCreatedAfterDays;
    private int deleteItemsNotChangedSinceDays;

}
