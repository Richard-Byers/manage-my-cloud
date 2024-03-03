package org.mmc.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomEmail {

    private String id;
    private String webUrl;
    private OffsetDateTime receivedDate;
    private String emailSubject;

}
