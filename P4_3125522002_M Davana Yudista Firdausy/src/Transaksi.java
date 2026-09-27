package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Transaksi mengorkestrasi relasi antar objek:
 * 1. Asosiasi: Transaksi memiliki referensi ke Petugas penanggung jawab.
 * 2. Komposisi: Transaksi memiliki dan mengontrol siklus hidup DetailTransaksi.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 1.0 (Modul 4 - Relasi Objek)
 */
public class Transaksi {
    // Encapsulation: Attributes dideklarasikan private
    private String idTransaksi;                             // Read-only
    private String jenisTransaksi;                          // "Barang Masuk" atau "Barang Keluar"
    private Petugas petugas;                                // Asosiasi: Transaksi mengenal Petugas
    private List<DetailTransaksi> daftarDetail;             // Komposisi: Transaksi memiliki daftar DetailTransaksi

    // Constructor menerima idTransaksi, jenisTransaksi, dan objek Petugas
    public Transaksi(String idTransaksi, String jenisTransaksi, Petugas petugas) {
        this.idTransaksi = idTransaksi;
        this.daftarDetail = new ArrayList<>();
        setJenisTransaksi(jenisTransaksi);
        setPetugas(petugas);
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

    public double hitungTotalTransaksi() {
        double total = 0.0;
        for (DetailTransaksi detail : daftarDetail) {
            total += detail.getSubtotal();
        }
        return total;
    }

    public void tampilkanTransaksi() {
        System.out.println("================================================================================");
        System.out.println("                          DETAIL TRANSAKSI TOKO                                 ");
        System.out.println("================================================================================");
        System.out.println("ID Transaksi    : " + idTransaksi);
        System.out.println("Jenis Transaksi : " + (jenisTransaksi != null ? jenisTransaksi : "Tidak Valid"));
        if (petugas != null) {
            System.out.println("Petugas PIC     : " + petugas.getIdPetugas() + " - " + petugas.getNamaPetugas() + " (" + petugas.getJabatan() + ")");
        } else {
            System.out.println("Petugas PIC     : [Belum Ditentukan / Invalid]");
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
        System.out.printf("Total Biaya/Aset : Rp%,.2f%n", hitungTotalTransaksi());
        System.out.println("================================================================================\n");
    }
}
