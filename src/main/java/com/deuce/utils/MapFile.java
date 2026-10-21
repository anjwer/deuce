package com.deuce.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.http.Part;

public class MapFile {
    private String filename;
    private String extension;
    private byte[] bytes;
    private String contentType;

    public MapFile() {}

    public MapFile(Part part) throws IOException {
        this.contentType = part.getContentType();
        this.filename = extractFileName(part);
        if (this.filename != null && this.filename.contains(".")) {
            // recup l'extension par exemple .jpeg
            this.extension = this.filename.substring(this.filename.lastIndexOf(".") + 1);
        } else {
            this.extension = "";
        }
        
        try (InputStream inputStream = part.getInputStream()) {
            this.bytes = inputStream.readAllBytes();
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String cd : contentDisp.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf("=") + 2, cd.length() - 1);
            }
        }
        return null;
    }

    /**
     * Enregistre le fichier dans le dossier défini par 'app.upload.dir' dans application.properties
     */
    public void upload() throws IOException {
        String uploadDir = AppConfig.get("app.upload.dir", "uploads");
        uploadToDir(uploadDir);
    }

    /**
     * Enregistre le fichier avec un nom spécifique (ex: UUID ou timestamp) dans le dossier de config
     */
    public void uploadAs(String customFilename) throws IOException {
        String uploadDir = AppConfig.get("app.upload.dir", "uploads");
        uploadToDir(uploadDir, customFilename);
    }

    /**
     * Méthode interne de sauvegarde
     */
    public void uploadToDir(String destinationDir, String targetFilename) throws IOException {
        File dir = new File(destinationDir);
        if (!dir.exists()) {
            dir.mkdirs(); // Crée les dossiers s'ils n'existent pas
        }

        File targetFile = new File(dir, targetFilename);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(this.bytes);
        }
    }

    public void uploadToDir(String destinationDir) throws IOException {
        uploadToDir(destinationDir, this.filename);
    }

    public String getFilename() { return filename; }
    public String getExtension() { return extension; }
    public byte[] getBytes() { return bytes; }
    public String getContentType() { return contentType; }
}