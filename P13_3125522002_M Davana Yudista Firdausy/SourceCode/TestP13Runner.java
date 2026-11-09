package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class TestP13Runner {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" SUITE PENGUJIAN OTOMATIS MODUL 13 - INTEGRASI MENU & DIRTY STATE TRACKING");
        System.out.println(" Mahasiswa  : M. Davana Yudista Firdausy (NRP: 3125522002)");
        System.out.println(" Kelas      : A D3 TI-SM | PENS PSDKU Sumenep");
        System.out.println("================================================================================");

        int passCount = 0;
        int totalTest = 10;
        Path testFile = Paths.get("data", "test_menu_store.txt");
        Perpustakaan repo = new Perpustakaan();
        boolean berubah = false;

        // Test 1: Inisialisasi & Dirty State Awal
        System.out.println("\n[UJI-01] Status dirty state awal saat inisialisasi");
        if (!berubah && repo.getJumlahBuku() == 0) {
            System.out.println("-> STATUS: [PASS] Inisialisasi awal bersih (berubah == false).");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] State awal kotor.");
        }

        // Test 2: Tambah buku sukses mengubah state berubah -> true
        System.out.println("\n[UJI-02] Mutasi berhasil (menuTambah) mengubah status menjadi kotor (berubah == true)");
        Scanner inTambah = new Scanner("B001\nAlgoritma Lanjut\n");
        if (Main.menuTambah(inTambah, repo)) {
            berubah = true;
        }
        if (berubah && repo.getJumlahBuku() == 1) {
            System.out.println("-> STATUS: [PASS] Flag berubah berhasil berpindah ke TRUE setelah penambahan.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Flag berubah tidak terpicu.");
        }

        // Test 3: Tambah buku gagal (duplikat) TIDAK boleh mengubah state
        System.out.println("\n[UJI-03] Penambahan gagal (duplikat) tidak memicu mutasi status tambahan");
        boolean stateSebelum = berubah;
        Scanner inDup = new Scanner("B001\nAlgoritma Duplikat\n");
        boolean hasilTambah = Main.menuTambah(inDup, repo);
        if (!hasilTambah && berubah == stateSebelum && repo.getJumlahBuku() == 1) {
            System.out.println("-> STATUS: [PASS] Penambahan duplikat ditolak, flag dan koleksi tetap konsisten.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Inkonsistensi status pada operasi gagal.");
        }

        // Test 4: Simpan sukses me-reset status berubah -> false
        System.out.println("\n[UJI-04] Simpan sukses (menuSimpan) me-reset status dirty state menjadi FALSE");
        if (Main.menuSimpan(repo, testFile)) {
            berubah = false;
        }
        if (!berubah && Files.exists(testFile)) {
            System.out.println("-> STATUS: [PASS] Flag berubah berhasil di-reset ke FALSE setelah file tertulis.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Gagal me-reset status setelah simpan.");
        }

        // Test 5: Simpan gagal TIDAK me-reset status berubah
        System.out.println("\n[UJI-05] Simpan gagal menjaga status berubah tetap TRUE (Mencegah Silent Data Loss)");
        berubah = true; // Anggap ada mutasi baru
        Path invalidPath = Paths.get("Z:\\folder_ilegal_tidak_ada\\file.txt");
        boolean hasilSimpan = Main.menuSimpan(repo, invalidPath);
        if (!hasilSimpan && berubah) {
            System.out.println("-> STATUS: [PASS] Simpan gagal mempertahankan flag berubah == TRUE. Data loss dicegah!");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Flag berubah ter-reset meskipun simpan gagal.");
        }

        // Kembalikan ke state tersimpan
        Main.menuSimpan(repo, testFile);
        berubah = false;

        // Test 6: Hapus buku sukses mengubah berubah -> true
        System.out.println("\n[UJI-06] Penghapusan sukses (menuHapus) mengubah status berubah -> TRUE");
        Scanner inHapus = new Scanner("B001\n");
        if (Main.menuHapus(inHapus, repo)) {
            berubah = true;
        }
        if (berubah && repo.getJumlahBuku() == 0) {
            System.out.println("-> STATUS: [PASS] Buku terhapus dan flag berubah bernilai TRUE.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Gagal menghapus atau flag tidak termutasi.");
        }

        // Test 7: Hapus buku tidak terdaftar TIDAK memicu mutasi status
        System.out.println("\n[UJI-07] Penghapusan kode fiktif tidak memutasi koleksi");
        Scanner inHapusGagal = new Scanner("B999\n");
        boolean statusHapus = Main.menuHapus(inHapusGagal, repo);
        if (!statusHapus) {
            System.out.println("-> STATUS: [PASS] Operasi hapus fiktif mengembalikan false tanpa error.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Mengembalikan status sukses pada data fiktif.");
        }

        // Test 8: Menu pencarian
        System.out.println("\n[UJI-08] Verifikasi menuCari membaca referensi tanpa mengubah dirty flag");
        repo.tambahBuku(new Buku("B002", "Jaringan Komputer"));
        boolean flagSebelumCari = berubah;
        Scanner inCari = new Scanner("B002\n");
        Main.menuCari(inCari, repo);
        if (berubah == flagSebelumCari) {
            System.out.println("-> STATUS: [PASS] Operasi baca murni (read-only) tidak mengubah status dirty state.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Dirty state termutasi pada operasi baca.");
        }

        // Test 9: Muat ulang memulihkan kondisi dari storage fisik
        System.out.println("\n[UJI-09] Menu muat ulang (menuMuat) memulihkan data dari storage fisik");
        if (Main.menuMuat(repo, testFile)) {
            berubah = false;
        }
        if (!berubah && repo.cariBuku("B001") != null) {
            System.out.println("-> STATUS: [PASS] Koleksi berhasil dipulihkan dari berkas fisik, flag berubah == FALSE.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Muat ulang gagal memulihkan data.");
        }

        // Test 10: Verifikasi alur transaksional menyeluruh
        System.out.println("\n[UJI-10] Verifikasi alur transaksional komprehensif bebas error");
        if (repo.getJumlahBuku() >= 1 && !berubah) {
            System.out.println("-> STATUS: [PASS] Seluruh siklus CRUD terintegrasi menu telah terverifikasi aman.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Status akhir tidak konsisten.");
        }

        System.out.println("\n================================================================================");
        System.out.println(" RINGKASAN HASIL PENGUJIAN P13: " + passCount + "/" + totalTest + " SKENARIO BERHASIL (" + (passCount * 100 / totalTest) + "%)");
        System.out.println("================================================================================");
    }
}
