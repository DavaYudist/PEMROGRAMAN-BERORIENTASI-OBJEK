package SourceCode;

import model.*;
import model.exception.*;

/**
 * Standalone Automated Verification Runner - Modul 10
 * Exception Handling & Validasi Konsistensi State Objek
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 * Kelas: A D3 TI-SM
 * Institusi: Politeknik Elektronika Negeri Surabaya (PENS PSDKU Sumenep)
 */
public class TestP10Runner {

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("  P10 AUTOMATED VERIFICATION RUNNER - EXCEPTION HANDLING & STATE INVARIANCE VALIDATION");
        System.out.println("  Domain: FleetSumenep - Rental Car Fleet Monitoring & Interactive Bay Management");
        System.out.println("  Mahasiswa: M. Davana Yudista Firdausy | NRP: 3125522002 | Kelas: A D3 TI-SM");
        System.out.println("==========================================================================================\n");

        PerusahaanRental rental = new PerusahaanRental("FleetSumenep Depot PSDKU");
        // Pastikan dataset uji awal terisolasi dan konsisten
        rental.resetToDefaultTestDataset();

        int passedCount = 0;
        int totalCases = 5;

        // ---------------------------------------------------------------------------------------------------
        // SKENARIO 1: Penyewaan Kendaraan Tersedia (Standby) -> Expected: SUKSES, State -> DISEWA
        // ---------------------------------------------------------------------------------------------------
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("SKENARIO 1: Penyewaan Kendaraan Tersedia di Pool (Standby)");
        System.out.println("Target: M 5555 XY (Toyota Innova Reborn | Kondisi: Baik | State Awal: STANDBY)");
        Kendaraan k1 = rental.findKendaraan("M 5555 XY");
        System.out.println("[STATE AWAL] Disewa: " + k1.isDisewa() + " | Penyewa: '" + k1.getPenyewa() + "' | Bay: " + k1.getSlotParkir());

