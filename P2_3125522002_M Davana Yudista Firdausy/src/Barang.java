package src;

public class Barang {
    String kodeBarang;
    String namaBarang;
    String kategori;
    int stok;
    double hargaSatuan;

    // Explicit parameterized constructor initializing all attributes
    public Barang(String kodeBarang, String namaBarang, String kategori, int stok, double hargaSatuan) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.stok = stok;
        this.hargaSatuan = hargaSatuan;
    }

    // Method 1: Tanpa parameter, void
    public void tampilkanData() {
        System.out.println("=== Data Barang ===");
        System.out.println("Kode Barang  : " + kodeBarang);
        System.out.println("Nama Barang  : " + namaBarang);
        System.out.println("Kategori     : " + kategori);
        System.out.println("Jumlah Stok  : " + stok + " unit");
        System.out.println("Harga Satuan : Rp" + hargaSatuan);
        System.out.println("----------------------------");
    }

    // Method 2: Dengan parameter
    public void tambahStok(int jumlah) {
        this.stok += jumlah;
        System.out.println("Berhasil menambah " + jumlah + " unit ke " + namaBarang);
    }

    // Method 3: Mengembalikan nilai (return value)
    public double hitungTotalNilai() {
        return stok * hargaSatuan;
    }
}
