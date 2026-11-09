package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Perpustakaan perpustakaan = new Perpustakaan();
        Path lokasi = Paths.get("data", "buku.txt");

        System.out.println("=================================================");
        System.out.println(" PRAKTIKUM P12 - PERSISTENSI FILE TEKS          ");
        System.out.println(" Mahasiswa : M. Davana Yudista Firdausy          ");
        System.out.println(" NRP       : 3125522002 | PENS PSDKU Sumenep    ");
        System.out.println("=================================================");

        System.out.println("\n[Langkah 1] Menambah data ke koleksi memori:");
        perpustakaan.tambahBuku(new Buku("B001", "Dasar Java"));
        perpustakaan.tambahBuku(new Buku("B002", "Konsep OOP"));
        perpustakaan.tampilkanBuku();

        System.out.println("\n[Langkah 2] Menyimpan koleksi ke berkas: " + lokasi);
        try {
            perpustakaan.simpan(lokasi);
            System.out.println("Data berhasil disimpan ke file.");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan: " + e.getMessage());
        }

        System.out.println("\n[Langkah 3] Mengosongkan koleksi di memori (Simulasi Restart):");
        Perpustakaan perpustakaanBaru = new Perpustakaan();
        perpustakaanBaru.tampilkanBuku();

        System.out.println("\n[Langkah 4] Memuat kembali data dari berkas:");
        try {
            perpustakaanBaru.muat(lokasi);
            System.out.println("Data berhasil dimuat dari file.");
        } catch (IOException e) {
            System.out.println("Gagal memuat: " + e.getMessage());
        }

        System.out.println("\n[Langkah 5] Menampilkan isi koleksi setelah pemulihan berkas:");
        perpustakaanBaru.tampilkanBuku();
    }
}
