package src;

public class PengirimanReguler extends Pengiriman {
    public PengirimanReguler(String noResi, double beratKg, double jarakKm, String status) {
        super(noResi, beratKg, jarakKm, status);
    }
    
    // Creative Feature: Dynamic cost calculation
    @Override
    public double hitungBiaya() { return (getBeratKg() * 5000) + (getJarakKm() * 1500); }
    
    @Override
    public String estimasiWaktu() { return "3-5 Hari Kerja"; }
    
    @Override
    public String lacakStatus() { return "[REGULER] " + getNoResi() + " - Status: " + getStatus(); }
}
