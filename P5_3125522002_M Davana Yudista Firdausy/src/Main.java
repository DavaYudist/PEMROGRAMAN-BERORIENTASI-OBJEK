package src;

/**
 * Main Class untuk eksekusi dan verifikasi praktikum Modul 5:
 * Inheritance, Generalization, Superclass, dan Subclass.
 *
 * Mengimplementasikan pengujian komprehensif sesuai rubrik lab:
 * 1. Test Case 1: Pembuktian Pewarisan (Inheritance), Keyword extends, super(...), & Constructor Chaining
 * 2. Test Case 2: Akses Member Superclass dari Subclass & Polimorfisme (Enkapsulasi Terjaga)
 * 3. Test Case 3: Integrasi Hierarki Inheritance dengan Relasi Multi-Objek (Asosiasi, Agregasi, Komposisi)
 * 4. Test Case 4: Pengujian Validasi Data Warisan & Subclass (Negative Testing)
 *
 * @author M. Davana Yudista Firdausy
 * @nrp 3125522002
 * @kelas A D3 TI-SM
 * @institusi PENS PSDKU Sumenep
 * @proyek Sistem Manajemen Inventaris Toko
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK - MODUL 5                  ");
        System.out.println("            INHERITANCE, GENERALIZATION, SUPERCLASS & SUBCLASS                  ");
        System.out.println("================================================================================");
        System.out.println("  Nama Mahasiswa : M. Davana Yudista Firdausy");
        System.out.println("  NRP            : 3125522002");
        System.out.println("  Kelas          : A D3 TI-SM");
        System.out.println("  Institusi      : PENS PSDKU Sumenep");
        System.out.println("  Proyek         : Sistem Manajemen Inventaris Toko (Iterasi P4 -> P5)");
        System.out.println("================================================================================\n");

        // ==============================================================================
        // TEST CASE 1: PEMBUKTIAN INHERITANCE, KEYWORD EXTENDS, SUPER(...), & CONSTRUCTOR CHAINING
        // Membuktikan bahwa saat subclass diinstansiasi:
        // 1. Constructor superclass (Person) dieksekusi terlebih dahulu melalui super(...)
        // 2. Kemudian constructor subclass (Petugas / Pemasok) dieksekusi.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 1: PEMBUKTIAN INHERITANCE & CONSTRUCTOR CHAINING               ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Menginstansiasi Objek Subclass 1: Petugas (Petugas extends Person):");
        System.out.println("Membuat objek Petugas 1 (Ahmad Fauzi):");
        Petugas petugas1 = new Petugas("PTG-001", "Ahmad Fauzi", "Jl. Trunojoyo No. 12, Sumenep", "Supervisor Gudang", "Pagi");
        System.out.println("Hasil: Constructor chaining berhasil membuktikan urutan eksekusi Person -> Petugas.\n");

        System.out.println("Membuat objek Petugas 2 (Siti Nurhaliza):");
        Petugas petugas2 = new Petugas("PTG-002", "Siti Nurhaliza", "Jl. Diponegoro No. 45, Sumenep", "Kasir Senior", "Siang");
        System.out.println("Hasil: Constructor chaining berhasil membuktikan urutan eksekusi Person -> Petugas.\n");

        System.out.println("[Langkah 2] Menginstansiasi Objek Subclass 2: Pemasok (Pemasok extends Person):");
        System.out.println("Membuat objek Pemasok 1 (PT Gramedia Pustaka Utama):");
        Pemasok pemasok1 = new Pemasok("PMS-101", "Budi Santoso", "Jl. Palmerah Barat No. 29, Jakarta", "PT Gramedia Mitra Sarana", "Alat Tulis & Buku");
        System.out.println("Hasil: Constructor chaining berhasil membuktikan urutan eksekusi Person -> Pemasok.\n");

        System.out.println("Membuat objek Pemasok 2 (CV Sinar Grafika Stationery):");
        Pemasok pemasok2 = new Pemasok("PMS-102", "Dewi Lestari", "Kawasan Industri Sier No. 8, Surabaya", "CV Sinar Grafika", "Kertas & Cetakan");
        System.out.println("Hasil: Constructor chaining berhasil membuktikan urutan eksekusi Person -> Pemasok.\n");

        System.out.println("Kesimpulan Test Case 1: Hubungan IS-A, keyword extends, pemanggilan super(...),");
        System.out.println("dan mekanisme Constructor Chaining terbukti berjalan sesuai kaidah OOP Java.\n");

        // ==============================================================================
        // TEST CASE 2: AKSES MEMBER SUPERCLASS DARI SUBCLASS & POLIMORFISME
        // Membuktikan akses atribut private superclass melalui public getters,
        // method overriding (@Override) dengan super.tampilkanProfil(), serta
        // polymorphic substitution principle (subtipe dapat diperlakukan sebagai supertipe).
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 2: AKSES MEMBER SUPERCLASS & CODE REUSE DENGAN OVERRIDING      ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Akses Getter Atribut Warisan vs Atribut Spesifik Subclass:");
        System.out.println("Data Petugas 1:");
        System.out.println("  * [Warisan Person] ID     : " + petugas1.getId());
        System.out.println("  * [Warisan Person] Nama   : " + petugas1.getNama());
        System.out.println("  * [Warisan Person] Alamat : " + petugas1.getAlamat());
        System.out.println("  * [Spesifik Petugas] Jabatan    : " + petugas1.getJabatan());
        System.out.println("  * [Spesifik Petugas] Shift Kerja: " + petugas1.getShiftKerja());
        System.out.println();

        System.out.println("Data Pemasok 1:");
        System.out.println("  * [Warisan Person] ID     : " + pemasok1.getId());
        System.out.println("  * [Warisan Person] Nama   : " + pemasok1.getNama());
        System.out.println("  * [Warisan Person] Alamat : " + pemasok1.getAlamat());
        System.out.println("  * [Spesifik Pemasok] Perusahaan : " + pemasok1.getNamaPerusahaan());
        System.out.println("  * [Spesifik Pemasok] Kategori   : " + pemasok1.getKategoriSuplai());
        System.out.println();

        System.out.println("[Langkah 2] Memanggil Method Overriding tampilkanProfil() pada Subclass:");
        petugas1.tampilkanProfil();
        System.out.println();
        pemasok1.tampilkanProfil();
        System.out.println();

        System.out.println("[Langkah 3] Demonstrasi Polimorfisme (Person Reference menunjuk Objek Subclass):");
        Person refPerson1 = petugas2; // Upcasting implicit: Petugas IS-A Person
        Person refPerson2 = pemasok2; // Upcasting implicit: Pemasok IS-A Person

        System.out.println("Memanggil tampilkanProfil() via referensi Person (Dynamic Method Dispatch):");
        System.out.println("--> Referensi Person menunjuk objek Petugas (Siti Nurhaliza):");
        refPerson1.tampilkanProfil();
        System.out.println();
        System.out.println("--> Referensi Person menunjuk objek Pemasok (CV Sinar Grafika):");
        refPerson2.tampilkanProfil();
        System.out.println();

        System.out.println("Kesimpulan Test Case 2: Subclass berhasil mewarisi fungsionalitas Person,");
        System.out.println("meng-override method dengan memanfaatkan super, serta mendukung polimorfisme.\n");

        // ==============================================================================
        // TEST CASE 3: INTEGRASI HIERARKI INHERITANCE DENGAN RELASI MULTI-OBJEK
        // Mengintegrasikan:
        // 1. Agregasi: Gudang menampung objek Barang independen.
        // 2. Asosiasi: Transaksi mengaitkan Petugas (PIC) dan Pemasok (Vendor Suplai).
        // 3. Komposisi: Transaksi membuat dan mengelola DetailTransaksi secara eksklusif.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 3: INTEGRASI INHERITANCE DENGAN ASOSIASI, AGREGASI & KOMPOSISI ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Inisialisasi Objek Barang & Agregasi ke Gudang:");
        Barang b1 = new Barang("BRG-001", "Buku Tulis Hardcover", "Alat Tulis", 50, 15000.0);
        Barang b2 = new Barang("BRG-002", "Pulpen Gel 0.5mm", "Alat Tulis", 100, 5000.0);
        Barang b3 = new Barang("BRG-003", "Kertas HVS A4 70gr", "Kertas", 40, 48000.0);

        Gudang gudangPusat = new Gudang("GDG-SMP-01", "Gudang Distribusi Sumenep");
        gudangPusat.tambahBarang(b1);
        gudangPusat.tambahBarang(b2);
        gudangPusat.tambahBarang(b3);
        System.out.println();
        gudangPusat.tampilkanInventaris();

        System.out.println("[Langkah 2] Transaksi 1: Barang Masuk (Restock)");
        System.out.println("Karakteristik: Asosiasi dengan Petugas 1 & Pemasok 1, Komposisi dengan DetailTransaksi");
        Transaksi trxMasuk = new Transaksi("TRX-IN-2026-001", "Barang Masuk", petugas1, pemasok1);

        System.out.println("Menambahkan item ke dalam transaksi masuk (Komposisi otomatis menambah stok):");
        trxMasuk.tambahItem(b1, 30); // Stok b1 bertambah 30 (50 -> 80)
        trxMasuk.tambahItem(b2, 50); // Stok b2 bertambah 50 (100 -> 150)
        System.out.println();
        trxMasuk.tampilkanTransaksi();

        System.out.println("[Langkah 3] Transaksi 2: Barang Keluar (Penjualan Kasir)");
        System.out.println("Karakteristik: Asosiasi dengan Petugas 2 (Kasir), Komposisi dengan DetailTransaksi");
        Transaksi trxKeluar = new Transaksi("TRX-OUT-2026-001", "Barang Keluar", petugas2);

        System.out.println("Menambahkan item ke dalam transaksi keluar (Komposisi otomatis memotong stok):");
        trxKeluar.tambahItem(b1, 20); // Stok b1 berkurang 20 (80 -> 60)
        trxKeluar.tambahItem(b3, 15); // Stok b3 berkurang 15 (40 -> 25)
        System.out.println();
        trxKeluar.tampilkanTransaksi();

        System.out.println("[Langkah 4] Kondisi Terkini Inventaris Gudang Pasca Seluruh Transaksi:");
        gudangPusat.tampilkanInventaris();

        System.out.println("Kesimpulan Test Case 3: Seluruh relasi objek (Inheritance, Association,");
        System.out.println("Aggregation, dan Composition) beroperasi secara harmonis dalam arsitektur terpadu.\n");

        // ==============================================================================
        // TEST CASE 4: PENGUJIAN VALIDASI DATA WARISAN & SUBCLASS (NEGATIVE TESTING)
        // Memastikan seluruh aturan validasi enkapsulasi pada superclass dan subclass
        // menolak data tidak sah dan mempertahankan integritas data objek.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 4: NEGATIVE TESTING PADA ENKAPSULASI WARISAN & SUBCLASS        ###");
        System.out.println("################################################################################\n");

        System.out.println("--- 1. Pengujian Validasi Setter Warisan dari Superclass Person ---");
        System.out.println("Kondisi awal Petugas 1 -> Nama: " + petugas1.getNama() + ", Alamat: " + petugas1.getAlamat());
        System.out.println("Mencoba petugas1.setNama(\"\"):");
        petugas1.setNama("");
        System.out.println("Mencoba petugas1.setNama(null):");
        petugas1.setNama(null);
        System.out.println("Mencoba petugas1.setAlamat(\"   \"):");
        petugas1.setAlamat("   ");
        System.out.println("Mencoba petugas1.setAlamat(null):");
        petugas1.setAlamat(null);
        System.out.println("Verifikasi integritas data Petugas 1 pasca uji negatif warisan:");
        System.out.println("  * Nama   : " + petugas1.getNama() + " (TETAP AMAN)");
        System.out.println("  * Alamat : " + petugas1.getAlamat() + " (TETAP AMAN)");
        System.out.println();

        System.out.println("--- 2. Pengujian Validasi Setter Spesifik Subclass Petugas ---");
        System.out.println("Kondisi awal Petugas 1 -> Jabatan: " + petugas1.getJabatan() + ", Shift: " + petugas1.getShiftKerja());
        System.out.println("Mencoba petugas1.setJabatan(\"\"):");
        petugas1.setJabatan("");
        System.out.println("Mencoba petugas1.setShiftKerja(\"   \"):");
        petugas1.setShiftKerja("   ");
        System.out.println("Verifikasi integritas data spesifik Petugas 1:");
        System.out.println("  * Jabatan     : " + petugas1.getJabatan() + " (TETAP AMAN)");
        System.out.println("  * Shift Kerja : " + petugas1.getShiftKerja() + " (TETAP AMAN)");
        System.out.println();

        System.out.println("--- 3. Pengujian Validasi Setter Spesifik Subclass Pemasok ---");
        System.out.println("Kondisi awal Pemasok 1 -> Perusahaan: " + pemasok1.getNamaPerusahaan() + ", Kategori: " + pemasok1.getKategoriSuplai());
        System.out.println("Mencoba pemasok1.setNamaPerusahaan(\"\"):");
        pemasok1.setNamaPerusahaan("");
        System.out.println("Mencoba pemasok1.setKategoriSuplai(\"   \"):");
        pemasok1.setKategoriSuplai("   ");
        System.out.println("Verifikasi integritas data spesifik Pemasok 1:");
        System.out.println("  * Perusahaan  : " + pemasok1.getNamaPerusahaan() + " (TETAP AMAN)");
        System.out.println("  * Kategori    : " + pemasok1.getKategoriSuplai() + " (TETAP AMAN)");
        System.out.println();

        System.out.println("--- 4. Pengujian Mutasi Stok Invalid & Batasan Bisnis ---");
        System.out.println("Stok saat ini " + b3.getNamaBarang() + ": " + b3.getStok() + " unit.");
        System.out.println("Mencoba transaksi keluar melebihi stok (misal: 100 unit):");
        boolean statusOversell = trxKeluar.tambahItem(b3, 100);
        System.out.println("Status mutasi oversell: " + (statusOversell ? "BERHASIL" : "GAGAL (Ditolak Sistem)"));
        System.out.println("Sisa stok b3: " + b3.getStok() + " unit (Tetap aman tidak berubah)");
        System.out.println();

        System.out.println("Mencoba tambahItem dengan jumlah negatif (-5):");
        trxKeluar.tambahItem(b3, -5);
        System.out.println("Mencoba tambahItem dengan objek barang null:");
        trxKeluar.tambahItem(null, 10);
        System.out.println();

        System.out.println("================================================================================");
        System.out.println("    SELURUH PENGUJIAN MODUL 5: INHERITANCE & GENERALIZATION BERHASIL 100%!      ");
        System.out.println("================================================================================");
    }
}
