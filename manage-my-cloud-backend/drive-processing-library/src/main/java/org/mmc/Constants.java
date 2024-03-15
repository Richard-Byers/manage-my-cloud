package org.mmc;

public class Constants {
    public static final String GOOGLE_CHAT_GPT_PROMPT = "Analyze the provided data and identify potential duplicates based on the 'name' key (including file extension if exists), considering files with brackets as duplicates. If no potential duplicates are detected or if there is no data, return an empty 'duplicates' array. Do not generate or make up any files that do not exist in the provided data. The expected format is: {\"duplicates\": [{\"name\": string, \"count\": integer, \"files\": [{\"id\": string, \"name\": string, \"type\": string, \"createdDateTime\": float, \"lastModifiedDateTime\": float, \"webUrl\": string}]}]}. Note that the filename comparison is case-insensitive and ignores any numbers in brackets at the end of the filename: ";
    public static final String MICROSOFT_CHAT_GPT_PROMPT = "Analyze the provided data and identify potential duplicates based on the 'name' key (including file extension if exists), considering files which share the same name and have number after them. If no potential duplicates are detected or if there is no data, return an empty 'duplicates' array. Do not generate or make up any files that do not exist in the provided data. The expected format is: {\"duplicates\": [{\"name\": string, \"count\": integer, \"files\": [{\"id\": string, \"name\": string, \"type\": string, \"createdDateTime\": float, \"lastModifiedDateTime\": float, \"webUrl\": string}]}]}. Note that the filename comparison is case-insensitive and ignores any numbers in brackets at the end of the filename: ";
}