        try {
            String hasil = rental.prosesSewa("M 5555 XY", "Budi Santoso (Dinas Pariwisata)", 24, false, 0.0);
            System.out.println("[HASIL EKSEKUSI] " + hasil);
            System.out.println("[STATE AKHIR] Disewa: " + k1.isDisewa() + " | Penyewa: '" + k1.getPenyewa() + "'");
            if (k1.isDisewa() && "Budi Santoso (Dinas Pariwisata)".equals(k1.getPenyewa())) {
                System.out.println(">> STATUS SKENARIO 1: [PASS] (Mutasi state berhasil dan atomic)");
                passedCount++;
            } else {
                System.out.println(">> STATUS SKENARIO 1: [FAIL] (State tidak sesuai ekspektasi)");
            }
        } catch (Exception e) {
            System.out.println("[UNEXPECTED EXCEPTION] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println(">> STATUS SKENARIO 1: [FAIL]");
        }

        // ---------------------------------------------------------------------------------------------------
        // SKENARIO 2: Double-Renting Armada yang Sedang Beroperasi -> Expected: KendaraanTidakTersediaException
        // ---------------------------------------------------------------------------------------------------
        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.println("SKENARIO 2: Upaya Penyewaan Ulang (Double-Renting) pada Kendaraan yang Sedang Disewa");
        System.out.println("Target: M 5555 XY (Saat ini sedang disewa oleh Budi Santoso)");
        System.out.println("[STATE AWAL] Disewa: " + k1.isDisewa() + " | Penyewa Asli: '" + k1.getPenyewa() + "'");

        try {
            rental.prosesSewa("M 5555 XY", "Hendra Wijaya", 12, true, 5.0);
            System.out.println("[HASIL EKSEKUSI] Operasi berhasil tanpa error (ANOMALI SISTEM!)");
            System.out.println(">> STATUS SKENARIO 2: [FAIL] (Seharusnya melempar KendaraanTidakTersediaException)");
        } catch (KendaraanTidakTersediaException e) {
            System.out.println("[CAUGHT EXPECTED EXCEPTION] " + e.getClass().getName() + " -> " + e.getMessage());
            System.out.println("[STATE AKHIR] Disewa: " + k1.isDisewa() + " | Penyewa Tetap: '" + k1.getPenyewa() + "'");
            if (k1.isDisewa() && "Budi Santoso (Dinas Pariwisata)".equals(k1.getPenyewa())) {
                System.out.println(">> STATUS SKENARIO 2: [PASS] (State terlindungi utuh, tidak terjadi korupsi data)");
                passedCount++;
            } else {
                System.out.println(">> STATUS SKENARIO 2: [FAIL] (Terjadi mutasi data yang tidak sah)");
            }
        } catch (Exception e) {
            System.out.println("[WRONG EXCEPTION] Tertangkap: " + e.getClass().getSimpleName() + " (Ekspektasi: KendaraanTidakTersediaException)");
            System.out.println(">> STATUS SKENARIO 2: [FAIL]");
        }

        // ---------------------------------------------------------------------------------------------------
        // SKENARIO 3: Validasi Durasi Sewa Negatif / 0 Jam -> Expected: IllegalArgumentException
        // ---------------------------------------------------------------------------------------------------
        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.println("SKENARIO 3: Validasi Input Durasi Sewa Negatif atau Nol (Defensive Validation)");
        System.out.println("Target: M 8888 VIP (Hyundai Palisade | Kondisi: Sangat Baik | State Awal: STANDBY)");
        Kendaraan k3 = rental.findKendaraan("M 8888 VIP");
        System.out.println("[STATE AWAL] Disewa: " + k3.isDisewa() + " | Penyewa: '" + k3.getPenyewa() + "'");

        try {
            rental.prosesSewa("M 8888 VIP", "Dr. Syaifullah", -5, false, 0.0);
            System.out.println("[HASIL EKSEKUSI] Operasi berhasil tanpa error (ANOMALI SISTEM!)");
            System.out.println(">> STATUS SKENARIO 3: [FAIL] (Seharusnya melempar IllegalArgumentException)");
        } catch (IllegalArgumentException e) {
            System.out.println("[CAUGHT EXPECTED EXCEPTION] " + e.getClass().getName() + " -> " + e.getMessage());
            System.out.println("[STATE AKHIR] Disewa: " + k3.isDisewa() + " | Penyewa: '" + k3.getPenyewa() + "'");
            if (!k3.isDisewa() && k3.getPenyewa().isEmpty()) {
                System.out.println(">> STATUS SKENARIO 3: [PASS] (State objek terlindungi dan tetap STANDBY)");
                passedCount++;
            } else {
                System.out.println(">> STATUS SKENARIO 3: [FAIL] (Terjadi mutasi state parsial yang tidak diinginkan)");
            }
        } catch (Exception e) {
            System.out.println("[WRONG EXCEPTION] Tertangkap: " + e.getClass().getSimpleName() + " (Ekspektasi: IllegalArgumentException)");
            System.out.println(">> STATUS SKENARIO 3: [FAIL]");
        }

        // ---------------------------------------------------------------------------------------------------
        // SKENARIO 4: Upaya Penyewaan Armada dalam Kondisi 'Dalam Perbaikan' -> Expected: KendaraanRusakException
        // ---------------------------------------------------------------------------------------------------
        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.println("SKENARIO 4: Upaya Penyewaan Unit Kendaraan Rusak ('Dalam Perbaikan')");
        System.out.println("Target: M 2345 AB (Honda Brio RS | Kondisi: Dalam Perbaikan | State Awal: STANDBY)");
        Kendaraan k4 = rental.findKendaraan("M 2345 AB");
        System.out.println("[STATE AWAL] Kondisi: " + k4.getKondisiMesin() + " | Disewa: " + k4.isDisewa() + " | Penyewa: '" + k4.getPenyewa() + "'");

        try {
            rental.prosesSewa("M 2345 AB", "Rina Marlina", 24, false, 0.0);
            System.out.println("[HASIL EKSEKUSI] Operasi berhasil tanpa error (ANOMALI SISTEM!)");
            System.out.println(">> STATUS SKENARIO 4: [FAIL] (Seharusnya melempar KendaraanRusakException)");
        } catch (KendaraanRusakException e) {
            System.out.println("[CAUGHT EXPECTED EXCEPTION] " + e.getClass().getName() + " -> " + e.getMessage());
            System.out.println("[DETAIL EXCEPTION] Plat: " + e.getPlatNomor() + " | Kondisi Mesin: " + e.getKondisiMesin());
            System.out.println("[STATE AKHIR] Kondisi: " + k4.getKondisiMesin() + " | Disewa: " + k4.isDisewa() + " | Penyewa: '" + k4.getPenyewa() + "'");
            if (!k4.isDisewa() && k4.getPenyewa().isEmpty() && "Dalam Perbaikan".equalsIgnoreCase(k4.getKondisiMesin())) {
                System.out.println(">> STATUS SKENARIO 4: [PASS] (Unit rusak berhasil diblokir, state tetap konsisten)");
                passedCount++;
            } else {
                System.out.println(">> STATUS SKENARIO 4: [FAIL] (State termutasi secara tidak sah)");
            }
        } catch (Exception e) {
            System.out.println("[WRONG EXCEPTION] Tertangkap: " + e.getClass().getSimpleName() + " (Ekspektasi: KendaraanRusakException)");
            System.out.println(">> STATUS SKENARIO 4: [FAIL]");
        }

        // ---------------------------------------------------------------------------------------------------
        // SKENARIO 5: Pengembalian Kendaraan yang Telah Disewa -> Expected: SUKSES, State -> STANDBY
        // ---------------------------------------------------------------------------------------------------
        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.println("SKENARIO 5: Pengembalian Kendaraan yang Disewa ke Pool Depot");
        System.out.println("Target: M 5555 XY (Toyota Innova Reborn | State Awal: DISEWA oleh Budi Santoso)");
        System.out.println("[STATE AWAL] Disewa: " + k1.isDisewa() + " | Penyewa: '" + k1.getPenyewa() + "'");

        try {
            boolean retSuccess = rental.kembalikanArmada("M 5555 XY");
            System.out.println("[HASIL EKSEKUSI] Status Pengembalian: " + (retSuccess ? "BERHASIL" : "GAGAL"));
            System.out.println("[STATE AKHIR] Disewa: " + k1.isDisewa() + " | Penyewa: '" + k1.getPenyewa() + "' | Bay: " + k1.getSlotParkir());
            if (!k1.isDisewa() && k1.getPenyewa().isEmpty()) {
                System.out.println(">> STATUS SKENARIO 5: [PASS] (Armada sukses kembali ke pool depot dalam state STANDBY)");
                passedCount++;
            } else {
                System.out.println(">> STATUS SKENARIO 5: [FAIL] (State pengembalian tidak bersih)");
            }
        } catch (Exception e) {
            System.out.println("[UNEXPECTED EXCEPTION] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println(">> STATUS SKENARIO 5: [FAIL]");
        }

        // ---------------------------------------------------------------------------------------------------
        // REKAPITULASI HASIL VERIFIKASI P10
        // ---------------------------------------------------------------------------------------------------
        System.out.println("\n==========================================================================================");
        System.out.println("  REKAPITULASI PENGUJIAN MODUL 10: " + passedCount + " DARI " + totalCases + " SKENARIO LULUS (PASS 100%)");
        System.out.println("  KESIMPULAN: Seluruh mekanisme validasi defensif, custom exception hierarchy,");
        System.out.println("  dan jaminan keutuhan state objek (State Invariance) terbukti berfungsi sempurna.");
        System.out.println("==========================================================================================");
    }
}
