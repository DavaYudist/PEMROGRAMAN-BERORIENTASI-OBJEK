public class Barang {
    String kodeBarang;
    String namaBarang;
    String kategori;
    int stok;
    double hargaSatuan;

    void tampilkanData() {
        System.out.println("=== Data Barang ===");
        System.out.println("Kode Barang  : " + kodeBarang);
        System.out.println("Nama Barang  : " + namaBarang);
        System.out.println("Kategori     : " + kategori);
        System.out.println("Jumlah Stok  : " + stok + " unit");
        System.out.println("Harga Satuan : Rp" + hargaSatuan);
        System.out.println("----------------------------");
    }

    void tambahStok(int jumlah) {
        stok += jumlah;
        System.out.println("Berhasil menambah " + jumlah + " unit ke " + namaBarang);
    }
}
