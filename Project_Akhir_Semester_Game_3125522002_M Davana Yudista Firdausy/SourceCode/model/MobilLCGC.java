package model;

/**
 * P5: Subclass Mobil LCGC (Low Cost Green Car) - Daihatsu Sigra / Toyota Ayla / Calya.
 * Paling terjangkau, irit bahan bakar, cocok untuk pasar rental hemat.
 */
public class MobilLCGC extends Kendaraan {
    private static final long serialVersionUID = 1L;

    public MobilLCGC(String platNomor, String merk, double hargaBeli, double tarifHarian, int odoMeterKm) {
        super(platNomor, merk, hargaBeli, tarifHarian, odoMeterKm);
    }

    @Override
    public String getKategori() {
        return "LCGC";
    }

    @Override
    public double getBiayaServisRutin() {
        return 450000.0;
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

