package gov.samhsa.c2s.trypolicy.service.dto;

import java.nio.charset.StandardCharsets;

public class CCDDto {

    private byte[] ccdFile;

    public CCDDto() {
    }

    public CCDDto(byte[] ccdFile) {
        this.ccdFile = ccdFile;
    }

    public byte[] getCCDFile() {
        return ccdFile;
    }

    public void setCCDFile(byte[] ccdFile) {
        this.ccdFile = ccdFile;
    }

    @Override
    public String toString() {
        return this.ccdFile == null ? "" : new String(this.getCCDFile(), StandardCharsets.UTF_8);
    }
}