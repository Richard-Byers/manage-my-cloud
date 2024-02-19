package com.authorisation.givens;

import org.mmc.response.FilesDeletedResponse;

public class FilesDeleteGivens {

    public static FilesDeletedResponse generateFilesDeletedResponse() {
        FilesDeletedResponse filesDeletedResponse = new FilesDeletedResponse();
        filesDeletedResponse.setFilesDeleted(1);
        return filesDeletedResponse;
    }

}
