package SourceCode.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Perpustakaan {
    private final List<Buku> daftarBuku = new ArrayList<>();

    public void tambahBuku(Buku buku) {
        if (buku == null) {
            throw new IllegalArgumentException("Objek buku tidak boleh null.");
        }

        if (cariBuku(buku.getKode()) != null) {
            throw new IllegalArgumentException("Kode buku sudah terdaftar.");
        }

        daftarBuku.add(buku);
    }

    public Buku cariBuku(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            return null;
        }

        String search = kode.trim();
        for (Buku buku : daftarBuku) {
            if (buku.getKode().equalsIgnoreCase(search)) {
                return buku;
            }
        }

        return null;
    }

    public boolean hapusBuku(String kode) {
        Buku buku = cariBuku(kode);
        if (buku == null) {
            return false;
        }
        return daftarBuku.remove(buku);
    }

    public void tampilkanBuku() {
        if (daftarBuku.isEmpty()) {
            System.out.println("Koleksi masih kosong.");
            return;
        }

        for (Buku buku : daftarBuku) {
            System.out.println(buku.getKode() + " - " + buku.getJudul());
        }
        System.out.println("Total buku: " + daftarBuku.size());
    }

    public List<Buku> getDaftarBuku() {
        return Collections.unmodifiableList(daftarBuku);
    }

    public int getJumlahBuku() {
        return daftarBuku.size();
    }

    public void clear() {
        daftarBuku.clear();
    }

    public void simpan(Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();

        for (Buku buku : daftarBuku) {
            baris.add(buku.getKode() + "|" + buku.getJudul());
        }

        if (lokasi.getParent() != null && !Files.exists(lokasi.getParent())) {
            Files.createDirectories(lokasi.getParent());
        }

        Files.write(lokasi, baris, StandardCharsets.UTF_8);
    }

    public void muat(Path lokasi) throws IOException {
        List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        Perpustakaan sementara = new Perpustakaan();

        for (int i = 0; i < baris.size(); i++) {
            String b = baris.get(i).trim();
            if (b.isEmpty()) {
                continue;
            }

            String[] bagian = b.split("\\|", -1);
            if (bagian.length != 2) {
                throw new IllegalArgumentException("Format data salah pada baris " + (i + 1) + ": " + b);
            }

            sementara.tambahBuku(new Buku(bagian[0], bagian[1]));
        }

        daftarBuku.clear();
        daftarBuku.addAll(sementara.daftarBuku);
    }
}
