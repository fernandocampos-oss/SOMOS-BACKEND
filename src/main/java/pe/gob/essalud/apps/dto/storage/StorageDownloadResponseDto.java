package pe.gob.essalud.apps.dto.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StorageDownloadResponseDto {

    @JsonProperty("filePath")
    private String filePath;

    @JsonProperty("fileBuffer")
    private FileBuffer fileBuffer;

    @JsonProperty("contentType")
    private String contentType;

    @Data
    public static class FileBuffer {

        @JsonProperty("type")
        private String type;

        // Array de enteros del Buffer de Node.js (valores 0-255)
        @JsonProperty("data")
        private List<Integer> data;
    }
}
