package com.parking.system.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootPath;

    public LocalFileStorageService(@Value("${app.storage.root:work/uploads}") String rootPath) {
        this.rootPath = Paths.get(rootPath).toAbsolutePath().normalize();
    }

    @Override
    public StoredFile storePlateImage(MultipartFile file) {
        try {
            Files.createDirectories(rootPath);
            Path folder = rootPath.resolve("plate-recognition").resolve(LocalDate.now().toString());
            Files.createDirectories(folder);

            String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename());
            String storedName = UUID.randomUUID() + "_" + originalName;
            Path target = folder.resolve(storedName).normalize();
            Files.copy(file.getInputStream(), target);

            return StoredFile.builder()
                    .storagePath(target.toString())
                    .publicUrl("/uploads/plate-recognition/" + LocalDate.now() + "/" + storedName)
                    .originalFileName(originalName)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store plate image", ex);
        }
    }
}
