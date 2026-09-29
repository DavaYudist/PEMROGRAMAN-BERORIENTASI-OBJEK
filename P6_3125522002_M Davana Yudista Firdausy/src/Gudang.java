package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Gudang merepresentasikan unit penyimpanan inventaris barang.
 * Mengimplementasikan hubungan Agregasi (Aggregation):
 * Gudang menampung objek-objek Barang yang sudah ada sebelumnya.
 * Objek Barang memiliki siklus hidup independen (loose coupling);
 * jika objek Gudang dikosongkan atau dihapus, objek-objek Barang tetap eksis.
 *
 * @author M. Davana Yudista Firana Yudista Firdausy (3125522002)
 * @version 2.0 (Modul 6 - Polymorphism, Overriding, Overloading, Dynamic Binding)
 */
public class Gudang {
    // Encapsulation: Attributes dideklarasikan private
    private String kodeGudang;                  // Read-only
    private String namaGudang;
    private List<Barang> daftarBarang;          // Agregasi: Menampung referensi objek Barang yang berdiri sendiri

    // Constructor menerima kodeGudang dan namaGudang, menginisialisasi list kosong
    public Gudang(String kodeGudang, String namaGudang) {
        this.kodeGudang = kodeGudang;
        setNamaGudang(namaGudang);
        this.daftarBarang = new ArrayList<>();
    }

    // ================= GETTER METHODS =================
    public String getKodeGudang() {
        return kodeGudang;
    }

    public String getNamaGudang() {
        return namaGudang;
    }

    public List<Barang> getDaftarBarang() {
        return daftarBarang;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    public void setNamaGudang(String namaGudang) {
        if (namaGudang != null && !namaGudang.trim().isEmpty()) {
            this.namaGudang = namaGudang;
        } else {
            System.out.println("Error [Gudang]: Nama gudang tidak boleh kosong!");
        }
    }

    // ================= BUSINESS METHODS =================
    /**
     * Menambahkan objek Barang yang sudah ada ke dalam daftar inventaris gudang.
     * Mengimplementasikan AGREGASI (Barang diciptakan di luar class Gudang).
     */
    public boolean tambahBarang(Barang barang) {
        if (barang != null) {
            daftarBarang.add(barang);
            System.out.println("Sukses [Gudang " + namaGudang + "]: Agregasi barang [" + barang.getKodeBarang() + "] " + barang.getNamaBarang() + " berhasil didaftarkan.");
            return true;
        } else {
            System.out.println("Error [Gudang]: Objek barang yang ditambahkan tidak boleh null!");
            return false;
        }
    }

    public void kosongkanGudang() {
        daftarBarang.clear();
        System.out.println("Pemberitahuan [Gudang]: Inventaris Gudang " + namaGudang + " telah dikosongkan. (Objek Barang tetap eksis independen).");
    }

    public double hitungTotalAsetGudang() {
        double total = 0.0;
        for (Barang b : daftarBarang) {
            total += b.hitungTotalNilai();
        }
        return total;
    }

    public int hitungTotalStokGudang() {
        int total = 0;
        for (Barang b : daftarBarang) {
            total += b.getStok();
        }
        return total;
    }

    public void tampilkanInventaris() {
        System.out.println("================================================================================");
        System.out.println("                     INVENTARIS GUDANG (AGREGASI OBJEK)                         ");
        System.out.println("================================================================================");
        System.out.println("Kode Gudang     : " + kodeGudang);
        System.out.println("Nama Gudang     : " + namaGudang);
        System.out.println("Jumlah Jenis Brg: " + daftarBarang.size() + " SKU");
        System.out.println("--------------------------------------------------------------------------------");
        if (daftarBarang.isEmpty()) {
            System.out.println("  (Gudang saat ini tidak memiliki barang tersimpan)");
        } else {
            System.out.printf("  %-10s | %-24s | %-15s | %-6s | %-15s | %-15s%n",
                    "KODE", "NAMA BARANG", "KATEGORI", "STOK", "HARGA SATUAN", "TOTAL NILAI");
            System.out.println("  ------------------------------------------------------------------------------");
            for (Barang b : daftarBarang) {
                System.out.printf("  %-10s | %-24s | %-15s | %-6d | Rp%,12.2f | Rp%,12.2f%n",
                        b.getKodeBarang(),
                        b.getNamaBarang(),
                        b.getKategori(),
                        b.getStok(),
                        b.getHargaSatuan(),
                        b.hitungTotalNilai());
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Total Unit Stok  : %d unit%n", hitungTotalStokGudang());
        System.out.printf("Total Nilai Aset : Rp%,.2f%n", hitungTotalAsetGudang());
        System.out.println("================================================================================\n");
    }
}
