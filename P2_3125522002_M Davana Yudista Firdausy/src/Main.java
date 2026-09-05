package src;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SISTEM MANAJEMEN INVENTARIS TOKO (MODUL 2 PBO)  ");
        System.out.println("==================================================\n");

        // ==============================================================
        // 1. PENGUJIAN CLASS BARANG
        // ==============================================================
        System.out.println("=== PENGUJIAN CLASS BARANG ===");

        // Instansiasi 2 objek Barang menggunakan parameterized constructor
        Barang barang1 = new Barang("BRG-001", "Buku Tulis Hardcover", "Alat Tulis", 50, 15000.0);
        Barang barang2 = new Barang("BRG-002", "Pulpen Gel 0.5mm", "Alat Tulis", 100, 5000.0);

        // Memanggil method tanpa parameter (void)
        System.out.println("[Kondisi Awal Objek Barang]");
        barang1.tampilkanData();
        barang2.tampilkanData();

        // Memanggil method dengan parameter untuk modifikasi nilai/state
        System.out.println("\n[Modifikasi Stok Barang]");
        barang1.tambahStok(25);
        barang2.tambahStok(50);

        // Menampilkan data setelah penambahan stok
        System.out.println("\n[Kondisi Setelah Tambah Stok]");
        barang1.tampilkanData();
        barang2.tampilkanData();

        // Memanggil method dengan nilai kembali (return value)
        System.out.println("[Perhitungan Total Nilai Inventaris (Return Value)]");
        double totalNilai1 = barang1.hitungTotalNilai();
        double totalNilai2 = barang2.hitungTotalNilai();
        System.out.println("Total Nilai " + barang1.namaBarang + ": Rp" + totalNilai1);
        System.out.println("Total Nilai " + barang2.namaBarang + ": Rp" + totalNilai2);
        System.out.println();

        // ==============================================================
        // 2. PENGUJIAN CLASS PETUGAS
        // ==============================================================
        System.out.println("=== PENGUJIAN CLASS PETUGAS ===");

        // Instansiasi 2 objek Petugas menggunakan parameterized constructor
        Petugas petugas1 = new Petugas("PTG-101", "Ahmad Fauzi", "Staff Gudang");
        Petugas petugas2 = new Petugas("PTG-102", "Siti Nurhaliza", "Kasir Senior");

        // Memanggil method tanpa parameter (void)
        System.out.println("[Kondisi Awal Objek Petugas]");
        petugas1.tampilkanProfil();
        petugas2.tampilkanProfil();

        // Memanggil method dengan parameter untuk modifikasi jabatan
        System.out.println("\n[Pembaruan Jabatan Petugas]");
        petugas1.ubahJabatan("Supervisor Gudang");
        petugas2.ubahJabatan("Kepala Toko");

        // Menampilkan profil setelah update jabatan
        System.out.println("\n[Kondisi Setelah Ubah Jabatan]");
        petugas1.tampilkanProfil();
        petugas2.tampilkanProfil();

        // Memanggil method dengan nilai kembali (return value)
        System.out.println("[Informasi Nama Petugas (Return Value)]");
        String nama1 = petugas1.getNamaPetugas();
        String nama2 = petugas2.getNamaPetugas();
        System.out.println("Nama Petugas 1: " + nama1);
        System.out.println("Nama Petugas 2: " + nama2);
        System.out.println();

        // ==============================================================
        // 3. PENGUJIAN CLASS TRANSAKSI
        // ==============================================================
        System.out.println("=== PENGUJIAN CLASS TRANSAKSI ===");

        // Instansiasi 2 objek Transaksi menggunakan parameterized constructor
        Transaksi trx1 = new Transaksi("TRX-2026-001", "BRG-001", 25, "Barang Masuk");
        Transaksi trx2 = new Transaksi("TRX-2026-002", "BRG-002", 15, "Barang Keluar");

        // Memanggil method tanpa parameter (void)
        System.out.println("[Detail Awal Transaksi]");
        trx1.tampilkanDetail();
        trx2.tampilkanDetail();

        // Memanggil method dengan parameter untuk modifikasi jenis transaksi
        System.out.println("\n[Modifikasi Jenis Transaksi]");
        trx1.setJenisTransaksi("Restock Supplier");
        trx2.setJenisTransaksi("Retur Penjualan");

        // Menampilkan detail setelah modifikasi
        System.out.println("\n[Detail Setelah Modifikasi Transaksi]");
        trx1.tampilkanDetail();
        trx2.tampilkanDetail();

        // Memanggil method dengan nilai kembali (return value)
        System.out.println("[Ringkasan Transaksi (Return Value)]");
        String ringkasan1 = trx1.ringkasanTransaksi();
        String ringkasan2 = trx2.ringkasanTransaksi();
        System.out.println("Ringkasan 1: " + ringkasan1);
        System.out.println("Ringkasan 2: " + ringkasan2);
        System.out.println();
    }
}
