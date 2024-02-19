package org.mmc.enumerations;

import java.util.HashSet;
import java.util.Set;

public class ItemTypeChecker {

    private ItemTypeChecker() {
    }

    public static class ImageType {
        private ImageType() {
        }

        private static final Set<String> IMAGE_TYPES = new HashSet<>();

        static {
            IMAGE_TYPES.add("PNG");
            IMAGE_TYPES.add("JPEG");
            IMAGE_TYPES.add("JPG");
            IMAGE_TYPES.add("GIF");
            IMAGE_TYPES.add("BMP");
            IMAGE_TYPES.add("TIFF");
            IMAGE_TYPES.add("SVG");
            IMAGE_TYPES.add("WEBP");
        }

        public static boolean isImageType(String itemName) {
            int lastIndexOfDot = itemName.lastIndexOf(".");
            if (lastIndexOfDot == -1) {
                return false;
            }
            String itemFileExtension = itemName.substring(lastIndexOfDot + 1).toUpperCase();
            return IMAGE_TYPES.contains(itemFileExtension);
        }
    }

    public static class DocumentType {
        private DocumentType() {
        }

        private static final Set<String> DOCUMENT_TYPES = new HashSet<>();

        static {
            DOCUMENT_TYPES.add("PDF");
            DOCUMENT_TYPES.add("DOC");
            DOCUMENT_TYPES.add("DOCX");
            DOCUMENT_TYPES.add("TXT");
            DOCUMENT_TYPES.add("RTF");
            DOCUMENT_TYPES.add("XLS");
            DOCUMENT_TYPES.add("XLSX");
            DOCUMENT_TYPES.add("PPT");
            DOCUMENT_TYPES.add("PPTX");
            DOCUMENT_TYPES.add("CSV");
        }

        public static boolean isDocumentType(String itemName) {
            int lastIndexOfDot = itemName.lastIndexOf(".");
            if (lastIndexOfDot == -1) {
                return false;
            }
            String documentFileExtension = itemName.substring(lastIndexOfDot + 1).toUpperCase();
            return DOCUMENT_TYPES.contains(documentFileExtension);
        }
    }

    public static class VideoType {

        private VideoType() {
        }

        private static final Set<String> VIDEO_TYPES = new HashSet<>();

        static {
            VIDEO_TYPES.add("MP4");
            VIDEO_TYPES.add("MP3");
            VIDEO_TYPES.add("AVI");
            VIDEO_TYPES.add("FLV");
            VIDEO_TYPES.add("MOV");
            VIDEO_TYPES.add("WMV");
            VIDEO_TYPES.add("MKV");
            VIDEO_TYPES.add("WEBM");
        }

        public static boolean isVideoType(String itemName) {
            int lastIndexOfDot = itemName.lastIndexOf(".");
            if (lastIndexOfDot == -1) {
                return false;
            }
            String videoFileExtension = itemName.substring(lastIndexOfDot + 1).toUpperCase();
            return VIDEO_TYPES.contains(videoFileExtension);
        }
    }

    public static class OtherType {
        private OtherType() {
        }

        public static boolean isOtherType(String itemName) {
            int lastIndexOfDot = itemName.lastIndexOf(".");
            String otherFileExtension = lastIndexOfDot == -1 ? "" : itemName.substring(lastIndexOfDot + 1).toUpperCase();
            return !ImageType.IMAGE_TYPES.contains(otherFileExtension) && !DocumentType.DOCUMENT_TYPES.contains(otherFileExtension) && !VideoType.VIDEO_TYPES.contains(otherFileExtension);
        }
    }

}
