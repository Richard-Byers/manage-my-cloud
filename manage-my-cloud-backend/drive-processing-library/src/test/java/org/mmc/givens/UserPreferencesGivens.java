package org.mmc.givens;

import org.mmc.pojo.UserPreferences;

public class UserPreferencesGivens {

    public static UserPreferences generateUserPreferencesAllTrue0Days(){
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setDeleteDocuments(true);
        userPreferences.setDeleteEmails(true);
        userPreferences.setDeleteImages(true);
        userPreferences.setDeleteVideos(true);
        userPreferences.setDeleteItemsCreatedAfterDays(0);
        userPreferences.setDeleteItemsNotChangedSinceDays(0);

        return userPreferences;
    }

    public static UserPreferences generateUserPreferencesOnlyImage0Days(){
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setDeleteDocuments(false);
        userPreferences.setDeleteEmails(false);
        userPreferences.setDeleteImages(true);
        userPreferences.setDeleteVideos(false);
        userPreferences.setDeleteItemsCreatedAfterDays(0);
        userPreferences.setDeleteItemsNotChangedSinceDays(0);

        return userPreferences;
    }

    public static UserPreferences generateUserPreferencesOnlyDocument0Days(){
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setDeleteDocuments(true);
        userPreferences.setDeleteEmails(false);
        userPreferences.setDeleteImages(false);
        userPreferences.setDeleteVideos(false);
        userPreferences.setDeleteItemsCreatedAfterDays(0);
        userPreferences.setDeleteItemsNotChangedSinceDays(0);

        return userPreferences;
    }

    public static UserPreferences generateUserPreferencesOnlyVideo0Days(){
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setDeleteDocuments(false);
        userPreferences.setDeleteEmails(false);
        userPreferences.setDeleteImages(false);
        userPreferences.setDeleteVideos(true);
        userPreferences.setDeleteItemsCreatedAfterDays(0);
        userPreferences.setDeleteItemsNotChangedSinceDays(0);

        return userPreferences;
    }

    public static UserPreferences generateUserPreferencesOnlyUnknownFileTypes0Days(){
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setDeleteDocuments(false);
        userPreferences.setDeleteEmails(false);
        userPreferences.setDeleteImages(false);
        userPreferences.setDeleteVideos(false);
        userPreferences.setDeleteItemsCreatedAfterDays(0);
        userPreferences.setDeleteItemsNotChangedSinceDays(0);

        return userPreferences;
    }

    public static UserPreferences generateUserPreferencesOnlyUnknownFileTypes7Days(){
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setDeleteDocuments(false);
        userPreferences.setDeleteEmails(false);
        userPreferences.setDeleteImages(false);
        userPreferences.setDeleteVideos(false);
        userPreferences.setDeleteItemsCreatedAfterDays(7);
        userPreferences.setDeleteItemsNotChangedSinceDays(7);

        return userPreferences;
    }

}
