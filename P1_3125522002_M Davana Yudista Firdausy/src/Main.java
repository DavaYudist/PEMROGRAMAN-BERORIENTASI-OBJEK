public class Main {
    public static void main(String[] args) {
        // Membuat object Barang
        Barang item1 = new Barang();

        // Mengisi state/data
        item1.kodeBarang = "BRG-001";
        item1.namaBarang = "Buku Tulis Hardcover";
        item1.kategori = "Alat Tulis";
        item1.stok = 50;
        item1.hargaSatuan = 15000.0;

        // Memanggil behavior/method
        item1.tampilkanData();

        // Simulasi update stok
        item1.tambahStok(20);
        item1.tampilkanData();
    }
}
