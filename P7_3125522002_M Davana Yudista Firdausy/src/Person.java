package src;

/**
 * Abstract Superclass Person merepresentasikan entitas dasar manusia (generalisasi)
 * dalam ekosistem Sistem Manajemen Inventaris Toko.
 * Menurunkan atribut umum (id, nama, alamat) kepada subclass terspesialisasi
 * seperti Petugas dan Pemasok dengan enkapsulasi penuh dan validasi data.
 *
 * Pada Modul 7, Person dideklarasikan sebagai 'abstract class' untuk mencegah
 * instansiasi langsung (mencegah pembuatan objek Person anonim/generik yang tidak masuk akal).
 * Method prosesTugas() diubah menjadi 'abstract method' guna mewajibkan setiap subclass
 * konkret (Petugas & Pemasok) menyediakan implementasi spesifiknya masing-masing.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 3.0 (Modul 7 - Abstract Class, Abstract Method, and Interface)
 */
public abstract class Person {
    // Encapsulation: Attributes dideklarasikan private
    private String id;      // Read-only setelah inisialisasi pada constructor
    private String nama;
    private String alamat;

    /**
     * Constructor Superclass Person.
     * Mengimplementasikan trace message pembuktian Constructor Chaining.
     * Catatan: Meskipun abstract class tidak dapat diinstansiasi langsung, constructor
     * tetap berfungsi untuk menginisialisasi state atribut saat dipanggil oleh subclass (super).
     */
    public Person(String id, String nama, String alamat) {
        // Trace pembuktian eksekusi superclass constructor
        System.out.println("  -> [Superclass Constructor] Person dieksekusi untuk ID: " + id);

        // Validasi identitas read-only
        if (id != null && !id.trim().isEmpty()) {
            this.id = id;
        } else {
            this.id = "ID-UNKNOWN";
            System.out.println("Error [Person]: ID tidak valid atau kosong! Diberikan default: ID-UNKNOWN");
        }

        // Routing atribut yang dapat diubah melalui validated setters
        setNama(nama);
        setAlamat(alamat);
    }

    // ================= GETTER METHODS =================
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    // ================= SETTER METHODS WITH VALIDATION =================
    // id bersifat Read-Only (tidak disediakan setter publik demi integritas identitas)

    public void setNama(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.nama = nama;
        } else {
            System.out.println("Error [Person]: Nama tidak boleh kosong!");
        }
    }

    public void setAlamat(String alamat) {
        if (alamat != null && !alamat.trim().isEmpty()) {
            this.alamat = alamat;
        } else {
            System.out.println("Error [Person]: Alamat tidak boleh kosong!");
        }
    }

    // ================= BUSINESS METHODS & ABSTRACTION =================
    /**
     * Menampilkan profil dasar dari entitas Person.
     * Bersifat konkret (memiliki body) dan dapat dipakai atau diperluas oleh subclass.
     */
    public void tampilkanProfil() {
        System.out.println("ID           : " + id);
        System.out.println("Nama         : " + nama);
        System.out.println("Alamat       : " + alamat);
    }

    /**
     * Abstract Method prosesTugas().
     * Tidak memiliki implementasi (body) pada class Person.
     * Mewajibkan seluruh subclass konkret (seperti Petugas dan Pemasok) untuk mengimplementasikan
     * logika tugas operasional spesifik masing-masing.
     */
    public abstract void prosesTugas();
}
