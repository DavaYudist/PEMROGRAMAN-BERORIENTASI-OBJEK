package SourceCode;

import model.DatabaseManager;
import model.Kendaraan;
import model.PerusahaanRental;

public class TestMySQLPersistence {
    public static void main(String[] args) throws Exception {
        System.out.println(">>> MEMULAI TEST PERSISTENSI MYSQL LARAGON <<<");

        // 1. Inisialisasi Database
        DatabaseManager db = DatabaseManager.getInstance();
        db.initializeDatabase();
        System.out.println("[TEST 1] DatabaseManager.initializeDatabase() SELESAI.");

        // 2. Load dari Database ke PerusahaanRental
        PerusahaanRental rental = new PerusahaanRental("BPP Wilayah Sumenep & Madura");
        System.out.println("[TEST 2] Total Armada Terload: " + rental.getArmada().size());
        System.out.println("[TEST 2] Total Sedang Disewa: " + rental.getArmadaDisewa().size());
        System.out.println("[TEST 2] Total Bay Parkir   : " + rental.getDaftarSlotKapasitas().size());

        assert rental.getArmada().size() >= 5 : "Armada harus >= 5";
        assert rental.getArmadaDisewa().size() >= 2 : "Armada disewa harus >= 2";

        // 3. Edit Spesifikasi Kendaraan
        String testPlat = "M 5555 XY"; // Innova
        Kendaraan innova = rental.findKendaraan(testPlat);
        System.out.println("[TEST 3] Kondisi awal " + testPlat + ": " + innova.getKondisiMesin() + ", Catatan: " + innova.getCatatanFisik());

        boolean editOk = rental.editKendaraan(testPlat, 950000.0, "Sangat Baik", "Baru selesai servis berkala & poles bodi");
        System.out.println("[TEST 3] Hasil editKendaraan: " + editOk);
        assert editOk : "Edit harus berhasil";

        // 4. Sewa Mobil yang Standby
        // Innova saat ini Standby
        String receipt = rental.prosesSewa(testPlat, "Bupati Sumenep (Kunjungan Kerja)", 48, true, 15.0);
        System.out.println("[TEST 4] Transaksi Sewa Innova:\n" + receipt);
        assert receipt.startsWith("SUKSES") : "Sewa harus sukses";

        // 5. Buat Instance Baru untuk Menyimulasikan Restart Aplikasi dan Validasi Persistensi
        System.out.println("\n--- Menyimulasikan Restart Aplikasi (Reload dari MySQL) ---");
        PerusahaanRental reloaded = new PerusahaanRental("BPP Wilayah Sumenep & Madura");
        Kendaraan innovaReloaded = reloaded.findKendaraan(testPlat);
        
        System.out.println("[TEST 5] Status Innova setelah restart: " + (innovaReloaded.isDisewa() ? "DISEWA" : "STANDBY"));
        System.out.println("[TEST 5] Penyewa Innova: " + innovaReloaded.getPenyewa());
        System.out.println("[TEST 5] Tarif Harian  : Rp " + innovaReloaded.getTarifPerHari());
        System.out.println("[TEST 5] Kondisi Mesin : " + innovaReloaded.getKondisiMesin());
        System.out.println("[TEST 5] Catatan Fisik : " + innovaReloaded.getCatatanFisik());
        System.out.println("[TEST 5] Dengan Supir  : " + innovaReloaded.isDenganSupir());

        assert innovaReloaded.isDisewa() : "Innova harus berstatus DISEWA setelah restart";
        assert "Bupati Sumenep (Kunjungan Kerja)".equals(innovaReloaded.getPenyewa()) : "Penyewa harus persisten";
        assert innovaReloaded.getTarifPerHari() == 950000.0 : "Tarif harus 950000";
        assert "Sangat Baik".equals(innovaReloaded.getKondisiMesin()) : "Kondisi harus Sangat Baik";
        assert innovaReloaded.isDenganSupir() : "Layanan supir harus true";

        // 6. Kembalikan Mobil dan Verifikasi Persistensi Kembali
        System.out.println("\n--- Menguji Kembalikan Mobil dan Validasi Penghapusan Sewa Aktif ---");
        reloaded.kembalikanArmada(innovaReloaded);
        System.out.println("[TEST 6] Status Innova setelah dikembalikan: " + (innovaReloaded.isDisewa() ? "DISEWA" : "STANDBY"));

        PerusahaanRental reloaded2 = new PerusahaanRental("BPP Wilayah Sumenep & Madura");
        Kendaraan innovaReloaded2 = reloaded2.findKendaraan(testPlat);
        System.out.println("[TEST 6] Status Innova di DB setelah reload ke-2: " + (innovaReloaded2.isDisewa() ? "DISEWA" : "STANDBY"));
        assert !innovaReloaded2.isDisewa() : "Innova harus STANDBY di database";
        assert "".equals(innovaReloaded2.getPenyewa()) : "Penyewa harus kosong di database";

        System.out.println("\n>>> SELURUH UJI PERSISTENSI MYSQL LARAGON BERHASIL (100% PASS) <<<");
    }
}
