package org.mmc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilesDeletedResponse {
    private int filesDeleted;
    private int emailsDeleted;
}
