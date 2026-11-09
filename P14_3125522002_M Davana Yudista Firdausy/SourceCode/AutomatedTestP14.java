package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class AutomatedTestP14 {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" SUITE REGRESI DAN VERIFIKASI AKHIR MODUL 14 - FINALISASI PROYEK & REFACTORING");
        System.out.println(" Mahasiswa  : M. Davana Yudista Firdausy (NRP: 3125522002)");
        System.out.println(" Kelas      : A D3 TI-SM | PENS PSDKU Sumenep");
        System.out.println("================================================================================");

        int passCount = 0;
        int totalTest = 10;
        Path dataDir = Paths.get("data");

        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException ignored) {}

        // Skenario 1: Tampilkan collection kosong
        System.out.println("\n[SKENARIO 1] Tampilkan collection kosong");
        Perpustakaan p1 = new Perpustakaan();
        try {
            p1.tampilkanBuku();
            if (p1.getJumlahBuku() == 0) {
                System.out.println("-> HASIL: [PASS] Pesan koleksi kosong tercetak; aplikasi tetap berjalan aman.");
                passCount++;
            } else {
                System.out.println("-> HASIL: [FAIL] State koleksi tidak kosong.");
            }
        } catch (Exception e) {
            System.out.println("-> HASIL: [FAIL] Exception tak terduga: " + e.getMessage());
        }

        // Skenario 2: Tambah kode baru dengan judul valid
        System.out.println("\n[SKENARIO 2] Tambah kode baru dengan judul valid");
        try {
            p1.tambahBuku(new Buku("B001", "Dasar Pemrograman Java"));
            if (p1.getJumlahBuku() == 1 && p1.cariBuku("B001") != null) {
                System.out.println("-> HASIL: [PASS] Data bertambah satu (Total: 1 buku).");
                passCount++;
            } else {
                System.out.println("-> HASIL: [FAIL] Data gagal bertambah.");
            }
        } catch (Exception e) {
            System.out.println("-> HASIL: [FAIL] Error: " + e.getMessage());
        }

        // Skenario 3: Tambah kode yang sudah terdaftar
        System.out.println("\n[SKENARIO 3] Tambah kode yang sudah terdaftar");
        try {
            p1.tambahBuku(new Buku("B001", "Buku Palsu Duplikat"));
            System.out.println("-> HASIL: [FAIL] Kode duplikat lolos.");
        } catch (IllegalArgumentException e) {
            if (p1.getJumlahBuku() == 1) {
                System.out.println("-> HASIL: [PASS] Ditolak via IllegalArgumentException; data lama tidak berubah.");
                passCount++;
            } else {
                System.out.println("-> HASIL: [FAIL] Data lama termutasi.");
            }
        }

        // Skenario 4: Tambah kode atau judul berisi spasi saja
        System.out.println("\n[SKENARIO 4] Tambah kode atau judul berisi spasi saja");
        try {
            new Buku("   ", "   ");
            System.out.println("-> HASIL: [FAIL] Spasi kosong lolos dibuat.");
        } catch (IllegalArgumentException e) {
            System.out.println("-> HASIL: [PASS] Ditolak pada konstruktor Buku (Validasi Invarian Berhasil).");
            passCount++;
        }

        // Skenario 5: Tambah judul mengandung delimiter '|'
        System.out.println("\n[SKENARIO 5] Tambah judul mengandung karakter '|'");
        try {
            new Buku("B003", "Struktur Data | Algoritma");
            System.out.println("-> HASIL: [FAIL] Karakter '|' diizinkan masuk.");
        } catch (IllegalArgumentException e) {
            System.out.println("-> HASIL: [PASS] Ditolak sebelum masuk collection (Proteksi Delimiter Berhasil).");
            passCount++;
        }

        // Skenario 6: Cari kode dengan huruf kecil, misalnya b001
        System.out.println("\n[SKENARIO 6] Cari kode dengan huruf kecil ('b001')");
        Buku hasilCari = p1.cariBuku("b001");
        if (hasilCari != null && hasilCari.getKode().equals("B001")) {
            System.out.println("-> HASIL: [PASS] Buku ditemukan: '" + hasilCari.getJudul() + "' secara case-insensitive.");
            passCount++;
        } else {
            System.out.println("-> HASIL: [FAIL] Buku tidak ditemukan.");
        }

        // Skenario 7: Hapus kode yang terdaftar
        System.out.println("\n[SKENARIO 7] Hapus kode yang terdaftar ('B001')");
        boolean terhapus = p1.hapusBuku("B001");
        if (terhapus && p1.getJumlahBuku() == 0) {
            System.out.println("-> HASIL: [PASS] Data berkurang satu (Total: 0 buku).");
            passCount++;
        } else {
            System.out.println("-> HASIL: [FAIL] Gagal menghapus buku terdaftar.");
        }

        // Skenario 8: Hapus kode yang tidak terdaftar
        System.out.println("\n[SKENARIO 8] Hapus kode yang tidak terdaftar ('B999')");
        boolean hapusFiktif = p1.hapusBuku("B999");
        if (!hapusFiktif && p1.getJumlahBuku() == 0) {
            System.out.println("-> HASIL: [PASS] Pesan tidak ditemukan (return false); isi collection tidak berubah.");
            passCount++;
        } else {
            System.out.println("-> HASIL: [FAIL] Mengembalikan true pada data fiktif.");
        }

        // Skenario 9: Muat file yang baris keduanya rusak
        System.out.println("\n[SKENARIO 9] Muat file yang baris keduanya rusak");
        Path fileBarisRusak = dataDir.resolve("test_baris_rusak.txt");
        try {
            Files.write(fileBarisRusak, Arrays.asList("B001|Java Fundamental", "B002BarisCacatTanpaPipe"), StandardCharsets.UTF_8);
            Perpustakaan pUji9 = new Perpustakaan();
            pUji9.tambahBuku(new Buku("B_EXIST", "Buku Sebelum Pemuatan"));
            try {
                pUji9.muat(fileBarisRusak);
                System.out.println("-> HASIL: [FAIL] File baris rusak lolos dimuat.");
            } catch (IllegalArgumentException e) {
                if (pUji9.getJumlahBuku() == 1 && pUji9.cariBuku("B_EXIST") != null) {
                    System.out.println("-> HASIL: [PASS] Ditolak via IllegalArgumentException; isi collection sebelum muat TIDAK BERUBAH.");
                    passCount++;
                } else {
                    System.out.println("-> HASIL: [FAIL] Collection termutasi sebagian.");
                }
            }
        } catch (Exception e) {
            System.out.println("-> HASIL: [FAIL] Exception test: " + e.getMessage());
        }

        // Skenario 10: Simpan, tutup program, jalankan kembali, lalu muat
        System.out.println("\n[SKENARIO 10] Simpan, tutup program (simulasi), jalankan kembali, lalu muat");
        Path filePersisten = dataDir.resolve("test_siklus_penuh.txt");
        try {
            Perpustakaan sesiSatu = new Perpustakaan();
            sesiSatu.tambahBuku(new Buku("B101", "Arsitektur Enterprise"));
            sesiSatu.tambahBuku(new Buku("B102", "DevOps Engineering"));
            sesiSatu.simpan(filePersisten);

            // Simulasi program ditutup dan dibuka kembali dengan instance baru
            Perpustakaan sesiDua = new Perpustakaan();
            sesiDua.muat(filePersisten);

            if (sesiDua.getJumlahBuku() == 2 &&
                sesiDua.cariBuku("B101") != null &&
                sesiDua.cariBuku("B102") != null) {
                System.out.println("-> HASIL: [PASS] Seluruh data yang disimpan tampil kembali dengan benar dan identik 100%.");
                passCount++;
            } else {
                System.out.println("-> HASIL: [FAIL] Data hasil pemulihan tidak sesuai.");
            }
        } catch (Exception e) {
            System.out.println("-> HASIL: [FAIL] Terjadi kegagalan persistensi: " + e.getMessage());
        }

        System.out.println("\n================================================================================");
        System.out.println(" RINGKASAN HASIL REGRESI P14: " + passCount + "/" + totalTest + " SKENARIO LULUS (" + (passCount * 100 / totalTest) + "%)");
        System.out.println("================================================================================");
    }
}
