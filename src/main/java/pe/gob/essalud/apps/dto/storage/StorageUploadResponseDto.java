package pe.gob.essalud.apps.dto.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StorageUploadResponseDto {

    @JsonProperty("newFilename")
    private String newFilename;

    @JsonProperty("pathFileName")
    private String pathFileName;

    @JsonProperty("kbSize")
    private String kbSize;
}
