package model;

/**
 * P5: Subclass Mobil Keluarga / Low MPV (Toyota Avanza, Mitsubishi Xpander, Suzuki Ertiga).
 * 'Mobil Sejuta Umat' Indonesia, paling laris disewa untuk perjalanan keluarga dan dinas.
 */
public class MobilKeluarga extends Kendaraan {
    private static final long serialVersionUID = 1L;

    public MobilKeluarga(String platNomor, String merk, double hargaBeli, double tarifHarian, int odoMeterKm) {
        super(platNomor, merk, hargaBeli, tarifHarian, odoMeterKm);
    }

    @Override
    public String getKategori() {
        return "KELUARGA";
    }

    @Override
    public double getBiayaServisRutin() {
        return 750000.0;
    }

    @Override
    public double hitungBiayaSewa(int durasiJam, boolean denganSopir) {
        if (durasiJam <= 0) throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
        double tarifPerJam = getTarifHarian() / 24.0;
        double biayaMobil = tarifPerJam * durasiJam;
        double biayaSopir = denganSopir ? (150000.0 / 24.0 * durasiJam) : 0.0;
        return biayaMobil + biayaSopir;
    }
}
