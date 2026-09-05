package src;

public class Transaksi {
    String idTransaksi;
    String kodeBarang;
    int jumlah;
    String jenisTransaksi;

    // Explicit parameterized constructor initializing all attributes
    public Transaksi(String idTransaksi, String kodeBarang, int jumlah, String jenisTransaksi) {
        this.idTransaksi = idTransaksi;
        this.kodeBarang = kodeBarang;
        this.jumlah = jumlah;
        this.jenisTransaksi = jenisTransaksi;
    }

    // Method 1: Tanpa parameter, void
    public void tampilkanDetail() {
        System.out.println("=== Detail Transaksi ===");
        System.out.println("ID Transaksi    : " + idTransaksi);
        System.out.println("Kode Barang     : " + kodeBarang);
        System.out.println("Jumlah          : " + jumlah + " unit");
        System.out.println("Jenis Transaksi : " + jenisTransaksi);
        System.out.println("----------------------------");
    }

    // Method 2: Dengan parameter
    public void setJenisTransaksi(String jenis) {
        this.jenisTransaksi = jenis;
        System.out.println("Jenis transaksi " + idTransaksi + " diperbarui menjadi: " + jenis);
    }

    // Method 3: Mengembalikan nilai (return value)
    public String ringkasanTransaksi() {
        return "[" + idTransaksi + "] " + jenisTransaksi + " - Kode Barang: " + kodeBarang + " (Jumlah: " + jumlah + " unit)";
    }
}
