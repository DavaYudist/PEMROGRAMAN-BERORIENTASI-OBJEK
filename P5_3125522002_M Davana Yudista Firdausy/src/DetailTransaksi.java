package src;

/**
 * Class DetailTransaksi merepresentasikan satu baris detail item dalam transaksi.
 * Memiliki hubungan Komposisi (Composition) dengan Transaksi:
 * DetailTransaksi diciptakan dan dikelola langsung oleh objek Transaksi
 * serta tidak memiliki siklus hidup independen di luar Transaksi tersebut.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 1.0 (Modul 5 - Inheritance, Superclass & Subclass)
 */
public class DetailTransaksi {
    // Encapsulation: Attributes dideklarasikan private
    private Barang barang;
    private int subtotalJumlah;
    private double subtotalHarga;

    // Constructor dipanggil oleh Transaksi untuk menjamin lifecycle komposisi
    public DetailTransaksi(Barang barang, int subtotalJumlah) {
        if (barang == null) {
            System.out.println("Error [DetailTransaksi]: Objek barang tidak boleh null!");
        } else if (subtotalJumlah <= 0) {
            System.out.println("Error [DetailTransaksi]: Subtotal jumlah harus lebih dari 0! (Nilai input: " + subtotalJumlah + ")");
        } else {
            this.barang = barang;
            this.subtotalJumlah = subtotalJumlah;
            this.subtotalHarga = subtotalJumlah * barang.getHargaSatuan();
        }
    }

    // ================= GETTER METHODS =================
    public Barang getBarang() {
        return barang;
    }

    public int getSubtotalJumlah() {
        return subtotalJumlah;
    }

    public double getSubtotal() {
        return subtotalHarga;
    }

    public double getSubtotalHarga() {
        return subtotalHarga;
    }

    // ================= BUSINESS METHODS =================
    public void tampilkanDetail() {
        if (barang != null) {
            System.out.printf("  * [%-8s] %-24s | Qty: %-3d | @Rp%,11.2f | Subtotal: Rp%,12.2f%n",
                    barang.getKodeBarang(),
                    barang.getNamaBarang(),
                    subtotalJumlah,
                    barang.getHargaSatuan(),
                    subtotalHarga);
        } else {
            System.out.println("  * [Detail Transaksi Kosong / Tidak Valid]");
        }
    }
}
