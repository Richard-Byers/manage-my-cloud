package com.authorisation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private UserEntity userEntity;

    private boolean deleteVideos = true;

    private boolean deleteImages = true;

    private boolean deleteDocuments = true;

    private boolean deleteEmails = true;

    private int deleteItemsCreatedAfterDays = 7;

    private int deleteItemsNotChangedSinceDays = 7;

    private int deleteEmailsAfterDays = 7;
}
