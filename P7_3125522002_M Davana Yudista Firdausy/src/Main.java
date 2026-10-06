package src;

/**
 * Main Class untuk eksekusi dan verifikasi praktikum Modul 7:
 * Abstract Class, Abstract Method, and Interface.
 *
 * Mengimplementasikan 4 skenario pengujian utama:
 * 1. Scenario 1: Object Instantiation (Concrete Subclasses vs Abstract Class)
 * 2. Scenario 2: Abstract Method via Superclass Reference (Dynamic Binding)
 * 3. Scenario 3: Interface Method via Interface Reference (Contract Realization)
 * 4. Scenario 4: Polymorphic Collections (Array of Person & Array of DapatDiaudit)
 *
 * @author M. Davana Yudista Firdausy
 * @nrp 3125522002
 * @kelas A D3 TI-SM
 * @institusi PENS PSDKU Sumenep
 * @proyek Sistem Manajemen Inventaris Toko (Iterasi P6 -> P7)
 */
public class Main {
    public static void main(String[] args) {
        // ==============================================================================
        // 1. HEADER: IDENTITAS MAHASISWA & JUDUL MODUL 7
        // ==============================================================================
        System.out.println("================================================================================");
        System.out.println("            PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK - MODUL 7                  ");
        System.out.println("                ABSTRACT CLASS, ABSTRACT METHOD, AND INTERFACE                  ");
        System.out.println("================================================================================");
        System.out.println("  Nama Mahasiswa : M. Davana Yudista Firdausy");
        System.out.println("  NRP            : 3125522002");
        System.out.println("  Kelas          : A D3 TI-SM");
        System.out.println("  Institusi      : PENS PSDKU Sumenep");
        System.out.println("  Topik          : Abstraksi & Kontrak Perilaku Sistem Inventaris Toko");
        System.out.println("================================================================================\n");

        // ==============================================================================
        // SCENARIO 1: OBJECT INSTANTIATION (CONCRETE SUBCLASSES)
        // Instansiasi langsung objek Petugas, Pemasok, Barang, dan Transaksi.
        // Menunjukkan bahwa concrete class berhasil diinstansiasi, sedangkan class Person
        // yang kini berstatus 'abstract' terlindungi dari instansiasi langsung secara ilegal.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### SCENARIO 1: OBJECT INSTANTIATION (CONCRETE SUBCLASSES)                   ###");
        System.out.println("################################################################################\n");

        System.out.println("[Step 1.1] Menginstansiasi Objek Concrete Subclass Petugas:");
        Petugas petugas1 = new Petugas("PTG-001", "Ahmad Fauzi", "Jl. Trunojoyo No. 12, Sumenep", "Supervisor Gudang", "Pagi");

        System.out.println("\n[Step 1.2] Menginstansiasi Objek Concrete Subclass Pemasok:");
        Pemasok pemasok1 = new Pemasok("PMS-101", "Budi Santoso", "Jl. Palmerah Barat No. 29, Jakarta", "PT Gramedia Mitra Sarana", "Alat Tulis & Buku");

        System.out.println("\n[Step 1.3] Menginstansiasi Objek Concrete Class Barang:");
        Barang barang1 = new Barang("BRG-001", "Buku Tulis Hardcover", "Alat Tulis", 120, 15000.0);
        System.out.println("  -> [Concrete Instantiation] Objek Barang berhasil dibuat: " + barang1.getNamaBarang());

        System.out.println("\n[Step 1.4] Menginstansiasi Objek Concrete Class Transaksi:");
        Transaksi trx1 = new Transaksi("TRX-OUT-2026-001", "Barang Keluar", petugas1, pemasok1);
        trx1.tambahItem(barang1, 20);
        System.out.println("  -> [Concrete Instantiation] Objek Transaksi berhasil dibuat: " + trx1.getIdTransaksi());

        System.out.println("\n[Analisis & Verifikasi Scenario 1]");
        System.out.println("[SUCCESS] Keempat objek concrete (Petugas, Pemasok, Barang, Transaksi) berhasil diinstansiasi.");
        System.out.println("[ENFORCEMENT] Perhatikan bahwa instruksi: 'Person p = new Person(...);' kini DITOLAK oleh compiler.");
        System.out.println("              Error Compiler: 'Person is abstract; cannot be instantiated'.");
        System.out.println("              Hal ini membuktikan Abstract Class berhasil mengamankan integritas domain model.\n");

        // ==============================================================================
        // SCENARIO 2: ABSTRACT METHOD VIA SUPERCLASS REFERENCE
        // Upcasting objek Petugas & Pemasok ke referensi superclass Person.
        // Memanggil abstract method prosesTugas() untuk membuktikan Dynamic Binding
        // (Dynamic Method Dispatch) mengeksekusi body konkret milik subclass saat runtime.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### SCENARIO 2: ABSTRACT METHOD VIA SUPERCLASS REFERENCE                     ###");
        System.out.println("################################################################################\n");

        System.out.println("[Step 2.1] Upcasting Petugas ke Referensi Person (Person p1 = new Petugas(...)):");
        Person p1 = new Petugas("PTG-002", "Siti Nurhaliza", "Jl. Diponegoro No. 45, Sumenep", "Kasir Senior", "Siang");

        System.out.println("\n[Step 2.2] Upcasting Pemasok ke Referensi Person (Person p2 = new Pemasok(...)):");
        Person p2 = new Pemasok("PMS-102", "Dewi Lestari", "Kawasan Industri Sier No. 8, Surabaya", "CV Sinar Grafika", "Kertas & Cetakan");

        System.out.println("\n[Step 2.3] Memanggil Abstract Method prosesTugas() Melalui Referensi Person:");
        System.out.println("-> Pemanggilan via Referensi Person p1 (Objek Aktual: Petugas):");
        System.out.print("   Hasil Eksekusi: ");
        p1.prosesTugas();

        System.out.println("\n-> Pemanggilan via Referensi Person p2 (Objek Aktual: Pemasok):");
        System.out.print("   Hasil Eksekusi: ");
        p2.prosesTugas();

        System.out.println("\n[Analisis & Verifikasi Scenario 2]");
        System.out.println("[DYNAMIC BINDING] Pada waktu kompilasi (compile-time), compiler hanya memeriksa bahwa");
        System.out.println("                  tipe Person memiliki deklarasi method 'public abstract void prosesTugas()'.");
        System.out.println("                  Pada waktu eksekusi (runtime), JVM melalui Virtual Method Table (vtable)");
        System.out.println("                  secara otomatis melompat ke implementasi spesifik milik Petugas dan Pemasok.\n");

        // ==============================================================================
        // SCENARIO 3: INTERFACE METHOD VIA INTERFACE REFERENCE
        // Menggunakan tipe referensi Interface DapatDiaudit untuk menunjuk objek Barang
        // dan Transaksi.
        // Memanggil jalankanAudit() untuk membuktikan realisasi kontrak interface
        // lintas kelas yang tidak memiliki hubungan inheritance hierarkis.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### SCENARIO 3: INTERFACE METHOD VIA INTERFACE REFERENCE                     ###");
        System.out.println("################################################################################\n");

        // Objek barang dengan stok sedikit untuk membuktikan evaluasi status audit KRITIS
        Barang barangKritis = new Barang("BRG-004", "Spidol Whiteboard Hitam", "Alat Tulis", 6, 8500.0);
        Barang barangAman = new Barang("BRG-003", "Kertas HVS A4 70gr", "Kertas", 50, 48000.0);

        System.out.println("[Step 3.1] Polimorfisme Interface: DapatDiaudit d1 = new Barang(...):");
        DapatDiaudit d1 = barangKritis;
        System.out.println("  -> Referensi DapatDiaudit d1 berhasil di-assign objek Barang (" + barangKritis.getNamaBarang() + ")");

        System.out.println("\n[Step 3.2] Polimorfisme Interface: DapatDiaudit d2 = new Transaksi(...):");
        Transaksi trxMasuk = new Transaksi("TRX-IN-2026-002", "Barang Masuk", petugas1, pemasok1);
        trxMasuk.tambahItem(barangAman, 40);
        DapatDiaudit d2 = trxMasuk;
        System.out.println("  -> Referensi DapatDiaudit d2 berhasil di-assign objek Transaksi (" + trxMasuk.getIdTransaksi() + ")");

        System.out.println("\n[Step 3.3] Eksekusi jalankanAudit() Melalui Referensi Interface DapatDiaudit:");
        System.out.println("1. d1.jalankanAudit() [Barang - Mendeteksi Stok Kritis]:");
        System.out.println("   " + d1.jalankanAudit());

        System.out.println("\n2. d2.jalankanAudit() [Transaksi - Rekap Aktivitas Mutasi]:");
        System.out.println("   " + d2.jalankanAudit());

        System.out.println("\n[Analisis & Verifikasi Scenario 3]");
        System.out.println("[INTERFACE CONTRACT] Class Barang dan Transaksi TIDAK saling mewarisi (berbeda silsilah class).");
        System.out.println("                     Namun melalui Interface DapatDiaudit, keduanya tunduk pada satu kontrak perilaku.");
        System.out.println("                     Hal ini memungkinkan loose coupling dan standarisasi fitur audit sistem.\n");

        // ==============================================================================
        // SCENARIO 4: POLYMORPHIC COLLECTIONS (ARRAY / LIST)
        // 1. Koleksi Person[] menampung objek heterogen Petugas dan Pemasok.
        // 2. Koleksi DapatDiaudit[] menampung objek heterogen Barang dan Transaksi.
        // Dilakukan iterasi loop untuk memanggil method kontrak secara seragam.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### SCENARIO 4: POLYMORPHIC COLLECTIONS (ARRAY / LIST)                       ###");
        System.out.println("################################################################################\n");

        System.out.println("[Step 4.1] Pengujian Koleksi Polimorfik Abstract Class (Person[]):");
        Person[] daftarPerson = new Person[] {
            petugas1,
            p1,
            pemasok1,
            p2
        };

        System.out.println("Iterasi Koleksi Person[] (Ukuran: " + daftarPerson.length + " elemen):");
        int noPerson = 1;
        for (Person p : daftarPerson) {
            System.out.println("  ------------------------------------------------------------------------------");
            System.out.println("  [" + noPerson + "] Tipe Runtime: " + p.getClass().getSimpleName() + " | ID: " + p.getId() + " | Nama: " + p.getNama());
            System.out.print("      Eksekusi prosesTugas(): ");
            p.prosesTugas();
            noPerson++;
        }
        System.out.println("  ------------------------------------------------------------------------------\n");

        System.out.println("[Step 4.2] Pengujian Koleksi Polimorfik Interface (DapatDiaudit[]):");
        DapatDiaudit[] daftarAudit = new DapatDiaudit[] {
            barang1,
            barangKritis,
            barangAman,
            trx1,
            trxMasuk
        };

        System.out.println("Iterasi Koleksi DapatDiaudit[] (Ukuran: " + daftarAudit.length + " entitas terverifikasi):");
        int noAudit = 1;
        for (DapatDiaudit auditee : daftarAudit) {
            System.out.println("  [" + noAudit + "] " + auditee.jalankanAudit());
            noAudit++;
        }

        System.out.println("\n[Analisis & Verifikasi Scenario 4]");
        System.out.println("[ABSTRACTION POWER] Dengan Polymorphic Collection, satu blok looping sederhana dapat");
        System.out.println("                    mengelola puluhan bahkan ratusan entitas berbeda secara elegan tanpa");
        System.out.println("                    perlu melakukan branching manual (if-else / switch / instanceof).\n");

        System.out.println("================================================================================");
        System.out.println("    SELURUH 4 SKENARIO MODUL 7 BERHASIL DIEKSEKUSI 100% DENGAN SUKSES!          ");
        System.out.println("================================================================================");
    }
}
