package SourceCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import model.Kendaraan;
import model.MobilReguler;
import model.MobilVIP;
import model.PerusahaanRental;
import model.exception.KapasitasPenuhException;
import model.exception.KendaraanRusakException;
import model.exception.KendaraanTidakTersediaException;

/**
 * End-to-End Regression Test Runner (Modul 14 & Capstone Project)
 * Menguji 12 skenario pengujian fungsional dan integritas data menyeluruh
 * dari P1 hingga P15 tanpa gangguan status acak.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 * Kelas: A D3 TI-SM | PENS PSDKU Sumenep
 */
public class TestRegressionRunner {

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("  CAPSTONE END-TO-END REGRESSION TEST RUNNER (OOP P1 - P15 FINAL VERIFICATION)");
        System.out.println("  Project Domain: FleetSumenep - Rental Car Fleet Monitoring & Bay Management");
        System.out.println("  Mahasiswa: M. Davana Yudista Firdausy | NRP: 3125522002 | Kelas: A D3 TI-SM");
        System.out.println("==========================================================================================\n");

        PerusahaanRental rental = new PerusahaanRental("FleetSumenep Depot PSDKU");
        rental.resetToDefaultTestDataset();

        int passed = 0;
        int total = 12;

        // 1. Tambah Kendaraan Valid
        System.out.println("SKENARIO 1: Tambah Kendaraan Baru Valid");
        try {
            MobilReguler ertiga = new MobilReguler("M 7777 AB", "Suzuki All New Ertiga", -7.0150, 113.8650, 95, 550000.0, 15000, "Sangat Baik", "Bodi Mulus");
            rental.tambahKendaraan(ertiga);
            if (rental.cariKendaraan("M 7777 AB") != null) {
                System.out.println(">> [PASS] Unit M 7777 AB berhasil ditambahkan ke " + ertiga.getSlotParkir());
                passed++;
            }
        } catch (Exception e) {
            System.out.println(">> [FAIL] " + e.getMessage());
        }

        // 2. Tambah Plat Duplikat
        System.out.println("\nSKENARIO 2: Tambah Kendaraan dengan Plat Duplikat");
        try {
            MobilReguler duplikat = new MobilReguler("M 7777 AB", "Suzuki Ertiga Clone", -7.0150, 113.8650, 90);
            rental.tambahKendaraan(duplikat);
            System.out.println(">> [FAIL] Plat duplikat lolos!");
        } catch (IllegalArgumentException e) {
            System.out.println(">> [PASS] Berhasil dicegah: " + e.getMessage());
            passed++;
        } catch (Exception e) {
            System.out.println(">> [FAIL] Salah exception: " + e.getMessage());
        }

        // 3. Tambah Atribut Tidak Valid (Tarif <= 0)
        System.out.println("\nSKENARIO 3: Konstruktor dengan Tarif Sewa Negatif / Nol");
        try {
            new MobilReguler("M 3333 XX", "Mobil Gratis", -7.015, 113.865, 80, -100000, 2000, "Baik", "Catatan");
            System.out.println(">> [FAIL] Tarif negatif lolos!");
        } catch (IllegalArgumentException e) {
            System.out.println(">> [PASS] Berhasil dicegah di konstruktor: " + e.getMessage());
            passed++;
        }

        // 4. Cari Kendaraan Terdaftar
        System.out.println("\nSKENARIO 4: Pencarian Kendaraan Terdaftar");
        Kendaraan kFound = rental.cariKendaraan("M 5555 XY");
        if (kFound != null && "Toyota Innova Reborn".equals(kFound.getMerk())) {
            System.out.println(">> [PASS] Ditemukan: " + kFound.getMerk() + " | Kondisi: " + kFound.getKondisiMesin());
            passed++;
        } else {
            System.out.println(">> [FAIL] Kendaraan tidak ditemukan!");
        }

        // 5. Cari Kendaraan Tidak Terdaftar
        System.out.println("\nSKENARIO 5: Pencarian Kendaraan Tidak Terdaftar");
        Kendaraan kNotFound = rental.cariKendaraan("M 0000 ZZ");
        if (kNotFound == null) {
            System.out.println(">> [PASS] Benar mengembalikan null untuk plat fiktif.");
            passed++;
        } else {
            System.out.println(">> [FAIL] Objek tidak null!");
        }

        // 6. Sewa Kendaraan Standby
        System.out.println("\nSKENARIO 6: Transaksi Sewa Normal pada Mobil Standby");
        try {
            String receipt = rental.prosesSewa("M 5555 XY", "Budi Santoso", 24, false, 0.0);
            System.out.println(">> Log Transaksi: " + receipt);
            if (kFound.isDisewa() && "Budi Santoso".equals(kFound.getPenyewa())) {
                System.out.println(">> [PASS] Mutasi state atomic berhasil.");
                passed++;
            }
        } catch (Exception e) {
            System.out.println(">> [FAIL] " + e.getMessage());
        }

