package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.util.List;

public class TestP11Runner {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" SUITE PENGUJIAN OTOMATIS MODUL 11 - COLLECTION & PENGELOLAAN KUMPULAN OBJEK");
        System.out.println(" Mahasiswa  : M. Davana Yudista Firdausy (NRP: 3125522002)");
        System.out.println(" Kelas      : A D3 TI-SM | PENS PSDKU Sumenep");
        System.out.println("================================================================================");

        Perpustakaan repo = new Perpustakaan();
        int passCount = 0;
        int totalTest = 10;

        // Test 1: Tampilkan koleksi kosong
        System.out.println("\n[UJI-01] Menampilkan koleksi dalam kondisi kosong");
        try {
            if (repo.getJumlahBuku() == 0) {
                repo.tampilkanBuku();
                System.out.println("-> STATUS: [PASS] Koleksi kosong terdeteksi aman tanpa NullPointerException.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Koleksi tidak kosong saat inisialisasi.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Terjadi error: " + e.getMessage());
        }

        // Test 2: Tambah buku valid pertama
        System.out.println("\n[UJI-02] Menambahkan buku baru dengan atribut valid (B001 - Pemrograman Java)");
        try {
            repo.tambahBuku(new Buku("B001", "Pemrograman Java Modern"));
            if (repo.getJumlahBuku() == 1 && repo.cariBuku("B001") != null) {
                System.out.println("-> STATUS: [PASS] Buku berhasil ditambahkan ke dalam ArrayList.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Buku gagal masuk ke koleksi.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Exception tak terduga: " + e.getMessage());
        }

        // Test 3: Tambah buku valid kedua
        System.out.println("\n[UJI-03] Menambahkan buku valid kedua (B002 - Desain Pola OOP)");
        try {
            repo.tambahBuku(new Buku("B002", "Desain Pola OOP"));
            if (repo.getJumlahBuku() == 2) {
                System.out.println("-> STATUS: [PASS] Elemen kedua berhasil masuk, ukuran koleksi bertambah dinamis.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Ukuran koleksi salah.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Exception tak terduga: " + e.getMessage());
        }

        // Test 4: Tolak kode duplikat
        System.out.println("\n[UJI-04] Menolak penambahan buku dengan kode duplikat ('B001')");
        try {
            repo.tambahBuku(new Buku("B001", "Buku Palsu Duplikat"));
            System.out.println("-> STATUS: [FAIL] Buku duplikat berhasil masuk (seharusnya ditolak).");
        } catch (IllegalArgumentException e) {
            if (repo.getJumlahBuku() == 2) {
                System.out.println("-> STATUS: [PASS] Berhasil menangkap IllegalArgumentException: '" + e.getMessage() + "'.");
                System.out.println("   Integritas koleksi terjaga (jumlah elemen tetap 2).");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Koleksi termutasi saat operasi gagal.");
            }
        }

        // Test 5: Tolak kode atau judul kosong / spasi
        System.out.println("\n[UJI-05] Menolak pembuatan objek Buku dengan kode atau judul kosong/spasi");
        try {
            new Buku("   ", "Judul Sembarang");
            System.out.println("-> STATUS: [FAIL] Objek dengan kode spasi berhasil dibuat.");
        } catch (IllegalArgumentException e) {
            System.out.println("-> STATUS: [PASS] Berhasil memvalidasi kode kosong: '" + e.getMessage() + "'.");
            passCount++;
        }

        // Test 6: Pencarian case-insensitive
        System.out.println("\n[UJI-06] Pencarian buku dengan variasi huruf kecil ('b002')");
        Buku b = repo.cariBuku("b002");
        if (b != null && b.getKode().equals("B002")) {
            System.out.println("-> STATUS: [PASS] Buku berhasil ditemukan: '" + b.getJudul() + "' secara case-insensitive.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Pencarian case-insensitive gagal.");
        }

        // Test 7: Pencarian kode yang tidak terdaftar
        System.out.println("\n[UJI-07] Pencarian buku dengan kode tidak terdaftar ('B999')");
        Buku bNull = repo.cariBuku("B999");
        if (bNull == null) {
            System.out.println("-> STATUS: [PASS] Mengembalikan null secara elegan tanpa melempar exception liar.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Menghasilkan objek tidak valid.");
        }

        // Test 8: Hapus buku yang terdaftar
        System.out.println("\n[UJI-08] Menghapus buku terdaftar ('B001')");
        boolean terhapus = repo.hapusBuku("B001");
        if (terhapus && repo.getJumlahBuku() == 1 && repo.cariBuku("B001") == null) {
            System.out.println("-> STATUS: [PASS] Buku berhasil dihapus dan referensi dibersihkan dari koleksi.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Gagal menghapus buku terdaftar.");
        }

        // Test 9: Hapus buku yang tidak terdaftar
        System.out.println("\n[UJI-09] Menghapus buku tidak terdaftar ('B999')");
        boolean hapusGagal = repo.hapusBuku("B999");
        if (!hapusGagal && repo.getJumlahBuku() == 1) {
            System.out.println("-> STATUS: [PASS] Mengembalikan false tanpa merusak data koleksi yang ada.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Menghasilkan status tidak konsisten.");
        }

        // Test 10: Proteksi enkapsulasi internal koleksi
        System.out.println("\n[UJI-10] Verifikasi enkapsulasi internal list (Unmodifiable View)");
        try {
            List<Buku> unmodifiable = repo.getDaftarBuku();
            unmodifiable.add(new Buku("B003", "Hacking Attempt"));
            System.out.println("-> STATUS: [FAIL] List internal dapat dimutasi dari luar.");
        } catch (UnsupportedOperationException e) {
            System.out.println("-> STATUS: [PASS] Koleksi terlindungi enkapsulasi (UnsupportedOperationException tertangkap).");
            passCount++;
        }

        System.out.println("\n================================================================================");
        System.out.println(" RINGKASAN HASIL PENGUJIAN P11: " + passCount + "/" + totalTest + " SKENARIO BERHASIL (" + (passCount * 100 / totalTest) + "%)");
        System.out.println("================================================================================");
    }
}
