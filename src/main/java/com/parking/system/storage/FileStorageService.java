package com.parking.system.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredFile storePlateImage(MultipartFile file);
}
