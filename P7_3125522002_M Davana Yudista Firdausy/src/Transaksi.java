package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Transaksi mengorkestrasi relasi multi-objek dalam arsitektur OOP:
 * 1. Asosiasi: Transaksi berasosiasi dengan Petugas (PIC internal) dan Pemasok (rekanan suplai).
 * 2. Komposisi: Transaksi memiliki dan mengontrol siklus hidup DetailTransaksi secara eksklusif.
 *
 * Pada Modul 7, Transaksi mengimplementasikan interface DapatDiaudit sebagai wujud
 * pemenuhan kontrak audit operasional dan pencatatan transaksi barang inventaris toko.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 3.0 (Modul 7 - Abstract Class, Abstract Method, and Interface)
 */
public class Transaksi implements DapatDiaudit {
    // Encapsulation: Attributes dideklarasikan private
    private String idTransaksi;                             // Read-only
    private String jenisTransaksi;                          // "Barang Masuk" atau "Barang Keluar"
    private Petugas petugas;                                // Asosiasi: Transaksi mengenal Petugas (Subclass Person)
    private Pemasok pemasok;                                // Asosiasi: Transaksi mengenal Pemasok (Subclass Person)
    private List<DetailTransaksi> daftarDetail;             // Komposisi: Transaksi memiliki daftar DetailTransaksi

    /**
     * Constructor Transaksi standar (Asosiasi dengan Petugas penanggung jawab).
     */
    public Transaksi(String idTransaksi, String jenisTransaksi, Petugas petugas) {
        this(idTransaksi, jenisTransaksi, petugas, null);
    }

    /**
     * Overloaded Constructor Transaksi (Asosiasi dengan Petugas dan Pemasok rekanan).
     * Sangat relevan untuk transaksi "Barang Masuk" dari vendor/supplier.
     */
    public Transaksi(String idTransaksi, String jenisTransaksi, Petugas petugas, Pemasok pemasok) {
        this.idTransaksi = idTransaksi;
        this.daftarDetail = new ArrayList<>();
        setJenisTransaksi(jenisTransaksi);
        setPetugas(petugas);
        setPemasok(pemasok);
    }

    // ================= GETTER METHODS =================
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getJenisTransaksi() {
        return jenisTransaksi;
    }

    public Petugas getPetugas() {
        return petugas;
    }

    public Pemasok getPemasok() {
        return pemasok;
    }

