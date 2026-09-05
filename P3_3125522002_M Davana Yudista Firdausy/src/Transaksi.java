package src;

public class Transaksi {
    // Encapsulation: Attributes dideklarasikan private
    private String idTransaksi;
    private String kodeBarang;
    private int jumlah;
    private String jenisTransaksi;

    // Parameterized constructor routing validatable values through setters
    public Transaksi(String idTransaksi, String kodeBarang, int jumlah, String jenisTransaksi) {
        this.idTransaksi = idTransaksi; // Read-only attribute
        this.kodeBarang = kodeBarang;   // Read-only attribute
        setJumlah(jumlah);
        setJenisTransaksi(jenisTransaksi);
    }

    // ================= GETTER METHODS =================
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getJenisTransaksi() {
        return jenisTransaksi;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    // idTransaksi dan kodeBarang bersifat Read-Only (tidak disediakan setter)

    public void setJumlah(int jumlah) {
        if (jumlah > 0) {
            this.jumlah = jumlah;
        } else {
            System.out.println("Error: Jumlah transaksi harus lebih dari 0! (Nilai input: " + jumlah + ")");
        }
    }

    public void setJenisTransaksi(String jenis) {
        if (jenis != null && (jenis.equalsIgnoreCase("Barang Masuk") || jenis.equalsIgnoreCase("Barang Keluar"))) {
            this.jenisTransaksi = jenis;
        } else {
            System.out.println("Error: Jenis transaksi tidak valid! Harus 'Barang Masuk' atau 'Barang Keluar'. (Nilai input: \"" + jenis + "\")");
        }
    }

    // ================= BUSINESS METHODS =================
    public void tampilkanDetail() {
        System.out.println("=== Detail Transaksi ===");
        System.out.println("ID Transaksi    : " + idTransaksi);
        System.out.println("Kode Barang     : " + kodeBarang);
        System.out.println("Jumlah          : " + jumlah + " unit");
        System.out.println("Jenis Transaksi : " + jenisTransaksi);
        System.out.println("----------------------------");
    }

    public String ringkasanTransaksi() {
        return "[" + idTransaksi + "] " + jenisTransaksi + " - Kode Barang: " + kodeBarang + " (Jumlah: " + jumlah + " unit)";
    }
}
