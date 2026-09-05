package src;

public class Main {
    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("   SISTEM MANAJEMEN INVENTARIS TOKO (MODUL 3: ENKAPSULASI & VALIDASI) ");
        System.out.println("====================================================================\n");

        // ==============================================================
        // TEST CASE 1: PENGUJIAN DATA VALID (GETTER & SETTER NORMAL)
        // ==============================================================
        System.out.println("############################################################");
        System.out.println("### TEST CASE 1: PENGUJIAN DENGAN DATA VALID             ###");
        System.out.println("############################################################\n");

        // 1. Class Barang
        System.out.println("--- 1. Pengujian Objek Barang ---");
        Barang barang1 = new Barang("BRG-001", "Buku Tulis Hardcover", "Alat Tulis", 50, 15000.0);
        Barang barang2 = new Barang("BRG-002", "Pulpen Gel 0.5mm", "Alat Tulis", 100, 5000.0);

        System.out.println("[Kondisi Awal Barang]");
        barang1.tampilkanData();
        barang2.tampilkanData();

        // Modifikasi data valid melalui setter
        System.out.println("[Modifikasi Nilai Valid Menggunakan Setter]");
        barang1.setNamaBarang("Buku Tulis Eksekutif A5");
        barang1.setHargaSatuan(17500.0);
        barang1.tambahStok(25);

        barang2.setStok(120);
        barang2.setHargaSatuan(5500.0);

        // Membaca nilai melalui getter
        System.out.println("\n[Verifikasi Nilai Terkini Melalui Getter]");
        System.out.println("Barang 1 -> Kode: " + barang1.getKodeBarang() + ", Nama: " + barang1.getNamaBarang() + 
                           ", Stok: " + barang1.getStok() + ", Harga: Rp" + barang1.getHargaSatuan() + 
                           ", Total Nilai: Rp" + barang1.hitungTotalNilai());
        System.out.println("Barang 2 -> Kode: " + barang2.getKodeBarang() + ", Nama: " + barang2.getNamaBarang() + 
                           ", Stok: " + barang2.getStok() + ", Harga: Rp" + barang2.getHargaSatuan() + 
                           ", Total Nilai: Rp" + barang2.hitungTotalNilai());
        System.out.println();

        // 2. Class Petugas
        System.out.println("--- 2. Pengujian Objek Petugas ---");
        Petugas petugas1 = new Petugas("PTG-101", "Ahmad Fauzi", "Staff Gudang");
        Petugas petugas2 = new Petugas("PTG-102", "Siti Nurhaliza", "Kasir Senior");

        System.out.println("[Profil Awal Petugas]");
        petugas1.tampilkanProfil();
        petugas2.tampilkanProfil();

        // Modifikasi data valid melalui setter dan method
        System.out.println("[Modifikasi Nilai Valid Menggunakan Setter & Method]");
        petugas1.ubahJabatan("Supervisor Gudang");
        petugas2.setNamaPetugas("Siti Nurhaliza, S.E.");

        // Membaca nilai melalui getter
        System.out.println("\n[Verifikasi Nilai Terkini Melalui Getter]");
        System.out.println("Petugas 1 -> ID: " + petugas1.getIdPetugas() + ", Nama: " + petugas1.getNamaPetugas() + ", Jabatan: " + petugas1.getJabatan());
        System.out.println("Petugas 2 -> ID: " + petugas2.getIdPetugas() + ", Nama: " + petugas2.getNamaPetugas() + ", Jabatan: " + petugas2.getJabatan());
        System.out.println();

        // 3. Class Transaksi
        System.out.println("--- 3. Pengujian Objek Transaksi ---");
        Transaksi trx1 = new Transaksi("TRX-2026-001", "BRG-001", 25, "Barang Masuk");
        Transaksi trx2 = new Transaksi("TRX-2026-002", "BRG-002", 15, "Barang Keluar");

        System.out.println("[Detail Awal Transaksi]");
        trx1.tampilkanDetail();
        trx2.tampilkanDetail();

        // Modifikasi data valid melalui setter
        System.out.println("[Modifikasi Nilai Valid Menggunakan Setter]");
        trx1.setJumlah(30);
        trx2.setJenisTransaksi("Barang Masuk");

