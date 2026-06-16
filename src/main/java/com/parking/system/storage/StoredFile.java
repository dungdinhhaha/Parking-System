package com.parking.system.storage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoredFile {
    private final String publicUrl;
    private final String originalFileName;
}
