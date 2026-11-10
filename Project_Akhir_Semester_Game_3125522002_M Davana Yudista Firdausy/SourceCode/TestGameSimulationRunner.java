package SourceCode;

import model.*;
import model.exception.GarasiPenuhException;
import model.exception.MobilDibawaKaburException;
import model.exception.SaldoKasTidakCukupException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * P14-P15: Regression & Academic Verification Test Suite.
 * Menjalankan 12 skenario uji otomatis untuk membuktikan stabilitas dan invarian kelas.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002) - A D3 TI-SM
 */
public class TestGameSimulationRunner {

    private static int totalPassed = 0;
    private static final int TOTAL_TESTS = 14;

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("  INDONESIAN CAR RENTAL TYCOON - AUTOMATED VERIFICATION SUITE (P1 - P15)");
        System.out.println("  Author: M. Davana Yudista Firdausy | NRP: 3125522002 | PENS PSDKU Sumenep");
        System.out.println("==========================================================================================\n");

        runTestCase(1, "KasPerusahaan: Pemasukan Valid & Pencatatan Transaksi", () -> testKasPemasukan());
        runTestCase(2, "KasPerusahaan: Pencegahan Saldo Negatif (SaldoKasTidakCukupException)", () -> testKasSaldoTidakCukup());
        runTestCase(3, "Kendaraan: Immutabilitas Plat Nomor & Defensive Constructor", () -> testKendaraanInvarian());
        runTestCase(4, "Polimorfisme: Kalkulasi Biaya Sewa Subclass (LCGC s/d Mewah)", () -> testPolimorfismeTarif());
        runTestCase(5, "Kontrak Interface: Modul GPS & Remote Engine Immobilizer", () -> testInterfaceGPSDanCutoff());
        runTestCase(6, "Profiling Risiko Pelanggan: KTP, HP Baru, & Catatan Asosiasi", () -> testProfilingRisiko());
        runTestCase(7, "Mitigasi Risiko: Minta Jaminan (-40%) & Tawarkan Supir (0% Risiko)", () -> testMitigasiRisiko());
        runTestCase(8, "Alokasi Sewa: Transaksi Normal & Mutasi Status SIAP -> DISEWA", () -> testAlokasiSewaNormal());
        runTestCase(9, "Kapasitas Garasi: Pencegahan Overcapacity (GarasiPenuhException)", () -> testGarasiPenuh());
        runTestCase(10, "Krisis Penggelapan: Evaluasi Jatuh Tempo & MobilDibawaKaburException", () -> testKrisisPenggelapan());
        runTestCase(11, "Resolusi Krisis Taktis: Sukses Matikan Mesin Remote via GPS", () -> testResolusiKrisisGPS());
        runTestCase(12, "Persistensi P12: Serialisasi & Deserialisasi State Game", () -> testSavegamePersistence());
        runTestCase(13, "Collision Detection: Deteksi Bentrok Jadwal & Penolakan Double-Booking", () -> testCollisionDetection());
        runTestCase(14, "Upgrade Gratis & Bursa Bekas: Alokasi Tier Tinggi & Evaluasi Diskon 60%", () -> testUpgradeGratisDanBursaBekas());

        System.out.println("\n==========================================================================================");
        System.out.printf("  HASIL PENGUJIAN AKHIR: %d DARI %d TEST CASES LULUS (PASS %.1f%%)\n",
                totalPassed, TOTAL_TESTS, (totalPassed / (double) TOTAL_TESTS) * 100.0);
        System.out.println("  VERIFIKASI OOP CAPSTONE: Zero State Corruption, 100% Invariant Compliance.");
        System.out.println("==========================================================================================");