        // Membaca nilai melalui getter & return method
        System.out.println("\n[Verifikasi Nilai Terkini Melalui Getter & Ringkasan]");
        System.out.println("Transaksi 1 -> ID: " + trx1.getIdTransaksi() + ", Kode: " + trx1.getKodeBarang() + ", Jumlah: " + trx1.getJumlah() + ", Jenis: " + trx1.getJenisTransaksi());
        System.out.println("Ringkasan Trx 1: " + trx1.ringkasanTransaksi());
        System.out.println("Transaksi 2 -> ID: " + trx2.getIdTransaksi() + ", Kode: " + trx2.getKodeBarang() + ", Jumlah: " + trx2.getJumlah() + ", Jenis: " + trx2.getJenisTransaksi());
        System.out.println("Ringkasan Trx 2: " + trx2.ringkasanTransaksi());
        System.out.println();

        // ==============================================================
        // TEST CASE 2: PENGUJIAN DATA INVALID / NEGATIVE TESTING
        // ==============================================================
        System.out.println("############################################################");
        System.out.println("### TEST CASE 2: NEGATIVE TESTING & VALIDASI DATA        ###");
        System.out.println("############################################################\n");

        System.out.println("--- 1. Pengujian Nilai Invalid pada Barang 1 ---");
        System.out.println("Kondisi Sebelum Percobaan Update:");
        System.out.println("Harga: Rp" + barang1.getHargaSatuan() + " | Stok: " + barang1.getStok() + " unit\n");

        System.out.println("Mencoba update hargaSatuan dengan nilai negatif (-15000.0):");
        barang1.setHargaSatuan(-15000.0);

        System.out.println("Mencoba update stok dengan nilai negatif (-20):");
        barang1.setStok(-20);

        System.out.println("Mencoba update namaBarang dengan string kosong (\"\"):");
        barang1.setNamaBarang("");

        System.out.println("Mencoba tambahStok dengan nilai tidak valid (-10):");
        barang1.tambahStok(-10);

        System.out.println("\n[Verifikasi Integritas Data Barang 1 (Data Tidak Boleh Berubah)]");
        System.out.println("Nama Barang : " + barang1.getNamaBarang() + " (Tetap)");
        System.out.println("Harga Satuan: Rp" + barang1.getHargaSatuan() + " (Tetap)");
        System.out.println("Jumlah Stok : " + barang1.getStok() + " unit (Tetap)");
        System.out.println();

        System.out.println("--- 2. Pengujian Nilai Invalid pada Petugas 1 ---");
        System.out.println("Kondisi Sebelum Percobaan Update:");
        System.out.println("Nama: " + petugas1.getNamaPetugas() + " | Jabatan: " + petugas1.getJabatan() + "\n");

        System.out.println("Mencoba update namaPetugas dengan string kosong (\"\"):");
        petugas1.setNamaPetugas("");

        System.out.println("Mencoba ubahJabatan dengan string spasi (\"   \"):");
        petugas1.ubahJabatan("   ");

        System.out.println("\n[Verifikasi Integritas Data Petugas 1 (Data Tidak Boleh Berubah)]");
        System.out.println("Nama Petugas: " + petugas1.getNamaPetugas() + " (Tetap)");
        System.out.println("Jabatan     : " + petugas1.getJabatan() + " (Tetap)");
        System.out.println();

        System.out.println("--- 3. Pengujian Nilai Invalid pada Transaksi 1 ---");
        System.out.println("Kondisi Sebelum Percobaan Update:");
        System.out.println("Jumlah: " + trx1.getJumlah() + " unit | Jenis: " + trx1.getJenisTransaksi() + "\n");

        System.out.println("Mencoba update jumlah transaksi dengan nilai negatif (-5):");
        trx1.setJumlah(-5);

        System.out.println("Mencoba update jenis transaksi dengan jenis tidak valid (\"Pinjam\"):");
        trx1.setJenisTransaksi("Pinjam");

        System.out.println("\n[Verifikasi Integritas Data Transaksi 1 (Data Tidak Boleh Berubah)]");
        System.out.println("Jumlah Transaksi: " + trx1.getJumlah() + " unit (Tetap)");
        System.out.println("Jenis Transaksi : " + trx1.getJenisTransaksi() + " (Tetap)");
        System.out.println();

        System.out.println("====================================================================");
        System.out.println("   SELURUH PENGUJIAN VALIDASI & ENKAPSULASI BERHASIL DILALUI!       ");
        System.out.println("====================================================================");
    }
}
