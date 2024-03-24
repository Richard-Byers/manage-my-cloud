package org.mmc.pojo;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferences {

    boolean deleteVideos;
    boolean deleteImages;
    boolean deleteDocuments;
    boolean deleteEmails;
    private int deleteItemsCreatedAfterDays;
    private int deleteItemsNotChangedSinceDays;
    private int deleteEmailsAfterDays;
}
