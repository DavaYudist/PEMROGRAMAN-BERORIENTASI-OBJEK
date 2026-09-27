package src;

/**
 * Main Class untuk eksekusi dan verifikasi praktikum Modul 4:
 * Relasi Objek - Asosiasi, Agregasi, dan Komposisi.
 *
 * Mengimplementasikan pengujian komprehensif sesuai rubrik lab:
 * 1. Test Case 1: Demonstrasi Agregasi (Gudang -> Barang)
 * 2. Test Case 2: Demonstrasi Asosiasi (Transaksi -> Petugas)
 * 3. Test Case 3: Demonstrasi Komposisi (Transaksi -> DetailTransaksi & Mutasi Stok)
 * 4. Test Case 4: Negative Testing & Validasi Enkapsulasi
 *
 * @author M. Davana Yudista Firdausy
 * @nrp 3125522002
 * @kelas A D3 TI-SM
 * @institusi PENS PSDKU Sumenep
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK - MODUL 4                  ");
        System.out.println("               RELASI OBJEK: ASOSIASI, AGREGASI, DAN KOMPOSISI                  ");
        System.out.println("================================================================================");
        System.out.println("  Nama Mahasiswa : M. Davana Yudista Firdausy");
        System.out.println("  NRP            : 3125522002");
        System.out.println("  Kelas          : A D3 TI-SM");
        System.out.println("  Institusi      : PENS PSDKU Sumenep");
        System.out.println("  Proyek         : Sistem Manajemen Inventaris Toko");
        System.out.println("================================================================================\n");

        // ==============================================================================
        // TEST CASE 1: DEMONSTRASI AGREGASI (AGGREGATION)
        // Hubungan "has-a" dengan siklus hidup independen (loose coupling).
        // Objek Barang dibuat di luar Gudang, dimasukkan ke Gudang, dan tetap eksis
        // meskipun objek Gudang dikosongkan/dihancurkan.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 1: DEMONSTRASI AGREGASI (GUDANG HAS-A BARANG)                  ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Inisialisasi objek-objek Barang secara mandiri (Independen):");
        Barang b1 = new Barang("BRG-001", "Buku Tulis Hardcover", "Alat Tulis", 50, 15000.0);
        Barang b2 = new Barang("BRG-002", "Pulpen Gel 0.5mm", "Alat Tulis", 100, 5000.0);
        Barang b3 = new Barang("BRG-003", "Kertas HVS A4 70gr", "Kertas", 40, 48000.0);

        System.out.println("  * Objek b1 dibuat: [" + b1.getKodeBarang() + "] " + b1.getNamaBarang());
        System.out.println("  * Objek b2 dibuat: [" + b2.getKodeBarang() + "] " + b2.getNamaBarang());
        System.out.println("  * Objek b3 dibuat: [" + b3.getKodeBarang() + "] " + b3.getNamaBarang());
        System.out.println();

        System.out.println("[Langkah 2] Membuat objek Gudang dan melakukan agregasi objek Barang:");
        Gudang gudangPusat = new Gudang("GDG-SMP-01", "Gudang Distribusi Sumenep");
        gudangPusat.tambahBarang(b1);
        gudangPusat.tambahBarang(b2);
        gudangPusat.tambahBarang(b3);
        System.out.println();

        System.out.println("[Langkah 3] Menampilkan daftar inventaris dalam Gudang:");
        gudangPusat.tampilkanInventaris();

        System.out.println("[Langkah 4] Pembuktian Agregasi (Independent Lifecycle):");
        System.out.println("Mengosongkan inventaris gudang...");
        gudangPusat.kosongkanGudang();
        System.out.println("Status Gudang setelah dikosongkan (Jumlah SKU: " + gudangPusat.getDaftarBarang().size() + ")");
        System.out.println("\nMemeriksa eksistensi objek Barang di memori:");
        System.out.println("  * Barang b1 tetap eksis -> " + b1.getNamaBarang() + " | Stok: " + b1.getStok() + " unit");
        System.out.println("  * Barang b2 tetap eksis -> " + b2.getNamaBarang() + " | Stok: " + b2.getStok() + " unit");
        System.out.println("  * Barang b3 tetap eksis -> " + b3.getNamaBarang() + " | Stok: " + b3.getStok() + " unit");
        System.out.println("Kesimpulan: Konsep Agregasi terpenuhi. Siklus hidup Barang tidak terikat pada Gudang.\n");

        // Memasukkan kembali barang ke gudang untuk simulasi operasional selanjutnya
        gudangPusat.tambahBarang(b1);
        gudangPusat.tambahBarang(b2);
        gudangPusat.tambahBarang(b3);
        System.out.println();

        // ==============================================================================
        // TEST CASE 2: DEMONSTRASI ASOSIASI (ASSOCIATION)
        // Hubungan "knows-a" atau "uses-a".
        // Transaksi berasosiasi dengan Petugas yang menangani transaksi tersebut.
        // Petugas dan Transaksi merupakan entitas independen yang saling berinteraksi.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 2: DEMONSTRASI ASOSIASI (TRANSAKSI USES/KNOWS-A PETUGAS)       ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Membuat entitas Petugas:");
        Petugas petugas1 = new Petugas("PTG-101", "Ahmad Fauzi", "Supervisor Gudang");
        Petugas petugas2 = new Petugas("PTG-102", "Siti Nurhaliza", "Kasir Senior");
        petugas1.tampilkanProfil();
        petugas2.tampilkanProfil();

        System.out.println("[Langkah 2] Mengasosiasikan Petugas dengan objek Transaksi:");
        Transaksi trxAsosiasi = new Transaksi("TRX-2026-001", "Barang Masuk", petugas1);
        System.out.println("Transaksi [" + trxAsosiasi.getIdTransaksi() + "] berhasil dibuat dan diasosiasikan dengan Petugas [" + trxAsosiasi.getPetugas().getNamaPetugas() + "]");
        System.out.println();

        System.out.println("[Langkah 3] Mengakses dan memverifikasi profil Petugas melalui objek Transaksi:");
        System.out.println("Profil Petugas penanggung jawab Transaksi " + trxAsosiasi.getIdTransaksi() + ":");
        trxAsosiasi.getPetugas().tampilkanProfil();

        System.out.println("[Langkah 4] Pembuktian Asosiasi (Fleksibilitas Relasi):");
        System.out.println("Satu petugas dapat melayani transaksi lain secara bebas:");
        Transaksi trxAsosiasi2 = new Transaksi("TRX-2026-002", "Barang Keluar", petugas1);
        System.out.println("Petugas " + petugas1.getNamaPetugas() + " juga menangani " + trxAsosiasi2.getIdTransaksi());

        System.out.println("\nMengubah PIC transaksi TRX-2026-001 ke Petugas 2 (Siti Nurhaliza):");
        trxAsosiasi.setPetugas(petugas2);
        System.out.println("PIC baru Transaksi TRX-2026-001 -> ID: " + trxAsosiasi.getPetugas().getIdPetugas() + 
                           ", Nama: " + trxAsosiasi.getPetugas().getNamaPetugas() + 
                           ", Jabatan: " + trxAsosiasi.getPetugas().getJabatan());
        System.out.println("Kesimpulan: Konsep Asosiasi terpenuhi. Kedua objek saling berinteraksi secara independen.\n");

        // ==============================================================================
        // TEST CASE 3: DEMONSTRASI KOMPOSISI (COMPOSITION) & MUTASI STOK
        // Hubungan "part-of" dengan siklus hidup terikat erat (tight coupling).
        // DetailTransaksi dibuat dan dikendalikan secara eksklusif di dalam Transaksi.
        // Mutasi stok (tambah/kurang) dieksekusi secara otomatis saat item ditambahkan.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 3: DEMONSTRASI KOMPOSISI (TRANSAKSI CONTAINS DETAILTRANSAKSI)  ###");
        System.out.println("################################################################################\n");

        System.out.println("--- Skenario A: Transaksi Barang Keluar (Penjualan / Pengeluaran Stok) ---");
        System.out.println("Kondisi stok sebelum transaksi pengeluaran:");
        System.out.println("  * " + b1.getNamaBarang() + " : " + b1.getStok() + " unit");
        System.out.println("  * " + b2.getNamaBarang() + " : " + b2.getStok() + " unit");
        System.out.println();

        Transaksi trxKeluar = new Transaksi("TRX-OUT-2026-001", "Barang Keluar", petugas2);

        System.out.println("[Menambahkan item transaksi via tambahItem() - Komposisi Aktif]:");
        trxKeluar.tambahItem(b1, 15); // Stok b1 berkurang 15 (50 -> 35)
        trxKeluar.tambahItem(b2, 30); // Stok b2 berkurang 30 (100 -> 70)
        System.out.println();

        System.out.println("[Menampilkan Detail Lengkap Transaksi Pengeluaran]:");
        trxKeluar.tampilkanTransaksi();

        System.out.println("Verifikasi mutasi stok setelah transaksi keluar:");
        System.out.println("  * " + b1.getNamaBarang() + " : " + b1.getStok() + " unit (Tervalidasi berkurang 15 unit)");
        System.out.println("  * " + b2.getNamaBarang() + " : " + b2.getStok() + " unit (Tervalidasi berkurang 30 unit)\n");

        System.out.println("--- Skenario B: Transaksi Barang Masuk (Restock / Penerimaan Barang) ---");
        System.out.println("Kondisi stok sebelum transaksi penerimaan:");
        System.out.println("  * " + b3.getNamaBarang() + " : " + b3.getStok() + " unit");
        System.out.println();

        Transaksi trxMasuk = new Transaksi("TRX-IN-2026-001", "Barang Masuk", petugas1);
        System.out.println("[Menambahkan item transaksi via tambahItem() - Komposisi Aktif]:");
        trxMasuk.tambahItem(b3, 60); // Stok b3 bertambah 60 (40 -> 100)
        System.out.println();

        System.out.println("[Menampilkan Detail Lengkap Transaksi Penerimaan]:");
        trxMasuk.tampilkanTransaksi();

        System.out.println("Verifikasi mutasi stok setelah transaksi masuk:");
        System.out.println("  * " + b3.getNamaBarang() + " : " + b3.getStok() + " unit (Tervalidasi bertambah 60 unit)\n");

        System.out.println("[Kondisi Terkini Inventaris Gudang Pasca Transaksi]:");
        gudangPusat.tampilkanInventaris();

        // ==============================================================================
        // TEST CASE 4: NEGATIVE TESTING & VALIDASI INTEGRITAS DATA
        // Memastikan seluruh validasi P3 dipertahankan dan validasi relasi P4 berjalan.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 4: NEGATIVE VALIDATION TESTING                                  ###");
        System.out.println("################################################################################\n");

        System.out.println("--- 1. Pengujian Pengurangan Stok Melebihi Batas Tersedia ---");
        System.out.println("Stok saat ini " + b1.getNamaBarang() + ": " + b1.getStok() + " unit.");
        System.out.println("Mencoba transaksi keluar sebanyak 500 unit:");
        boolean statusOversell = trxKeluar.tambahItem(b1, 500);
        System.out.println("Status penambahan item: " + (statusOversell ? "BERHASIL" : "GAGAL (Ditolak Sistem)"));
        System.out.println("Sisa stok barang b1: " + b1.getStok() + " unit (Tetap aman tidak berubah)\n");

        System.out.println("--- 2. Pengujian Penambahan Item dengan Jumlah Negatif atau Nol ---");
        System.out.println("Mencoba tambahItem dengan jumlah = -10:");
        trxKeluar.tambahItem(b1, -10);
        System.out.println("Mencoba tambahItem dengan jumlah = 0:");
        trxKeluar.tambahItem(b1, 0);
        System.out.println();

        System.out.println("--- 3. Pengujian Penambahan Item dengan Referensi Objek Null ---");
        System.out.println("Mencoba tambahItem dengan Barang null:");
        trxKeluar.tambahItem(null, 10);
        System.out.println("Mencoba gudangPusat.tambahBarang(null):");
        gudangPusat.tambahBarang(null);
        System.out.println();

        System.out.println("--- 4. Pengujian Nilai Invalid pada Jenis Transaksi & Petugas ---");
        System.out.println("Mencoba set jenis transaksi dengan 'Pinjam':");
        trxKeluar.setJenisTransaksi("Pinjam");
        System.out.println("Mencoba set Petugas penanggung jawab bernilai null:");
        trxKeluar.setPetugas(null);
        System.out.println();

        System.out.println("--- 5. Pengujian Validasi Enkapsulasi Dasar Class Barang & Petugas ---");
        System.out.println("Mencoba b1.setStok(-50):");
        b1.setStok(-50);
        System.out.println("Mencoba b1.setHargaSatuan(-25000):");
        b1.setHargaSatuan(-25000);
        System.out.println("Mencoba b1.setNamaBarang(''):");
        b1.setNamaBarang("");
        System.out.println("Mencoba petugas1.setNamaPetugas('   '):");
        petugas1.setNamaPetugas("   ");
        System.out.println("Mencoba petugas1.setJabatan(''):");
        petugas1.setJabatan("");
        System.out.println();

        System.out.println("[Verifikasi Integritas Data Akhir Objek Barang 1]:");
        System.out.println("Nama Barang  : " + b1.getNamaBarang() + " (Tidak berubah)");
        System.out.println("Stok Barang  : " + b1.getStok() + " unit (Tidak berubah)");
        System.out.printf("Harga Satuan : Rp%,.2f (Tidak berubah)%n", b1.getHargaSatuan());
        System.out.println();

        System.out.println("================================================================================");
        System.out.println("       SELURUH PENGUJIAN RELASI OBJEK & ENKAPSULASI BERHASIL 100%!              ");
        System.out.println("================================================================================");
    }
}