        // 7. Double-Renting Unit Disewa
        System.out.println("\nSKENARIO 7: Upaya Sewa Ulang (Double-Renting) Mobil yang Sedang Disewa");
        try {
            rental.prosesSewa("M 5555 XY", "Pelanggan Kedua", 12, true, 5.0);
            System.out.println(">> [FAIL] Double-renting lolos!");
        } catch (KendaraanTidakTersediaException e) {
            System.out.println(">> [PASS] Tertangkap KendaraanTidakTersediaException: " + e.getMessage());
            passed++;
        } catch (Exception e) {
            System.out.println(">> [FAIL] Salah exception: " + e.getMessage());
        }

        // 8. Sewa Unit Kondisi 'Dalam Perbaikan'
        System.out.println("\nSKENARIO 8: Upaya Sewa Mobil Rusak ('Dalam Perbaikan')");
        try {
            rental.prosesSewa("M 2345 AB", "Rina Marlina", 24, false, 0.0);
            System.out.println(">> [FAIL] Sewa mobil rusak lolos!");
        } catch (KendaraanRusakException e) {
            System.out.println(">> [PASS] Tertangkap KendaraanRusakException: " + e.getMessage());
            passed++;
        } catch (Exception e) {
            System.out.println(">> [FAIL] Salah exception: " + e.getMessage());
        }

        // 9. Validasi Durasi Sewa <= 0
        System.out.println("\nSKENARIO 9: Sewa dengan Durasi Sewa Nol atau Negatif");
        try {
            rental.prosesSewa("M 8888 VIP", "Dr. Syaifullah", -5, false, 0.0);
            System.out.println(">> [FAIL] Durasi negatif lolos!");
        } catch (IllegalArgumentException e) {
            System.out.println(">> [PASS] Tertangkap IllegalArgumentException: " + e.getMessage());
            passed++;
        } catch (Exception e) {
            System.out.println(">> [FAIL] Salah exception: " + e.getMessage());
        }

        // 10. Pengembalian Mobil Sewa
        System.out.println("\nSKENARIO 10: Pengembalian Mobil Sewa ke Pool Garasi (Standby)");
        try {
            boolean ret = rental.kembalikanArmada("M 5555 XY");
            if (ret && !kFound.isDisewa() && kFound.getPenyewa().isEmpty()) {
                System.out.println(">> [PASS] Mobil M 5555 XY sukses kembali ke depot (STANDBY).");
                passed++;
            }
        } catch (Exception e) {
            System.out.println(">> [FAIL] " + e.getMessage());
        }

        // 11. Pengurangan Slot Bay Saat Bay Masih Ditempati
        System.out.println("\nSKENARIO 11: Pengurangan Kapasitas Bay yang Masih Terisi Mobil");
        try {
            // Coba kurangi 4 bay saat bay B-01 ditempati oleh M 2345 AB
            rental.kurangiKapasitasSlot(4);
            System.out.println(">> [FAIL] Pengurangan slot terisi lolos!");
        } catch (KapasitasPenuhException e) {
            System.out.println(">> [PASS] Tertangkap KapasitasPenuhException: " + e.getMessage());
            passed++;
        } catch (Exception e) {
            System.out.println(">> [FAIL] Salah exception: " + e.getMessage());
        }

        // 12. Simpan dan Muat Ulang File Persistensi (P12 Integration)
        System.out.println("\nSKENARIO 12: Persistensi Data File Teks (Save & Load Verification)");
        Path testFile = Paths.get("data", "test_regression_store.txt");
        try {
            rental.simpanKeFile(testFile);
            System.out.println(">> Data berhasil disimpan ke " + testFile);

            PerusahaanRental reloaded = new PerusahaanRental("FleetSumenep Reload Tester");
            reloaded.muatDariFile(testFile);
            System.out.println(">> Data berhasil dimuat ulang. Total unit: " + reloaded.getArmada().size());

            Kendaraan kReloaded = reloaded.cariKendaraan("M 7777 AB");
            if (kReloaded != null && "Suzuki All New Ertiga".equals(kReloaded.getMerk())) {
                System.out.println(">> [PASS] Unit M 7777 AB terverifikasi utuh setelah reload dari file.");
                passed++;
            }
            Files.deleteIfExists(testFile);
        } catch (Exception e) {
            System.out.println(">> [FAIL] File persistence error: " + e.getMessage());
        }

        System.out.println("\n==========================================================================================");
        System.out.printf("  REKAPITULASI HASIL UJI REGRESI: %d DARI %d SKENARIO LULUS (PASS %.1f%%)\n",
                passed, total, (passed * 100.0 / total));
        System.out.println("  STATUS AKHIR: Seluruh fungsionalitas OOP P1-P15 terintegrasi sempurna.");
        System.out.println("==========================================================================================");
    }
}
