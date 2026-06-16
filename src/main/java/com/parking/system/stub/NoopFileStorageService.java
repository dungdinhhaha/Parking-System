package com.parking.system.stub;

import com.parking.system.storage.FileStorageService;
import com.parking.system.storage.StoredFile;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NoopFileStorageService implements FileStorageService {

    @Override
    public StoredFile storePlateImage(MultipartFile file) throws IOException {
        return StoredFile.builder()
                .publicUrl("noop://plate-image")
                .originalFileName(file == null ? null : file.getOriginalFilename())
                .build();
    }
}
