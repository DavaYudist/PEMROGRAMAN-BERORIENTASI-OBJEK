package src;

/**
 * Subclass Pemasok merepresentasikan data rekanan penyuplai barang inventaris toko.
 * Mengimplementasikan konsep Pewarisan (Inheritance):
 * Pemasok adalah spesialisasi dari Person (Pemasok IS-A Person).
 * Mewarisi atribut id, nama (nama PIC kontak), dan alamat dari Person,
 * serta menambahkan atribut spesifik korporasi seperti namaPerusahaan dan kategoriSuplai.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 1.0 (Modul 5 - Inheritance & Generalization)
 */
public class Pemasok extends Person {
    // Attributes spesifik subclass dideklarasikan private (Encapsulation)
    private String namaPerusahaan;
    private String kategoriSuplai;

    /**
     * Parameterized Constructor Pemasok.
     * Menggunakan super(...) untuk memanggil constructor superclass Person (Constructor Chaining).
     */
    public Pemasok(String idPemasok, String namaKontak, String alamat, String namaPerusahaan, String kategoriSuplai) {
        // Pemanggilan constructor superclass HARUS berada pada baris pertama
        super(idPemasok, namaKontak, alamat);

        // Trace pembuktian eksekusi subclass constructor
        System.out.println("  -> [Subclass Constructor] Pemasok dieksekusi untuk Perusahaan: " + namaPerusahaan);

        // Validasi dan routing atribut spesifik melalui setter
        setNamaPerusahaan(namaPerusahaan);
        setKategoriSuplai(kategoriSuplai);
    }

    // ================= GETTER METHODS =================
    public String getNamaPerusahaan() {
        return namaPerusahaan;
    }

    public String getKategoriSuplai() {
        return kategoriSuplai;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    public void setNamaPerusahaan(String namaPerusahaan) {
        if (namaPerusahaan != null && !namaPerusahaan.trim().isEmpty()) {
            this.namaPerusahaan = namaPerusahaan;
        } else {
            System.out.println("Error [Pemasok]: Nama perusahaan tidak boleh kosong!");
        }
    }

    public void setKategoriSuplai(String kategoriSuplai) {
        if (kategoriSuplai != null && !kategoriSuplai.trim().isEmpty()) {
            this.kategoriSuplai = kategoriSuplai;
        } else {
            System.out.println("Error [Pemasok]: Kategori suplai tidak boleh kosong!");
        }
    }

    // ================= CONVENIENCE METHODS =================
    public String getIdPemasok() {
        return getId();
    }

    public String getNamaKontak() {
        return getNama();
    }

    public void setNamaKontak(String namaKontak) {
        setNama(namaKontak);
    }

    // ================= OVERRIDDEN BUSINESS METHODS =================
    /**
     * Override method tampilkanProfil() dari superclass Person.
     * Memanggil super.tampilkanProfil() untuk menampilkan atribut warisan,
     * lalu menampilkan atribut terspesialisasi milik Pemasok.
     */
    @Override
    public void tampilkanProfil() {
        System.out.println("=== Profil Pemasok Inventaris ===");
        super.tampilkanProfil();
        System.out.println("Perusahaan   : " + namaPerusahaan);
        System.out.println("Kategori     : " + kategoriSuplai);
        System.out.println("---------------------------------");
    }
}
