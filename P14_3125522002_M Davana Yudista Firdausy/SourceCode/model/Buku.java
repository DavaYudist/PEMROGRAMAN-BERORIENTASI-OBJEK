package SourceCode.model;

public class Buku {
    private final String kode;
    private final String judul;

    public Buku(String kode, String judul) {
        if (kode == null || kode.trim().isEmpty() || judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
        }

        String trimmedKode = kode.trim();
        String trimmedJudul = judul.trim();

        if (trimmedKode.contains("|") || trimmedJudul.contains("|")) {
            throw new IllegalArgumentException("Kode atau judul tidak boleh mengandung karakter pembatas '|'.");
        }

        this.kode = trimmedKode;
        this.judul = trimmedJudul;
    }

    public String getKode() {
        return kode;
    }

    public String getJudul() {
        return judul;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Buku buku = (Buku) obj;
        return kode.equalsIgnoreCase(buku.kode);
    }

    @Override
    public int hashCode() {
        return kode.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return kode + " - " + judul;
    }
}
