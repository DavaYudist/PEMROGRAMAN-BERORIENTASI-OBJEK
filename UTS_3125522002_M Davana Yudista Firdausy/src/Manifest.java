package src;

import java.util.ArrayList;
import java.util.List;

public class Manifest {
    private String idManifest;
    private Kurir kurir; // Relationship 1: Association (Manifest is handled by a Kurir)
    private List<Pengiriman> daftarPaket; // Relationship 2: Aggregation (Manifest contains multiple Pengiriman)

    public Manifest(String idManifest, Kurir kurir) {
        this.idManifest = idManifest;
        this.kurir = kurir;
        this.daftarPaket = new ArrayList<>();
    }
    
    public void tambahPaket(Pengiriman p) { 
        if(p.getBeratKg() > 0 && p.getJarakKm() > 0) {
            daftarPaket.add(p); 
        }
    }
    
    public void cetakManifest() {
        System.out.println("=== MANIFEST PENGIRIMAN: " + idManifest + " ===");
        System.out.println("Kurir Bertugas: " + kurir.getNama() + " (" + kurir.getPlatKendaraan() + ")");
        System.out.println("-------------------------------------------------");
        
        double totalPendapatan = 0;
        // Polymorphic iteration
        for (Pengiriman p : daftarPaket) {
            System.out.println(p.lacakStatus());
            System.out.println(" -> Biaya Ongkir : Rp" + p.hitungBiaya());
            System.out.println(" -> Estimasi Tiba: " + p.estimasiWaktu());
            System.out.println("-------------------------------------------------");
            totalPendapatan += p.hitungBiaya();
        }
        System.out.println("Total Pendapatan Manifest: Rp" + totalPendapatan);
        System.out.println("=================================================\n");
    }
}
