package src;

/**
 * Subclass Petugas merepresentasikan data staf/karyawan toko.
 * Mengimplementasikan konsep Pewarisan (Inheritance):
 * Petugas adalah spesialisasi konkret dari abstract class Person (Petugas IS-A Person).
 * Mewarisi atribut id, nama, dan alamat dari Person, serta menambahkan
 * atribut spesifik seperti jabatan dan shiftKerja.
 *
 * Pada Modul 7, Petugas bertindak sebagai Concrete Subclass yang mengimplementasikan
 * kontrak perilaku abstract method prosesTugas() dari Person menggunakan anotasi @Override.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 3.0 (Modul 7 - Abstract Class, Abstract Method, and Interface)
 */
public class Petugas extends Person {
    // Attributes spesifik subclass dideklarasikan private (Encapsulation)
    private String jabatan;
    private String shiftKerja;

    /**
     * Parameterized Constructor Petugas.
     * Menggunakan super(...) untuk memanggil constructor superclass Person (Constructor Chaining).
     */
    public Petugas(String idPetugas, String namaPetugas, String alamat, String jabatan, String shiftKerja) {
        // Pemanggilan constructor superclass HARUS berada pada baris pertama
        super(idPetugas, namaPetugas, alamat);

        // Trace pembuktian eksekusi subclass constructor
        System.out.println("  -> [Subclass Constructor] Petugas dieksekusi untuk Jabatan: " + jabatan);

        // Validasi dan routing atribut spesifik melalui setter
        setJabatan(jabatan);
        setShiftKerja(shiftKerja);
    }

    // ================= GETTER METHODS =================
    public String getJabatan() {
        return jabatan;
    }

    public String getShiftKerja() {
        return shiftKerja;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    public void setJabatan(String jabatan) {
        if (jabatan != null && !jabatan.trim().isEmpty()) {
            this.jabatan = jabatan;
        } else {
            System.out.println("Error [Petugas]: Jabatan petugas tidak boleh kosong!");
        }
    }

    public void setShiftKerja(String shiftKerja) {
        if (shiftKerja != null && !shiftKerja.trim().isEmpty()) {
            this.shiftKerja = shiftKerja;
        } else {
            System.out.println("Error [Petugas]: Shift kerja tidak boleh kosong!");
        }
    }

    // ================= BACKWARD COMPATIBILITY HELPERS =================
    // Mendukung pemanggilan berbasis versi P4/P5 agar integrasi berjalan mulus
    public String getIdPetugas() {
        return getId();
    }

    public String getNamaPetugas() {
        return getNama();
    }

    public void setNamaPetugas(String namaPetugas) {
        setNama(namaPetugas);
    }

    public void ubahJabatan(String jabatanBaru) {
        if (jabatanBaru != null && !jabatanBaru.trim().isEmpty()) {
            setJabatan(jabatanBaru);
            System.out.println("Jabatan " + getNama() + " berhasil diubah menjadi: " + jabatanBaru);
        } else {
            System.out.println("Error [Petugas]: Pembaruan gagal, jabatan baru tidak boleh kosong!");
        }
    }

    // ================= OVERRIDDEN BUSINESS METHODS =================
    /**
     * Override method tampilkanProfil() dari superclass Person.
     * Memanggil super.tampilkanProfil() untuk menampilkan atribut warisan,
     * lalu menampilkan atribut terspesialisasi milik Petugas.
     */
    @Override
    public void tampilkanProfil() {
        System.out.println("=== Profil Petugas Toko ===");
        super.tampilkanProfil();
        System.out.println("Jabatan      : " + jabatan);
        System.out.println("Shift Kerja  : " + shiftKerja);
        System.out.println("----------------------------");
    }

    /**
     * Override method prosesTugas() dari superclass Person.
     * Mengimplementasikan tugas spesifik karyawan/staf toko.
     * Mendemonstrasikan Dynamic Binding saat dipanggil melalui referensi Person.
     */
    @Override
    public void prosesTugas() {
        System.out.println("[Petugas] Melayani pelanggan dan memverifikasi stok gudang.");
    }
}
