package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestP12Runner {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" SUITE PENGUJIAN OTOMATIS MODUL 12 - PENYIMPANAN & PEMBACAAN DATA FILE");
        System.out.println(" Mahasiswa  : M. Davana Yudista Firdausy (NRP: 3125522002)");
        System.out.println(" Kelas      : A D3 TI-SM | PENS PSDKU Sumenep");
        System.out.println("================================================================================");

        int passCount = 0;
        int totalTest = 10;
        Path tempDir = Paths.get("data");

        try {
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }
        } catch (IOException ignored) {}

        // Test 1: Simpan koleksi valid ke file
        System.out.println("\n[UJI-01] Menyimpan koleksi objek Buku ke berkas teks terstruktur");
        Path fileValid = tempDir.resolve("test_buku_valid.txt");
        Perpustakaan p1 = new Perpustakaan();
        p1.tambahBuku(new Buku("B001", "Dasar Java"));
        p1.tambahBuku(new Buku("B002", "Konsep OOP Modern"));
        try {
            p1.simpan(fileValid);
            if (Files.exists(fileValid) && Files.size(fileValid) > 0) {
                System.out.println("-> STATUS: [PASS] File berhasil ditulis dengan format pipe-delimited.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] File tidak terbentuk atau berukuran 0 byte.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Terjadi error: " + e.getMessage());
        }

        // Test 2: Memuat file ke koleksi baru yang kosong
        System.out.println("\n[UJI-02] Membaca file valid dan merekonstruksi objek ke dalam memori");
        Perpustakaan p2 = new Perpustakaan();
        try {
            p2.muat(fileValid);
            if (p2.getJumlahBuku() == 2 && p2.cariBuku("B001") != null && p2.cariBuku("B002") != null) {
                System.out.println("-> STATUS: [PASS] Seluruh objek berhasil direkonstruksi tanpa kehilangan data.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Data hasil pemuatan tidak lengkap.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error saat memuat file: " + e.getMessage());
        }

        // Test 3: Verifikasi kesesuaian atribut setelah round-trip persistence
        System.out.println("\n[UJI-03] Verifikasi kesetaraan atribut objek sebelum dan sesudah persistensi");
        Buku b1 = p2.cariBuku("B001");
        if (b1 != null && b1.getJudul().equals("Dasar Java")) {
            System.out.println("-> STATUS: [PASS] Atribut kode dan judul identik dengan data asli.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Terjadi korupsi nilai atribut.");
        }

        // Test 4: Tolak penyimpanan karakter delimiter '|'
        System.out.println("\n[UJI-04] Menolak penyimpanan data jika mengandung karakter delimiter '|'");
        Perpustakaan pPipe = new Perpustakaan();
        pPipe.tambahBuku(new Buku("B003", "Java | Lanjutan"));
        try {
            pPipe.simpan(tempDir.resolve("test_pipe_fail.txt"));
            System.out.println("-> STATUS: [FAIL] Karakter '|' lolos dan disimpan ke berkas.");
        } catch (IllegalArgumentException e) {
            System.out.println("-> STATUS: [PASS] Berhasil mendeteksi delimiter ilegal: '" + e.getMessage() + "'.");
            passCount++;
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Melempar tipe exception yang salah: " + e);
        }

        // Test 5: Penanganan file yang tidak ditemukan
        System.out.println("\n[UJI-05] Menangani berkas yang tidak ada di storage (NoSuchFileException)");
        Perpustakaan pMissing = new Perpustakaan();
        try {
            pMissing.muat(tempDir.resolve("berkas_ghaib_999.txt"));
            System.out.println("-> STATUS: [FAIL] Memuat file yang tidak ada tanpa exception.");
        } catch (IOException e) {
            System.out.println("-> STATUS: [PASS] Terlempar IOException terdefinisi secara tepat.");
            passCount++;
        }

        // Test 6: Muat berkas dengan format baris rusak (Malformed format)
        System.out.println("\n[UJI-06] Menolak pemuatan berkas yang memiliki struktur kolom tidak lengkap");
        Path fileCorruptFormat = tempDir.resolve("test_corrupt_format.txt");
        try {
            Files.write(fileCorruptFormat, Arrays.asList("B001|Java Dasar", "B002FormatRusakTanpaPipe"), StandardCharsets.UTF_8);
            Perpustakaan pCorrupt = new Perpustakaan();
            pCorrupt.tambahBuku(new Buku("BEXIST", "Buku Aman"));
            try {
                pCorrupt.muat(fileCorruptFormat);
                System.out.println("-> STATUS: [FAIL] Berkas rusak berhasil dimuat.");
            } catch (IllegalArgumentException e) {
                // Verifikasi bahwa koleksi awal pCorrupt TIDAK terhapus / rusak!
                if (pCorrupt.getJumlahBuku() == 1 && pCorrupt.cariBuku("BEXIST") != null) {
                    System.out.println("-> STATUS: [PASS] Ditolak via IllegalArgumentException. State koleksi asli TERLINDUNGI.");
                    passCount++;
                } else {
                    System.out.println("-> STATUS: [FAIL] State koleksi terhapus sebelum berkas selesai divalidasi.");
                }
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Terjadi kegagalan persiapan test: " + e.getMessage());
        }

        // Test 7: Muat berkas dengan data duplikat di dalamnya
        System.out.println("\n[UJI-07] Menolak pemuatan berkas yang memuat kode buku duplikat");
        Path fileDup = tempDir.resolve("test_duplicate_in_file.txt");
        try {
            Files.write(fileDup, Arrays.asList("B001|Judul Pertama", "B001|Judul Kedua Duplikat"), StandardCharsets.UTF_8);
            Perpustakaan pDup = new Perpustakaan();
            pDup.tambahBuku(new Buku("B_ORIGINAL", "Koleksi Eksis"));
            try {
                pDup.muat(fileDup);
                System.out.println("-> STATUS: [FAIL] Berkas berisi duplikat lolos dimuat.");
            } catch (IllegalArgumentException e) {
                if (pDup.getJumlahBuku() == 1 && pDup.cariBuku("B_ORIGINAL") != null) {
                    System.out.println("-> STATUS: [PASS] Kode duplikat ditolak, koleksi asli tetap utuh 100%.");
                    passCount++;
                } else {
                    System.out.println("-> STATUS: [FAIL] Data asli rusak akibat parsing gagal.");
                }
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error test duplikat: " + e.getMessage());
        }

        // Test 8: Muat berkas kosong (Zero-byte file)
        System.out.println("\n[UJI-08] Memuat berkas kosong (Zero-byte file)");
        Path fileEmpty = tempDir.resolve("test_empty.txt");
        try {
            Files.write(fileEmpty, Arrays.asList(), StandardCharsets.UTF_8);
            Perpustakaan pEmpty = new Perpustakaan();
            pEmpty.muat(fileEmpty);
            if (pEmpty.getJumlahBuku() == 0) {
                System.out.println("-> STATUS: [PASS] Berkas kosong diproses secara valid tanpa error.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Koleksi tidak kosong setelah membaca file kosong.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error pada file kosong: " + e.getMessage());
        }

        // Test 9: Pengujian Atomic Buffer Loading (Koleksi sementara)
        System.out.println("\n[UJI-09] Menjamin atomisitas pemuatan (All-or-Nothing Loading)");
        Path filePartial = tempDir.resolve("test_partial_error.txt");
        try {
            Files.write(filePartial, Arrays.asList("B101|Buku Satu", "B102|Buku Dua", "   |Judul Error"), StandardCharsets.UTF_8);
            Perpustakaan pAtomic = new Perpustakaan();
            pAtomic.tambahBuku(new Buku("B_MASTER", "Data Penting"));
            try {
                pAtomic.muat(filePartial);
                System.out.println("-> STATUS: [FAIL] Operasi parsial lolos.");
            } catch (IllegalArgumentException e) {
                if (pAtomic.getJumlahBuku() == 1 && pAtomic.cariBuku("B_MASTER") != null) {
                    System.out.println("-> STATUS: [PASS] Operasi parsial dibatalkan secara atomik (All-or-Nothing).");
                    passCount++;
                } else {
                    System.out.println("-> STATUS: [FAIL] Atomisitas gagal, sebagian data masuk.");
                }
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error pengujian atomisitas: " + e.getMessage());
        }

        // Test 10: Mutasi memori tidak memengaruhi file sampai disimpan
        System.out.println("\n[UJI-10] Memverifikasi mutasi memori terisolasi dari storage fisik");
        try {
            Path fileIsolasi = tempDir.resolve("test_isolasi.txt");
            Perpustakaan pIso = new Perpustakaan();
            pIso.tambahBuku(new Buku("B901", "Buku Stabil"));
            pIso.simpan(fileIsolasi);

            long ukuranAwal = Files.size(fileIsolasi);
            // Tambahkan data baru di memori SAJA tanpa memanggil simpan
            pIso.tambahBuku(new Buku("B902", "Buku Memori Saja"));
            long ukuranSesudah = Files.size(fileIsolasi);

            if (ukuranAwal == ukuranSesudah && pIso.getJumlahBuku() == 2) {
                System.out.println("-> STATUS: [PASS] Storage fisik tidak terpengaruh sebelum method simpan() dipanggil.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Berkas terpengaruh secara tidak sinkron.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error test isolasi memori: " + e.getMessage());
        }

        System.out.println("\n================================================================================");
        System.out.println(" RINGKASAN HASIL PENGUJIAN P12: " + passCount + "/" + totalTest + " SKENARIO BERHASIL (" + (passCount * 100 / totalTest) + "%)");
        System.out.println("================================================================================");
    }
}
