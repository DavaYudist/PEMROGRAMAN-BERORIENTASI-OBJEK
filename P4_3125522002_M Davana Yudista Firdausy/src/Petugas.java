package src;

/**
 * Class Petugas merepresentasikan data staf/karyawan yang mengelola toko.
 * Memiliki hubungan Asosiasi dengan Transaksi (Petugas memproses Transaksi).
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 1.0 (Modul 4 - Relasi Objek)
 */
public class Petugas {
    // Encapsulation: Attributes dideklarasikan private
    private String idPetugas;
    private String namaPetugas;
    private String jabatan;

    // Parameterized constructor routing validatable values through setters
    public Petugas(String idPetugas, String namaPetugas, String jabatan) {
        this.idPetugas = idPetugas; // Read-only attribute
        setNamaPetugas(namaPetugas);
        setJabatan(jabatan);
    }

    // ================= GETTER METHODS =================
    public String getIdPetugas() {
        return idPetugas;
    }

    public String getNamaPetugas() {
        return namaPetugas;
    }

    public String getJabatan() {
        return jabatan;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    // idPetugas bersifat Read-Only (tidak disediakan setter publik)

    public void setNamaPetugas(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.namaPetugas = nama;
        } else {
            System.out.println("Error [Petugas]: Nama petugas tidak boleh kosong!");
        }
    }

    public void setJabatan(String jabatan) {
        if (jabatan != null && !jabatan.trim().isEmpty()) {
            this.jabatan = jabatan;
        } else {
            System.out.println("Error [Petugas]: Jabatan petugas tidak boleh kosong!");
        }
    }

    // ================= BUSINESS METHODS =================
    public void tampilkanProfil() {
        System.out.println("=== Profil Petugas ===");
        System.out.println("ID Petugas   : " + idPetugas);
        System.out.println("Nama Petugas : " + namaPetugas);
        System.out.println("Jabatan      : " + jabatan);
        System.out.println("----------------------------");
    }

    public void ubahJabatan(String jabatanBaru) {
        if (jabatanBaru != null && !jabatanBaru.trim().isEmpty()) {
            setJabatan(jabatanBaru);
            System.out.println("Jabatan " + namaPetugas + " berhasil diubah menjadi: " + jabatanBaru);
        } else {
            System.out.println("Error [Petugas]: Pembaruan gagal, jabatan baru tidak boleh kosong!");
        }
    }
}
