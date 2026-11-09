package SourceCode.model;

public class Buku {
    private final String kode;
    private final String judul;

    public Buku(String kode, String judul) {
        if (kode == null || kode.trim().isEmpty() || judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
        }
        this.kode = kode.trim();
        this.judul = judul.trim();
    }

    public String getKode() {
        return kode;
    }

    public String getJudul() {
        return judul;
    }

    @Override
    public String toString() {
        return kode + " - " + judul;
    }
}
