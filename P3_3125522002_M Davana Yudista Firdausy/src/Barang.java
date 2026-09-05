package src;

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
    // kodeBarang bersifat Read-Only (tidak disediakan setter)

    public void setNamaBarang(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.namaBarang = nama;
        } else {
            System.out.println("Error: Nama barang tidak boleh kosong!");
        }
    }

    public void setKategori(String kat) {
        if (kat != null && !kat.trim().isEmpty()) {
            this.kategori = kat;
        } else {
            System.out.println("Error: Kategori tidak boleh kosong!");
        }
    }

    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        } else {
            System.out.println("Error: Stok tidak boleh negatif! (Nilai input: " + stok + ")");
        }
    }

    public void setHargaSatuan(double harga) {
        if (harga > 0) {
            this.hargaSatuan = harga;
        } else {
            System.out.println("Error: Harga satuan harus lebih dari 0! (Nilai input: Rp" + harga + ")");
        }
    }

    // ================= BUSINESS METHODS =================
    public void tampilkanData() {
        System.out.println("=== Data Barang ===");
        System.out.println("Kode Barang  : " + kodeBarang);
        System.out.println("Nama Barang  : " + namaBarang);
        System.out.println("Kategori     : " + kategori);
        System.out.println("Jumlah Stok  : " + stok + " unit");
        System.out.println("Harga Satuan : Rp" + hargaSatuan);
        System.out.println("----------------------------");
    }

    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
            System.out.println("Berhasil menambah " + jumlah + " unit ke " + namaBarang);
        } else {
            System.out.println("Error: Penambahan stok harus lebih dari 0! (Nilai input: " + jumlah + ")");
        }
    }

    public double hitungTotalNilai() {
        return stok * hargaSatuan;
    }
}
