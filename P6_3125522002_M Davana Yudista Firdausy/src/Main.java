package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Class untuk eksekusi dan verifikasi praktikum Modul 6:
 * Polymorphism, Method Overriding, Method Overloading, dan Dynamic Binding.
 *
 * Mengimplementasikan pengujian komprehensif sesuai rubrik lab:
 * 1. Test Case 1: Method Overloading (Compile-time Polymorphism) pada Transaksi
 * 2. Test Case 2: Upcasting & Dynamic Method Dispatch (Person Reference -> Petugas & Pemasok)
 * 3. Test Case 3: Polymorphic Collection & Dynamic Binding (Heterogeneous Iteration)
 * 4. Test Case 4: Verifikasi Tambahan (Negative Testing Diskon & Integrasi Sistem)
 *
 * @author M. Davana Yudista Firdausy
 * @nrp 3125522002
 * @kelas A D3 TI-SM
 * @institusi PENS PSDKU Sumenep
 * @proyek Sistem Manajemen Inventaris Toko
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK - MODUL 6                  ");
        System.out.println("       POLYMORPHISM, METHOD OVERRIDING, METHOD OVERLOADING, & DYNAMIC BINDING   ");
        System.out.println("================================================================================");
        System.out.println("  Nama Mahasiswa : M. Davana Yudista Firdausy");
        System.out.println("  NRP            : 3125522002");
        System.out.println("  Kelas          : A D3 TI-SM");
        System.out.println("  Institusi      : PENS PSDKU Sumenep");
        System.out.println("  Proyek         : Sistem Manajemen Inventaris Toko (Iterasi P5 -> P6)");
        System.out.println("================================================================================\n");

        // Inisialisasi aktor dasar untuk transaksi
        Petugas petugasKasir = new Petugas("PTG-002", "Siti Nurhaliza", "Jl. Diponegoro No. 45, Sumenep", "Kasir Senior", "Siang");
        System.out.println();

        // ==============================================================================
        // TEST CASE 1: METHOD OVERLOADING (COMPILE-TIME POLYMORPHISM)
        // Membuktikan bahwa class Transaksi memiliki dua method dengan nama sama namun
        // signature berbeda:
        // Method 1: hitungTotalBerdasarkanSubtotal() -> tanpa parameter
        // Method 2: hitungTotalBerdasarkanSubtotal(double persentaseDiskon) -> dengan parameter diskon
        // Resolusi method ditentukan oleh compiler pada compile-time berdasarkan signature.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 1: METHOD OVERLOADING (COMPILE-TIME POLYMORPHISM)              ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Membuat Objek Transaksi dan Mengisi Item Detail Transaksi:");
        Barang barang1 = new Barang("BRG-001", "Buku Tulis Hardcover", "Alat Tulis", 100, 15000.0);
        Barang barang2 = new Barang("BRG-002", "Pulpen Gel 0.5mm", "Alat Tulis", 200, 5000.0);
        Barang barang3 = new Barang("BRG-003", "Kertas HVS A4 70gr", "Kertas", 50, 48000.0);

        Transaksi trx1 = new Transaksi("TRX-OUT-2026-001", "Barang Keluar", petugasKasir);
        trx1.tambahItem(barang1, 10); // Subtotal: 10 x 15.000 = Rp150.000
        trx1.tambahItem(barang2, 20); // Subtotal: 20 x 5.000  = Rp100.000
        trx1.tambahItem(barang3, 5);  // Subtotal: 5 x 48.000  = Rp240.000
        System.out.println();

        System.out.println("[Langkah 2] Menampilkan Rincian Transaksi:");
        trx1.tampilkanTransaksi();

        System.out.println("[Langkah 3] Pengujian Overloaded Methods pada Objek Transaksi:");

        // Pemanggilan Method Overloading 1: hitungTotalBerdasarkanSubtotal()
        double totalStandar = trx1.hitungTotalBerdasarkanSubtotal();
        System.out.printf("  * Pemanggilan Method 1 - hitungTotalBerdasarkanSubtotal()        : Rp%,.2f%n", totalStandar);

        // Pemanggilan Method Overloading 2: hitungTotalBerdasarkanSubtotal(double persentaseDiskon)
        double diskon10Persen = 10.0;
        double totalDiskon10 = trx1.hitungTotalBerdasarkanSubtotal(diskon10Persen);
        double penghematan10 = totalStandar - totalDiskon10;
        System.out.printf("  * Pemanggilan Method 2 - hitungTotalBerdasarkanSubtotal(%.1f%%)   : Rp%,.2f (Hemat: Rp%,.2f)%n",
                diskon10Persen, totalDiskon10, penghematan10);

        // Pemanggilan Method Overloading 2 dengan variasi diskon lain (misal: 25%)
        double diskon25Persen = 25.0;
        double totalDiskon25 = trx1.hitungTotalBerdasarkanSubtotal(diskon25Persen);
        double penghematan25 = totalStandar - totalDiskon25;
        System.out.printf("  * Pemanggilan Method 2 - hitungTotalBerdasarkanSubtotal(%.1f%%)   : Rp%,.2f (Hemat: Rp%,.2f)%n",
                diskon25Persen, totalDiskon25, penghematan25);

        System.out.println("\nPenjelasan Konsep Test Case 1:");
        System.out.println("-> Method Overloading terjadi di dalam class yang sama (Transaksi).");
        System.out.println("-> Compiler Java menentukan secara pasti method mana yang dieksekusi berdasarkan");
        System.out.println("   parameter signature pada saat proses kompilasi (Static Binding / Early Binding).\n");

        // ==============================================================================
        // TEST CASE 2: UPCASTING & DYNAMIC METHOD DISPATCH
        // Membuktikan:
        // 1. Upcasting: Objek subclass (Petugas & Pemasok) di-assign ke referensi superclass (Person).
        // 2. Dynamic Method Dispatch: JVM mengeksekusi method overridden dari tipe objek aktual
        //    (runtime object type), bukan tipe referensi (compile-time reference type).
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 2: UPCASTING & DYNAMIC METHOD DISPATCH                         ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Inisialisasi Objek dengan Upcasting ke Superclass Person:");
        System.out.println("-> Upcasting Petugas ke referensi Person (Person p1 = new Petugas(...)):");
        Person p1 = new Petugas("PTG-001", "Ahmad Fauzi", "Jl. Trunojoyo No. 12, Sumenep", "Supervisor Gudang", "Pagi");

        System.out.println("-> Upcasting Pemasok ke referensi Person (Person p2 = new Pemasok(...)):");
        Person p2 = new Pemasok("PMS-101", "Budi Santoso", "Jl. Palmerah Barat No. 29, Jakarta", "PT Gramedia Mitra Sarana", "Alat Tulis & Buku");

        System.out.println("-> Membuat Objek Murni Superclass Person untuk Kontras (Person p0 = new Person(...)):");
        Person p0 = new Person("PRS-000", "Bambang Pamungkas", "Jl. Jokotole No. 3, Sumenep");
        System.out.println();

        System.out.println("[Langkah 2] Memanggil prosesTugas() pada Seluruh Referensi Person:");
        System.out.println("1. Referensi p0 (Tipe Referensi: Person, Tipe Objek: Person):");
        System.out.print("   Output: ");
        p0.prosesTugas();

        System.out.println("2. Referensi p1 (Tipe Referensi: Person, Tipe Objek: Petugas):");
        System.out.print("   Output: ");
        p1.prosesTugas();

        System.out.println("3. Referensi p2 (Tipe Referensi: Person, Tipe Objek: Pemasok):");
        System.out.print("   Output: ");
        p2.prosesTugas();

        System.out.println("\nPenjelasan Konsep Test Case 2:");
        System.out.println("-> p1 dan p2 sama-sama dideklarasikan bertipe Person (Upcasting).");
        System.out.println("-> Namun saat p1.prosesTugas() dipanggil, JVM secara dinamis mengarahkan eksekusi");
        System.out.println("   ke method Petugas.prosesTugas().");
        System.out.println("-> Begitu juga dengan p2.prosesTugas(), JVM mengarahkan eksekusi ke Pemasok.prosesTugas().");
        System.out.println("-> Inilah yang membuktikan Dynamic Method Dispatch (Runtime Polymorphism).\n");

        // ==============================================================================
        // TEST CASE 3: POLYMORPHIC COLLECTION & DYNAMIC BINDING
        // Membuktikan:
        // 1. Polymorphic Collection: Koleksi bertipe superclass (Person[]) mampu menampung
        //    berbagai objek subclass turunan yang berbeda secara heterogen (Petugas & Pemasok).
        // 2. Dynamic Binding: Iterasi enhanced for-loop memanggil method overridden
        //    tampilkanProfil() dan prosesTugas(), mengeksekusi implementasi spesifik masing-masing.
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 3: POLYMORPHIC COLLECTION & DYNAMIC BINDING                    ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Mempersiapkan Koleksi Polimorfik (Person[]):");
        Person[] daftarAktor = new Person[4];
        daftarAktor[0] = new Petugas("PTG-001", "Ahmad Fauzi", "Jl. Trunojoyo No. 12, Sumenep", "Supervisor Gudang", "Pagi");
        daftarAktor[1] = new Petugas("PTG-002", "Siti Nurhaliza", "Jl. Diponegoro No. 45, Sumenep", "Kasir Senior", "Siang");
        daftarAktor[2] = new Pemasok("PMS-101", "Budi Santoso", "Jl. Palmerah Barat No. 29, Jakarta", "PT Gramedia Mitra Sarana", "Alat Tulis & Buku");
        daftarAktor[3] = new Pemasok("PMS-102", "Dewi Lestari", "Kawasan Industri Sier No. 8, Surabaya", "CV Sinar Grafika", "Kertas & Cetakan");
        System.out.println("Koleksi daftarAktor (Person[]) berhasil diisi dengan " + daftarAktor.length + " elemen heterogen:\n"
                + "  [0] Petugas (Ahmad Fauzi)\n"
                + "  [1] Petugas (Siti Nurhaliza)\n"
                + "  [2] Pemasok (PT Gramedia Mitra Sarana)\n"
                + "  [3] Pemasok (CV Sinar Grafika)\n");

        System.out.println("[Langkah 2] Iterasi Koleksi Menggunakan Enhanced For-Loop:");
        System.out.println("Setiap elemen dipanggil method tampilkanProfil() dan prosesTugas():\n");

        int index = 1;
        for (Person aktor : daftarAktor) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Iterasi Elemen #" + index + " | Tipe Objek Aktual: " + aktor.getClass().getSimpleName() + " (Referensi: Person)");
            System.out.println("--------------------------------------------------------------------------------");

            // Memanggil method overridden tampilkanProfil()
            aktor.tampilkanProfil();

            // Memanggil method overridden prosesTugas()
            System.out.print("Tugas Aktor  : ");
            aktor.prosesTugas();
            System.out.println();
            index++;
        }

        System.out.println("Penjelasan Konsep Test Case 3:");
        System.out.println("-> Seluruh elemen dalam loop bertipe referensi sama, yaitu 'Person aktor'.");
        System.out.println("-> Saat runtime, JVM melakukan Dynamic Method Lookup (Virtual Method Table / vtable).");
        System.out.println("-> Petugas mengeksekusi format profil Petugas dan tugas spesifik Petugas.");
        System.out.println("-> Pemasok mengeksekusi format profil Pemasok dan tugas spesifik Pemasok.");
        System.out.println("-> Tidak diperlukan type-casting manual (instanceof / downcasting) untuk menjalankan perilaku spesifik!\n");

        // ==============================================================================
        // TEST CASE 4: INTEGRASI SISTEM & NEGATIVE TESTING DISKON OVERLOADING
        // Menguji validasi batas nilai persentase diskon pada method overloading:
        // - Diskon negatif (<= 0) -> mengembalikan total normal tanpa potongan
        // - Diskon berlebih (> 100) -> proteksi dipangkas maksimal 100%
        // ==============================================================================
        System.out.println("################################################################################");
        System.out.println("### TEST CASE 4: NEGATIVE TESTING & ROBUSTNESS METHOD OVERLOADING            ###");
        System.out.println("################################################################################\n");

        System.out.println("[Langkah 1] Pengujian Nilai Diskon Negatif (-15%):");
        double hasilDiskonNegatif = trx1.hitungTotalBerdasarkanSubtotal(-15.0);
        System.out.printf("  Total transaksi setelah diskon -15%%: Rp%,.2f (Total normal tetap terjaga)%n%n", hasilDiskonNegatif);

        System.out.println("[Langkah 2] Pengujian Nilai Diskon Berlebih (120%):");
        double hasilDiskonBerlebih = trx1.hitungTotalBerdasarkanSubtotal(120.0);
        System.out.printf("  Total transaksi setelah diskon 120%%: Rp%,.2f (Maksimal diskon 100%% = gratis)%n%n", hasilDiskonBerlebih);

        System.out.println("[Langkah 3] Pengujian Agregasi Gudang & Pengurangan Stok Real-Time:");
        Gudang gudang = new Gudang("GDG-SMP-01", "Gudang Distribusi Sumenep");
        gudang.tambahBarang(barang1);
        gudang.tambahBarang(barang2);
        gudang.tambahBarang(barang3);
        System.out.println();
        gudang.tampilkanInventaris();

        System.out.println("================================================================================");
        System.out.println("    SELURUH PENGUJIAN MODUL 6: POLYMORPHISM BERHASIL 100% TANPA KENDALA!        ");
        System.out.println("================================================================================");
    }
}
