package src;

public abstract class Pengiriman implements LayananLacak {
    private String noResi;
    private double beratKg;
    private double jarakKm;
    private String status;

    public Pengiriman(String noResi, double beratKg, double jarakKm, String status) {
        this.noResi = noResi;
        setBeratKg(beratKg);
        setJarakKm(jarakKm);
        this.status = status;
    }
    
    // Encapsulation & Validation (Business Rules)
    public void setBeratKg(double beratKg) {
        if(beratKg > 0) this.beratKg = beratKg;
        else System.out.println("Error [Validasi]: Berat barang harus lebih dari 0!");
    }
    public void setJarakKm(double jarakKm) {
        if(jarakKm > 0) this.jarakKm = jarakKm;
        else System.out.println("Error [Validasi]: Jarak pengiriman harus lebih dari 0!");
    }
    
    public String getNoResi() { return noResi; }
    public double getBeratKg() { return beratKg; }
    public double getJarakKm() { return jarakKm; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Abstract methods to enforce polymorphism
    public abstract double hitungBiaya();
    public abstract String estimasiWaktu();
}
