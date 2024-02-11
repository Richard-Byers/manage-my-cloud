import PDF from "../components/images/dashboard/dashboard-modal-files/pdf-101.svg";
import DOCX from "../components/images/dashboard/dashboard-modal-files/docx-10.svg";
import PPTX from "../components/images/dashboard/dashboard-modal-files/pptx-5.svg";
import JPEG from "../components/images/dashboard/dashboard-modal-files/jpeg-17.svg";
import PNG from "../components/images/dashboard/dashboard-modal-files/png-55.svg";
import EXCEL from "../components/images/dashboard/dashboard-modal-files/excel-74.svg";
import MP4 from "../components/images/dashboard/dashboard-modal-files/mp4-24.svg";
import TXT from "../components/images/dashboard/dashboard-modal-files/txt-59.svg";
import GIF from "../components/images/dashboard/dashboard-modal-files/gif-51.svg";
import CSV from "../components/images/dashboard/dashboard-modal-files/csv-13.svg";
import OTHER from "../components/images/dashboard/dashboard-modal-files/other-71.svg";


interface FilesTypeProp {
    [key: string]: string;
}

export const FILES_TYPES: FilesTypeProp = {
    "pdf": PDF,
    "docx": DOCX,
    "pptx": PPTX,
    "jpeg": JPEG,
    "png": PNG,
    "xlsx": EXCEL,
    "mp4": MP4,
    "txt": TXT,
    "gif": GIF,
    "csv": CSV,
    "other": OTHER
}

export function getFileType(key: string): string {
    return FILES_TYPES[key] || FILES_TYPES["other"];
}