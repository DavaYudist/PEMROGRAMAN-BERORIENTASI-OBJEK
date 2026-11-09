package model;

/**
 * Subclass MobilReguler
 * Mewarisi Kendaraan dengan implementasi perhitungan sewa reguler:
 * Berbasis tarif harian (24 jam) dengan pembagian prorata per jam (tarifPerHari / 24.0).
 * Mengimplementasikan validasi argumen defensif sesuai Modul 10.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class MobilReguler extends Kendaraan {

    public MobilReguler(String plat, String merk, double lat, double lng, int bat) { 
        super(plat, merk, lat, lng, bat, 600000.0, 32000, "Sangat Baik", "Mulus, Bodi Bersih, AC Dingin"); 
    }

    public MobilReguler(String plat, String merk, double lat, double lng, int bat,
                        double tarifPerHari, int odoMeterKm, String kondisiMesin, String catatanFisik) {
        super(plat, merk, lat, lng, bat, tarifPerHari, odoMeterKm, kondisiMesin, catatanFisik);
    }

    @Override 
    public String getKategori() { 
        return "Reguler"; 
    }

    /**
     * Perhitungan Biaya Sewa Reguler:
     * - Validasi defensif: durasiJam > 0, diskonPersen 0% - 100%
     * - Prorata per jam = tarifPerHari / 24.0
     * - Biaya Supir Reguler: Rp 100.000 / hari jika denganSupir = true
     * - Diskon Persen Admin: memotong total tagihan sebelum diskon
     */
    @Override
    public double hitungBiayaSewa(int durasiJam, boolean denganSupir, double diskonPersen) {
        if (durasiJam <= 0) {
            throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
        }
        if (diskonPersen < 0.0 || diskonPersen > 100.0) {
            throw new IllegalArgumentException("Diskon harus di antara 0% dan 100%.");
        }

        double subtotal = durasiJam * (getTarifPerHari() / 24.0);
        double biayaSupir = denganSupir ? (durasiJam <= 24 ? 100000.0 : Math.ceil(durasiJam / 24.0) * 100000.0) : 0.0;
        double totalSebelumDiskon = subtotal + biayaSupir;
        double potongan = totalSebelumDiskon * (diskonPersen / 100.0);
        return Math.max(0.0, totalSebelumDiskon - potongan);
    }
}
