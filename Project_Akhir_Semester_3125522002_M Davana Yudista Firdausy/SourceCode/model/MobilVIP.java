package model;

import java.util.Locale;

/**
 * Subclass MobilVIP
 * Mewarisi Kendaraan dengan atribut khusus suhu kabin dan layanan concierge/chauffeur.
 * Berbasis tarif harian (24 jam) dengan prorata per jam (tarifPerHari / 24.0).
 * Perhitungan tarif menyertakan biaya layanan VIP Concierge & Chauffeur Rp 150.000
 * Mengimplementasikan validasi defensif parameter sesuai Modul 10.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class MobilVIP extends Kendaraan {
    private double suhuKabin;

    public MobilVIP(String plat, String merk, double lat, double lng, int bat, double suhuKabin) {
        super(plat, merk, lat, lng, bat, 2400000.0, 18500, "Sangat Baik", "Interior Mewah, Kulit Nappa, AC Dual Zone");
        this.suhuKabin = suhuKabin;
    }

    public MobilVIP(String plat, String merk, double lat, double lng, int bat, double suhuKabin,
                    double tarifPerHari, int odoMeterKm, String kondisiMesin, String catatanFisik) {
        super(plat, merk, lat, lng, bat, tarifPerHari, odoMeterKm, kondisiMesin, catatanFisik);
        this.suhuKabin = suhuKabin;
    }

    public double getSuhuKabin() {
        return suhuKabin;
    }

    public void setSuhuKabin(double suhuKabin) {
        this.suhuKabin = suhuKabin;
    }

    @Override
    public String getKategori() {
        return "VIP";
    }

    /**
     * Perhitungan Biaya Sewa VIP:
     * - Validasi defensif: durasiJam > 0, diskonPersen 0% - 100%
     * - Prorata per jam = tarifPerHari / 24.0
     * - Biaya Supir & Concierge VIP: Rp 150.000 / hari jika denganSupir = true
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
        // VIP Driver / Chauffeur & Concierge fee
        double biayaSupir = denganSupir ? (durasiJam <= 24 ? 150000.0 : Math.ceil(durasiJam / 24.0) * 150000.0) : 0.0;
        double totalSebelumDiskon = subtotal + biayaSupir;
        double potongan = totalSebelumDiskon * (diskonPersen / 100.0);
        return Math.max(0.0, totalSebelumDiskon - potongan);
    }

    @Override
    public String toJson() {
        return String.format(Locale.US, 
            "{\"plat\":\"%s\", \"merk\":\"%s\", \"lat\":%.6f, \"lng\":%.6f, \"baterai\":%d, \"kategori\":\"%s\", \"suhu\":%.1f, \"disewa\":%b, \"slot\":\"%s\", \"baySlot\":\"%s\", \"penyewa\":\"%s\", \"status\":\"%s\", \"tarifPerHari\":%.0f, \"tarifPerJam\":%.0f, \"tarif\":%.0f, \"kondisiMesin\":\"%s\", \"odoMeterKm\":%d, \"catatanFisik\":\"%s\", \"bisaDisewa\":%b, \"denganSupir\":%b}", 
            getPlatNomor(), getMerk(), getLat(), getLng(), getBaterai(), getKategori(), suhuKabin, isDisewa(), 
            getSlotParkir(), getSlotParkir(), getPenyewa(), (isDisewa() ? "DISEWA" : "STANDBY"), 
            getTarifPerHari(), getTarifPerJamEquivalent(), getTarifPerHari(), escapeJson(getKondisiMesin()), 
            getOdoMeterKm(), escapeJson(getCatatanFisik()), isBisaDisewa(), isDenganSupir());
    }
}
