package src;

public class PengirimanExpress extends Pengiriman {
    private boolean asuransi; // Additional specific attribute
    
    public PengirimanExpress(String noResi, double beratKg, double jarakKm, String status, boolean asuransi) {
        super(noResi, beratKg, jarakKm, status);
        this.asuransi = asuransi;
    }
    
    // Creative Feature: Higher dynamic cost + optional insurance calculation
    @Override
    public double hitungBiaya() {
        double biayaDasar = (getBeratKg() * 10000) + (getJarakKm() * 3000);
        return asuransi ? biayaDasar + 50000 : biayaDasar;
    }
    
    @Override
    public String estimasiWaktu() { return "1-2 Hari Kerja (Prioritas Tinggi)"; }
    
    @Override
    public String lacakStatus() { 
        return "[EXPRESS] " + getNoResi() + " - Status: " + getStatus() + (asuransi ? " (Dilindungi Asuransi)" : ""); 
    }
}