    public List<DetailTransaksi> getDaftarDetail() {
        return daftarDetail;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    public void setJenisTransaksi(String jenis) {
        if (jenis != null && (jenis.equalsIgnoreCase("Barang Masuk") || jenis.equalsIgnoreCase("Barang Keluar"))) {
            if (jenis.equalsIgnoreCase("Barang Masuk")) {
                this.jenisTransaksi = "Barang Masuk";
            } else {
                this.jenisTransaksi = "Barang Keluar";
            }
        } else {
            System.out.println("Error [Transaksi]: Jenis transaksi tidak valid! Harus 'Barang Masuk' atau 'Barang Keluar'. (Nilai input: \"" + jenis + "\")");
        }
    }

    public void setPetugas(Petugas petugas) {
        if (petugas != null) {
            this.petugas = petugas;
        } else {
            System.out.println("Error [Transaksi]: Petugas penanggung jawab tidak boleh null!");
        }
    }

    public void setPemasok(Pemasok pemasok) {
        this.pemasok = pemasok; // Pemasok bersifat opsional (dapat null untuk transaksi kasir/keluar)
    }

    // ================= BUSINESS METHODS =================
    /**
     * Menambahkan item ke dalam transaksi.
     * Mengimplementasikan KOMPOSISI: DetailTransaksi diinstansiasi di dalam method ini.
     * Mengatur mutasi stok pada objek Barang (tambahStok atau kurangiStok).
     */
    public boolean tambahItem(Barang barang, int jumlah) {
        if (barang == null) {
            System.out.println("Error [Transaksi]: Objek barang tidak boleh null!");
            return false;
        }
        if (jumlah <= 0) {
            System.out.println("Error [Transaksi]: Jumlah item harus lebih dari 0! (Nilai input: " + jumlah + ")");
            return false;
        }
        if (this.jenisTransaksi == null) {
            System.out.println("Error [Transaksi]: Tidak dapat menambah item, jenis transaksi belum valid!");
            return false;
        }

        boolean stokBerhasilDimutasi = false;
        if (this.jenisTransaksi.equals("Barang Masuk")) {
            stokBerhasilDimutasi = barang.tambahStok(jumlah);
        } else if (this.jenisTransaksi.equals("Barang Keluar")) {
            stokBerhasilDimutasi = barang.kurangiStok(jumlah);
        }

        if (stokBerhasilDimutasi) {
            // Komposisi: DetailTransaksi dibuat secara internal oleh Transaksi
            DetailTransaksi detail = new DetailTransaksi(barang, jumlah);
            this.daftarDetail.add(detail);
            System.out.println("Sukses [Transaksi " + idTransaksi + "]: Berhasil mencatat " + jumlah + " unit [" + barang.getNamaBarang() + "] ke dalam detail transaksi.");
            return true;
        } else {
            System.out.println("Gagal [Transaksi " + idTransaksi + "]: Item [" + barang.getNamaBarang() + "] batal dimasukkan karena mutasi stok gagal.");
            return false;
        }
    }

    // ================= METHOD OVERLOADING (COMPILE-TIME POLYMORPHISM) =================
    /**
     * Method Overloading - Versi 1:
     * Menghitung total transaksi secara standar berdasarkan penjumlahan seluruh subtotal item.
     *
     * @return Total biaya transaksi tanpa diskon.
     */
    public double hitungTotalBerdasarkanSubtotal() {
        double total = 0.0;
        for (DetailTransaksi detail : daftarDetail) {
            total += detail.getSubtotal();
        }
        return total;
    }

    /**
     * Method Overloading - Versi 2:
     * Menghitung total transaksi dengan potongan persentase diskon promo / mitra.
     *
     * @param persentaseDiskon Besar diskon dalam persen (misal: 10.0 untuk diskon 10%).
     * @return Total biaya transaksi setelah dikurangi diskon.
     */
    public double hitungTotalBerdasarkanSubtotal(double persentaseDiskon) {
        double totalAwal = hitungTotalBerdasarkanSubtotal();
        if (persentaseDiskon <= 0.0) {
            return totalAwal;
        }
        if (persentaseDiskon > 100.0) {
            System.out.println("Peringatan [Transaksi]: Persentase diskon (" + persentaseDiskon + "%) melebihi 100%! Maksimal diskon 100% diterapkan.");
            persentaseDiskon = 100.0;
        }
        double potongan = totalAwal * (persentaseDiskon / 100.0);
        return totalAwal - potongan;
    }

    /**
     * Backward compatibility helper untuk memanggil total standar.
     */
    public double hitungTotalTransaksi() {
        return hitungTotalBerdasarkanSubtotal();
    }

    public void tampilkanTransaksi() {
        System.out.println("================================================================================");
        System.out.println("                          DETAIL TRANSAKSI TOKO                                 ");
        System.out.println("================================================================================");
        System.out.println("ID Transaksi    : " + idTransaksi);
        System.out.println("Jenis Transaksi : " + (jenisTransaksi != null ? jenisTransaksi : "Tidak Valid"));
        if (petugas != null) {
            System.out.println("Petugas PIC     : " + petugas.getId() + " - " + petugas.getNama() + " (" + petugas.getJabatan() + " | Shift: " + petugas.getShiftKerja() + ")");
        } else {
            System.out.println("Petugas PIC     : [Belum Ditentukan / Invalid]");
        }
        if (pemasok != null) {
            System.out.println("Pemasok Rekanan : " + pemasok.getId() + " - " + pemasok.getNamaPerusahaan() + " (PIC: " + pemasok.getNama() + " | Suplai: " + pemasok.getKategoriSuplai() + ")");
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Rincian Item Transaksi (Komposisi):");
        if (daftarDetail.isEmpty()) {
            System.out.println("  (Belum ada item transaksi yang tercatat)");
        } else {
            for (DetailTransaksi detail : daftarDetail) {
                detail.tampilkanDetail();
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Total Macam Item : %d SKU%n", daftarDetail.size());
        System.out.printf("Total Subtotal   : Rp%,.2f%n", hitungTotalBerdasarkanSubtotal());
        System.out.println("================================================================================\n");
    }

    // ================= INTERFACE IMPLEMENTATION (DapatDiaudit) =================
    /**
     * Implementasi kontrak interface DapatDiaudit.
     * Mengembalikan rincian ID transaksi, jenis transaksi, total item yang diproses,
     * serta ringkasan nilai keuangan untuk kepentingan audit berkala.
     */
    @Override
    public String jalankanAudit() {
        int totalItemDiproses = 0;
        for (DetailTransaksi detail : daftarDetail) {
            totalItemDiproses += detail.getSubtotalJumlah();
        }

        return String.format("[AUDIT TRANSAKSI] ID: %s | Jenis: %-13s | Total Diproses: %3d unit (%d SKU) | Total Nilai: Rp%,.2f",
                idTransaksi, jenisTransaksi, totalItemDiproses, daftarDetail.size(), hitungTotalBerdasarkanSubtotal());
    }
}
