package com.parking.system.storage;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredFile storePlateImage(MultipartFile file) throws IOException;
}
