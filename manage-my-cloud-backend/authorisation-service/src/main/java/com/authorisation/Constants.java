package com.authorisation;

public class Constants {

    //GOOGLE COMMON CONSTANTS
    public static final String GOOGLE_CREDENTIALS_FILE_PATH = "/credentials.json";

    public static final String GOOGLEDRIVE = "GoogleDrive";

    //ONEDRIVE COMMON CONSTANTS
    public static final String ONEDRIVE = "OneDrive";

    // ONEDRIVE SECRETS ENVIRONMENT VARIABLES
    public static final String MS_AUTH_CODE_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    public static final String ONEDRIVE_CLIENT_ID = "ONEDRIVE_CLIENT_ID";
    public static final String ONEDRIVE_REDIRECT_URI = "ONEDRIVE_REDIRECT_URI";
    public static final String ONEDRIVE_CLIENT_SECRET = "ONEDRIVE_CLIENT_SECRET";
    public static final String ONEDRIVE_GRANT_TYPE = "authorization_code";
    public static final String ONEDRIVE_REFRESH_GRANT_TYPE = "refresh_token";

    public  static final long EXPIRATION_THRESHOLD_MILLISECONDS = 1000 * 60 * 5;
    public  static final long EXPIRATION_THRESHOLD_MINUTES = 5;



}