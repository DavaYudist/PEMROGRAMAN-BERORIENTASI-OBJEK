package model.exception;

/**
 * Custom Checked Exception: KapasitasPenuhException
 * Dilempar ketika alokasi bay parkir di pool garasi Sumenep telah mencapai batas maksimum
 * dan tidak dapat menampung unit armada baru, atau ketika dilakukan pemangkasan bay parkir
 * yang saat ini masih ditempati oleh unit armada aktif.
 * 
 * Modul 10 - Exception Handling & Validasi State Objek
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class KapasitasPenuhException extends Exception {
    private int kapasitasTersedia;
    private int kapasitasDiminta;

    public KapasitasPenuhException(String message) {
        super(message);
    }

    public KapasitasPenuhException(String message, int kapasitasTersedia, int kapasitasDiminta) {
        super(message);
        this.kapasitasTersedia = kapasitasTersedia;
        this.kapasitasDiminta = kapasitasDiminta;
    }

    public KapasitasPenuhException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getKapasitasTersedia() {
        return kapasitasTersedia;
    }

    public int getKapasitasDiminta() {
        return kapasitasDiminta;
    }
}

