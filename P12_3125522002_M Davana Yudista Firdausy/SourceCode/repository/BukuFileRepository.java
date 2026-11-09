package SourceCode.repository;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.io.IOException;
import java.nio.file.Path;

public class BukuFileRepository {
    private final Path filePath;

    public BukuFileRepository(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("Lokasi file tidak boleh null.");
        }
        this.filePath = filePath;
    }

    public void simpan(Perpustakaan perpustakaan) throws IOException {
        perpustakaan.simpan(filePath);
    }

    public void muat(Perpustakaan perpustakaan) throws IOException {
        perpustakaan.muat(filePath);
    }

    public Path getFilePath() {
        return filePath;
    }
}
