package src;

/**
 * Class Barang merepresentasikan entitas barang inventaris toko.
 * Mengimplementasikan prinsip enkapsulasi penuh, validasi input,
 * serta metode mutasi stok untuk mendukung agregasi dan komposisi.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 1.0 (Modul 4 - Relasi Objek)
 */
public class Barang {
    // Encapsulation: Attributes dideklarasikan private
    private String kodeBarang;
    private String namaBarang;
    private String kategori;
    private int stok;
    private double hargaSatuan;

    // Parameterized constructor routing validatable values through setters
    public Barang(String kodeBarang, String namaBarang, String kategori, int stok, double hargaSatuan) {
        this.kodeBarang = kodeBarang; // Read-only attribute
        setNamaBarang(namaBarang);
        setKategori(kategori);
        setStok(stok);
        setHargaSatuan(hargaSatuan);
    }

    // ================= GETTER METHODS =================
    public String getKodeBarang() {
        return kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public int getStok() {
        return stok;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    // kodeBarang bersifat Read-Only (tidak disediakan setter publik)

    public void setNamaBarang(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.namaBarang = nama;
        } else {
            System.out.println("Error [Barang]: Nama barang tidak boleh kosong!");
        }
    }

    public void setKategori(String kat) {
        if (kat != null && !kat.trim().isEmpty()) {
            this.kategori = kat;
        } else {
            System.out.println("Error [Barang]: Kategori tidak boleh kosong!");
        }
    }

    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        } else {
            System.out.println("Error [Barang]: Stok tidak boleh negatif! (Nilai input: " + stok + ")");
        }
    }

    public void setHargaSatuan(double harga) {
        if (harga > 0) {
            this.hargaSatuan = harga;
        } else {
            System.out.println("Error [Barang]: Harga satuan harus lebih dari 0! (Nilai input: Rp" + harga + ")");
        }
    }

    // ================= BUSINESS METHODS =================
    public boolean tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
            System.out.println("Berhasil [Barang]: Menambah " + jumlah + " unit ke [" + kodeBarang + "] " + namaBarang + " (Total stok: " + stok + ")");
            return true;
        } else {
            System.out.println("Error [Barang]: Penambahan stok harus lebih dari 0! (Nilai input: " + jumlah + ")");
            return false;
        }
    }

    public boolean kurangiStok(int jumlah) {
        if (jumlah <= 0) {
            System.out.println("Error [Barang]: Pengurangan stok harus lebih dari 0! (Nilai input: " + jumlah + ")");
            return false;
        }
        if (jumlah > this.stok) {
            System.out.println("Error [Barang]: Stok " + namaBarang + " tidak mencukupi! (Stok saat ini: " + stok + ", diminta: " + jumlah + ")");
            return false;
        }
        this.stok -= jumlah;
        System.out.println("Berhasil [Barang]: Mengurangi " + jumlah + " unit dari [" + kodeBarang + "] " + namaBarang + " (Sisa stok: " + stok + ")");
        return true;
    }

    public double hitungTotalNilai() {
        return stok * hargaSatuan;
    }

    public void tampilkanData() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Kode Barang  : " + kodeBarang);
        System.out.println("Nama Barang  : " + namaBarang);
        System.out.println("Kategori     : " + kategori);
        System.out.println("Jumlah Stok  : " + stok + " unit");
        System.out.printf("Harga Satuan : Rp%,.2f%n", hargaSatuan);
        System.out.printf("Total Nilai  : Rp%,.2f%n", hitungTotalNilai());
        System.out.println("--------------------------------------------------------------------------------");
    }
}
