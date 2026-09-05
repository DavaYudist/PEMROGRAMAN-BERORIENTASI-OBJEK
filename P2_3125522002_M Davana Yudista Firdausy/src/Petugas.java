package src;

public class Petugas {
    String idPetugas;
    String namaPetugas;
    String jabatan;

    // Explicit parameterized constructor initializing all attributes
    public Petugas(String idPetugas, String namaPetugas, String jabatan) {
        this.idPetugas = idPetugas;
        this.namaPetugas = namaPetugas;
        this.jabatan = jabatan;
    }

    // Method 1: Tanpa parameter, void
    public void tampilkanProfil() {
        System.out.println("=== Profil Petugas ===");
        System.out.println("ID Petugas   : " + idPetugas);
        System.out.println("Nama Petugas : " + namaPetugas);
        System.out.println("Jabatan      : " + jabatan);
        System.out.println("----------------------------");
    }

    // Method 2: Dengan parameter
    public void ubahJabatan(String jabatanBaru) {
        this.jabatan = jabatanBaru;
        System.out.println("Jabatan " + namaPetugas + " berhasil diubah menjadi: " + jabatanBaru);
    }

    // Method 3: Mengembalikan nilai (return value)
    public String getNamaPetugas() {
        return namaPetugas;
    }
}
