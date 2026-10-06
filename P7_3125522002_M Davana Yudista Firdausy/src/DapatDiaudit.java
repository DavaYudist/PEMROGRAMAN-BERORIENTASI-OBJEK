package src;

/**
 * Interface DapatDiaudit mendefinisikan behavioral contract (kontrak perilaku)
 * untuk setiap entitas dalam sistem inventaris yang wajib dapat diaudit secara berkala.
 *
 * Mengimplementasikan prinsip Abstraksi Murni (Pure Abstraction) di mana interface
 * hanya mendefinisikan spesifikasi method tanpa implementasi konkret, sehingga
 * tipe data yang tidak sekeluarga dalam hierarki inheritance (seperti Barang dan Transaksi)
 * dapat diikat dalam satu kontrak perilaku yang sama.
 *
 * @author M. Davana Yudista Firdausy (3125522002)
 * @version 1.0 (Modul 7 - Abstract Class, Abstract Method, and Interface)
 */
public interface DapatDiaudit {
    /**
     * Menjalankan audit kepatuhan dan status operasional pada entitas.
     *
     * @return String laporan status audit.
     */
    String jalankanAudit();
}
