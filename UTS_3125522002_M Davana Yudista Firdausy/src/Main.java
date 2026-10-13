package src;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   UTS PRAKTIKUM PBO - SISTEM PENGIRIMAN BARANG  ");
        System.out.println("   M. Davana Yudista Firdausy | 3125522002       ");
        System.out.println("=================================================\n");

        // 1. Instantiate Object 1
        Kurir kurir1 = new Kurir("KR-001", "Budi Santoso", "M 1234 XX");

        // 2. Instantiate Object 2 (Association)
        Manifest manifestSumenep = new Manifest("MF-SM-2026", kurir1);

        // 3. Instantiate Objects 3 & 4 (Polymorphism & Upcasting)
        Pengiriman pkt1 = new PengirimanReguler("RE-001", 2.5, 10.0, "Dikirim ke Sumenep");
        Pengiriman pkt2 = new PengirimanExpress("EX-999", 5.0, 50.0, "Transit di Pamekasan", true);
        
        // 4. Test Validation (Negative Testing)
        System.out.println("[TESTING VALIDASI BUSINESS RULE]");
        Pengiriman pktInvalid = new PengirimanReguler("RE-002", -5, 15, "Pending"); // Should trigger error
        System.out.println();

        // 5. Aggregation
        manifestSumenep.tambahPaket(pkt1);
        manifestSumenep.tambahPaket(pkt2);

        // 6. Execute Polymorphic Behavior
        manifestSumenep.cetakManifest();
    }
}