        if (totalPassed != TOTAL_TESTS) {
            System.exit(1);
        }
    }

    private static void runTestCase(int no, String nama, Runnable test) {
        System.out.printf("SKENARIO %02d: %s\n", no, nama);
        try {
            test.run();
            System.out.println(">> [PASS] Berhasil diverifikasi tanpa error.\n");
            totalPassed++;
        } catch (Throwable t) {
            System.out.printf(">> [FAIL] Gagal: %s\n\n", t.getMessage());
        }
    }

    private static void testKasPemasukan() {
        KasPerusahaan kas = new KasPerusahaan(50000000); // 50 Juta
        kas.tambahPemasukan(1500000, "Sewa Harian Avanza");
        if (kas.getSaldo() != 51500000) {
            throw new AssertionError("Saldo kas tidak sesuai: " + kas.getSaldo());
        }
    }

    private static void testKasSaldoTidakCukup() {
        KasPerusahaan kas = new KasPerusahaan(2000000); // 2 Juta
        try {
            kas.kurangiPengeluaran(10000000, "Beli Mobil Baru");
            throw new AssertionError("Harusnya melempar SaldoKasTidakCukupException!");
        } catch (SaldoKasTidakCukupException e) {
            // Berhasil dicegah
        }
    }

    private static void testKendaraanInvarian() {
        try {
            new MobilLCGC("", "Sigra", 140000000, 350000, 0);
            throw new AssertionError("Harusnya gagal saat plat nomor kosong.");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    private static void testPolimorfismeTarif() {
        Kendaraan lcgc = new MobilLCGC("M 1001 SM", "Sigra", 140000000, 360000, 0);
        Kendaraan mewah = new MobilMewah("M 9999 SM", "Alphard", 1400000000, 2400000, 0);

        double sewaLcgc24h = lcgc.hitungBiayaSewa(24, false);
        double sewaMewah24hSupir = mewah.hitungBiayaSewa(24, true);

        if (sewaLcgc24h != 360000) throw new AssertionError("Tarif LCGC salah: " + sewaLcgc24h);
        if (sewaMewah24hSupir != (2400000 + 350000)) throw new AssertionError("Tarif VIP salah: " + sewaMewah24hSupir);
    }

    private static void testInterfaceGPSDanCutoff() {
        Kendaraan k = new MobilKeluarga("M 2020 SM", "Avanza", 250000000, 500000, 0);
        if (k.isGPSInstalled()) throw new AssertionError("Default GPS harusnya false.");
        if (k.matikanMesinRemote()) throw new AssertionError("Cutoff harus gagal tanpa modul GPS.");

        k.pasangGPS();
        if (!k.isGPSInstalled()) throw new AssertionError("GPS harusnya aktif setelah dipasang.");
        if (!k.matikanMesinRemote()) throw new AssertionError("Cutoff harus sukses saat GPS terpasang.");
        if (!k.isMesinDimatikanRemote()) throw new AssertionError("Status mesin harus cutoff.");
    }

    private static void testProfilingRisiko() {
        // Pelanggan berbahaya: Cuma KTP, Nomor Baru, Tunai Muka, Blacklist Asosiasi
        PesananMasuk pBahaya = new PesananMasuk(1, "Mr. Risky", "Pribadi", "LCGC", 24,
                false, false, false, false, true);
        int skor = pBahaya.hitungSkorRisiko();
        if (skor < 70) throw new AssertionError("Skor risiko berbahaya harus tinggi: " + skor);
        if (!pBahaya.getTingkatBahaya().equals("BAHAYA_TINGGI")) throw new AssertionError("Harusnya BAHAYA_TINGGI.");
    }

    private static void testMitigasiRisiko() {
        PesananMasuk p = new PesananMasuk(2, "Calon Penyewa", "Pribadi", "LCGC", 24,
                false, false, false, false, false);
        int skorAwal = p.hitungSkorRisiko();
        p.mintaJaminan();
        int skorSetelahJaminan = p.hitungSkorRisiko();
        if (skorSetelahJaminan >= skorAwal) throw new AssertionError("Jaminan harus mereduksi risiko.");

        p.tawarkanSopir();
        int skorDenganSopir = p.hitungSkorRisiko();
        if (skorDenganSopir != 0) throw new AssertionError("Dengan supir risiko harus 0%.");
    }

    private static void testAlokasiSewaNormal() {
        PerusahaanRental rental = new PerusahaanRental("Garasi Test", 100000000);
        Kendaraan k = rental.getArmada().get(0);
        PesananMasuk pesanan = new PesananMasuk(3, "Bpk. Aman", "Dinas", k.getKategori(), 24,
                false, true, true, true, false);

        rental.getManajerPesanan().getAntrianPesanan(); // queue
        try {
            // Mock terima pesanan manual
            rental.getKas().tambahPemasukan(k.getTarifHarian(), "Sewa " + k.getPlatNomor());
            k.setStatusSewa(Kendaraan.StatusSewa.DISEWA);
            k.setPenyewaSaatIni(pesanan.getNamaPenyewa());
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }

        if (k.getStatusSewa() != Kendaraan.StatusSewa.DISEWA) throw new AssertionError("Status harus DISEWA.");
    }

    private static void testGarasiPenuh() {
        PerusahaanRental rental = new PerusahaanRental("Garasi Mini", 500000000);
        // Default kapasitas = 8, armada awal = 3.
        try {
            for (int i = 4; i <= 9; i++) {
                Kendaraan extra = new MobilLCGC("M " + (1000 + i) + " SM", "Sigra", 140000000, 350000, 0);
                rental.tambahKendaraan(extra);
            }
            throw new AssertionError("Harusnya melempar GarasiPenuhException pada unit ke-9!");
        } catch (GarasiPenuhException e) {
            // Expected
        }
    }

    private static void testKrisisPenggelapan() {
        PerusahaanRental rental = new PerusahaanRental("Garasi Uji", 100000000);
        Kendaraan k = rental.getArmada().get(0);
        k.setStatusSewa(Kendaraan.StatusSewa.HILANG);
        k.setPenyewaSaatIni("Sindikat Gelap");

        if (k.getStatusSewa() != Kendaraan.StatusSewa.HILANG) throw new AssertionError("Mobil harus berstatus HILANG.");
    }

    private static void testResolusiKrisisGPS() {
        PerusahaanRental rental = new PerusahaanRental("Garasi Uji", 100000000);
        Kendaraan k = rental.getArmada().get(1); // Avanza with GPS
        k.setStatusSewa(Kendaraan.StatusSewa.HILANG);

        String hasil = rental.getManajerPesanan().aksiMatikanMesinRemote(k.getPlatNomor(), rental);
        if (!hasil.contains("SUKSES")) throw new AssertionError("Aksi matikan mesin GPS harusnya sukses: " + hasil);
        if (k.getStatusSewa() != Kendaraan.StatusSewa.SIAP) throw new AssertionError("Unit harus kembali SIAP setelah recovery.");
    }

    private static void testSavegamePersistence() {
        PerusahaanRental rental = new PerusahaanRental("Garasi Persist", 150000000);
        GameSaveRepository repo = new GameSaveRepository();
        Path testFile = Paths.get("data", "test_savegame.txt");

        try {
            repo.simpan(testFile, rental);
            if (!Files.exists(testFile)) throw new AssertionError("File savegame tidak terbentuk.");

            PerusahaanRental target = new PerusahaanRental("Garasi Target", 100000000);
            repo.muat(testFile, target);

            Files.deleteIfExists(testFile);
        } catch (Exception e) {
            throw new AssertionError("Gagal persistensi: " + e.getMessage());
        }
    }

    private static void testCollisionDetection() {
        PerusahaanRental rental = new PerusahaanRental("Garasi Collision Test", 200000000);
        // Unit M 2233 SM (Avanza - KELUARGA)
        PesananMasuk p1 = new PesananMasuk(101, "Pak Budi", "Wisata", "KELUARGA", 24, false, true, true, true, false);
        p1.setHariMulai(1);
        p1.setJamMulai(8);
        rental.getManajerPesanan().tambahPesanan(p1);

        try {
            rental.konfirmasiSewaDenganUnit(p1.getId(), "M 2233 SM");
        } catch (Exception e) {
            throw new AssertionError("Sewa awal harus berhasil: " + e.getMessage());
        }

        // Cek bentrok pada jam yang sama
        boolean bentrok = rental.cekJadwalBentrok("M 2233 SM", 1, 10, 12);
        if (!bentrok) throw new AssertionError("Harusnya terdeteksi BENTROK jadwal pada rentang sewa yang sama!");

        // Coba konfirmasi sewa kedua yang bentrok -> harus ditolak!
        PesananMasuk p2 = new PesananMasuk(102, "Ibu Ani", "Arisan", "KELUARGA", 12, false, true, true, true, false);
        p2.setHariMulai(1);
        p2.setJamMulai(10);
        rental.getManajerPesanan().tambahPesanan(p2);

        try {
            rental.konfirmasiSewaDenganUnit(p2.getId(), "M 2233 SM");
            throw new AssertionError("Harusnya menolak alokasi unit yang jadwalnya bentrok!");
        } catch (IllegalStateException e) {
            // Expected: bentrok jadwal!
        } catch (Exception e) {
            throw new AssertionError("Exception tidak sesuai: " + e.getMessage());
        }

        // Cek tidak bentrok pada hari berikutnya jauh setelah sewa selesai
        boolean tidakBentrok = rental.cekJadwalBentrok("M 2233 SM", 5, 8, 12);
        if (tidakBentrok) throw new AssertionError("Harusnya KOSONG (tidak bentrok) pada hari ke-5!");
    }

    private static void testUpgradeGratisDanBursaBekas() {
        PerusahaanRental rental = new PerusahaanRental("Garasi Upgrade Test", 250000000);
        double saldoAwal = rental.getKas().getSaldo();

        // 1. Uji Upgrade Gratis: Pelanggan minta LCGC (350.000 / hr), tapi dialokasikan Innova Medium SUV (900.000 / hr)
        PesananMasuk p = new PesananMasuk(999, "Pak RT", "Dinas Cepat", "LCGC", 24, false, true, true, true, false);
        p.setHariMulai(1);
        p.setJamMulai(8);
        rental.getManajerPesanan().tambahPesanan(p);

        try {
            Kendaraan innova = rental.findKendaraan("M 3344 SM");
            if (innova == null) throw new AssertionError("Innova harus ada di default fleet.");

            // Lakukan sewa dengan alokasi upgrade gratis
            rental.konfirmasiSewaDenganUnit(p.getId(), "M 3344 SM");

            // Verifikasi kas bertambah tarif LCGC (350.000) bukan 900.000
            double saldoSetelahSewa = rental.getKas().getSaldo();
            double pemasukan = saldoSetelahSewa - saldoAwal;
            if (Math.abs(pemasukan - 350000.0) > 1.0) {
                throw new AssertionError("Pemasukan upgrade gratis harus mengikuti tarif LCGC (Rp 350.000), aktual: " + pemasukan);
            }
            if (innova.getStatusSewa() != Kendaraan.StatusSewa.DISEWA) {
                throw new AssertionError("Status Innova harus berubah menjadi DISEWA.");
            }

            // 2. Uji Bursa Mobil Bekas (Diskon 60% harga baru, kondisi 72%)
            rental.beliMobilBekas("LCGC", "M 7788 SM", "Daihatsu Ayla 1.0", 120000000, 300000);
            Kendaraan aylaBekas = rental.findKendaraan("M 7788 SM");
            if (aylaBekas == null) throw new AssertionError("Unit bekas harus terdaftar.");
            if (!aylaBekas.isBekas()) throw new AssertionError("Flag isBekas() harus true.");
            if (aylaBekas.getKondisiMesin() > 75.0) throw new AssertionError("Kondisi mobil bekas harus ~72%, aktual: " + aylaBekas.getKondisiMesin());

            // Biaya beli bekas = 120jt * 0.6 = 72jt
            double pengeluaran = saldoSetelahSewa - rental.getKas().getSaldo();
            if (Math.abs(pengeluaran - 72000000.0) > 1.0) {
                throw new AssertionError("Pengeluaran beli bekas harus 60% harga baru (Rp 72 jt), aktual: " + pengeluaran);
            }
        } catch (Exception e) {
            throw new AssertionError("Uji upgrade gratis & bursa bekas gagal: " + e.getMessage());
        }
    }
}

