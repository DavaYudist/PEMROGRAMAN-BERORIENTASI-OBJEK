package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestP15Runner {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" SUITE VERIFIKASI DEMONSTRASI AKHIR & EVALUASI INDIVIDU (MODUL 15)");
        System.out.println(" Mahasiswa  : M. Davana Yudista Firdausy (NRP: 3125522002)");
        System.out.println(" Kelas      : A D3 TI-SM | PENS PSDKU Sumenep");
        System.out.println("================================================================================");

        int passCount = 0;
        int totalTest = 10;
        Path sampleData = Paths.get("data", "data_sampel.txt");

        try {
            if (!Files.exists(sampleData.getParent())) {
                Files.createDirectories(sampleData.getParent());
            }
        } catch (IOException ignored) {}

        // Demo 1: Alur Normal Penambahan & Eksekusi Bisnis
        System.out.println("\n[DEMO-01] Alur Normal: Menambah entitas valid dan menampilkan koleksi");
        Perpustakaan pDemo = new Perpustakaan();
        pDemo.tambahBuku(new Buku("B001", "Dasar Pemrograman Java"));
        pDemo.tambahBuku(new Buku("B002", "Pola Desain Berorientasi Objek"));
        if (pDemo.getJumlahBuku() == 2) {
            System.out.println("-> STATUS: [PASS] Alur normal sukses. Koleksi berisi 2 entitas sah.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Alur normal gagal.");
        }

        // Demo 2: Kondisi Gagal - Penolakan Kode Duplikat
        System.out.println("\n[DEMO-02] Kondisi Gagal: Penolakan penambahan kode duplikat");
        try {
            pDemo.tambahBuku(new Buku("B001", "Buku Palsu"));
            System.out.println("-> STATUS: [FAIL] Duplikat lolos.");
        } catch (IllegalArgumentException e) {
            System.out.println("-> STATUS: [PASS] Penolakan sukses via IllegalArgumentException: '" + e.getMessage() + "'.");
            passCount++;
        }

        // Demo 3: Konsistensi State Pasca-Kegagalan
        System.out.println("\n[DEMO-03] Konsistensi State: Membuktikan operasi yang ditolak tidak merusak data");
        if (pDemo.getJumlahBuku() == 2 && pDemo.cariBuku("B001").getJudul().equals("Dasar Pemrograman Java")) {
            System.out.println("-> STATUS: [PASS] State koleksi terbukti 100% konsisten dan utuh.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] State koleksi rusak.");
        }

        // Demo 4: Persistensi Fisik - Simpan ke berkas sampel
        System.out.println("\n[DEMO-04] Persistensi Fisik: Menyimpan koleksi aktif ke media berkas");
        try {
            pDemo.simpan(sampleData);
            if (Files.exists(sampleData) && Files.size(sampleData) > 0) {
                System.out.println("-> STATUS: [PASS] Persistensi berkas berhasil ditulis (" + Files.size(sampleData) + " bytes).");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Gagal menulis ke file.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error: " + e.getMessage());
        }

        // Demo 5: Rekonstruksi Pasca-Restart Sesi
        System.out.println("\n[DEMO-05] Simulasi Restart & Pemuatan Ulang: Menutup memori dan memuat kembali");
        Perpustakaan pRestart = new Perpustakaan();
        try {
            pRestart.muat(sampleData);
            if (pRestart.getJumlahBuku() == 2 && pRestart.cariBuku("B002") != null) {
                System.out.println("-> STATUS: [PASS] Data dipulihkan sempurna ke memori baru.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Pemulihan gagal.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error pemulihan: " + e.getMessage());
        }

        // Demo 6: Penelusuran Kode - Enkapsulasi & Immutability
        System.out.println("\n[DEMO-06] Code Walkthrough: Verifikasi Enkapsulasi List (Unmodifiable View)");
        try {
            pRestart.getDaftarBuku().clear();
            System.out.println("-> STATUS: [FAIL] Referensi internal dapat dirusak dari luar.");
        } catch (UnsupportedOperationException e) {
            System.out.println("-> STATUS: [PASS] Enkapsulasi internal terproteksi dari modifikasi ilegal luar.");
            passCount++;
        }

        // Demo 7: Penelusuran Kode - Validasi Invarian Delimiter
        System.out.println("\n[DEMO-07] Code Walkthrough: Validasi Invarian batas terhadap karakter delimiter '|'");
        try {
            new Buku("B999", "Judul | Rusak");
            System.out.println("-> STATUS: [FAIL] Karakter '|' lolos.");
        } catch (IllegalArgumentException e) {
            System.out.println("-> STATUS: [PASS] Penegakan invarian konstruktor berhasil menolak delimiter ilegal.");
            passCount++;
        }

        // Demo 8: Fitur Perubahan Terarah (Langkah 4 Syllabus) - Substring Search
        System.out.println("\n[DEMO-08] Perubahan Terarah: Pencarian parsial berdasarkan sebagian kata judul");
        pRestart.tambahBuku(new Buku("B003", "Pemrograman Web Modern"));
        pRestart.tambahBuku(new Buku("B004", "Dasar Jaringan Komputer"));
        List<Buku> hasilCari = pRestart.cariBukuSebagianJudul("dasar");
        if (hasilCari.size() == 2) {
            System.out.println("-> STATUS: [PASS] Ditemukan " + hasilCari.size() + " buku memuat kata 'dasar':");
            for (Buku b : hasilCari) {
                System.out.println("   * " + b);
            }
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Pencarian substring parsial gagal.");
        }

        // Demo 9: Fitur Perubahan Terarah - Pencarian kata tidak ada
        System.out.println("\n[DEMO-09] Perubahan Terarah: Pencarian sebagian kata yang tidak ada");
        List<Buku> hasilKosong = pRestart.cariBukuSebagianJudul("QuantumAI");
        if (hasilKosong.isEmpty()) {
            System.out.println("-> STATUS: [PASS] Menghasilkan list kosong secara aman tanpa NullPointerException.");
            passCount++;
        } else {
            System.out.println("-> STATUS: [FAIL] Menghasilkan data tidak sesuai.");
        }

        // Demo 10: Eliminasi Entitas & Sinkronisasi Akhir
        System.out.println("\n[DEMO-10] Verifikasi Penghapusan & Simpan Ulang Final");
        boolean hapusB1 = pRestart.hapusBuku("B001");
        try {
            pRestart.simpan(sampleData);
            if (hapusB1 && pRestart.getJumlahBuku() == 3) {
                System.out.println("-> STATUS: [PASS] Buku berhasil dihapus dan berkas fisik tersinkronisasi.");
                passCount++;
            } else {
                System.out.println("-> STATUS: [FAIL] Gagal sinkronisasi.");
            }
        } catch (Exception e) {
            System.out.println("-> STATUS: [FAIL] Error: " + e.getMessage());
        }

        System.out.println("\n================================================================================");
        System.out.println(" RINGKASAN VERIFIKASI DEMO P15: " + passCount + "/" + totalTest + " SKENARIO LULUS (" + (passCount * 100 / totalTest) + "%)");
        System.out.println("================================================================================");
    }
}
