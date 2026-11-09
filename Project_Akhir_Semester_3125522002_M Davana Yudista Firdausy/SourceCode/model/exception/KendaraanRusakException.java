package model.exception;

/**
 * Custom Checked Exception: KendaraanRusakException
 * Dilempar ketika armada kendaraan memiliki status kondisi mesin "Dalam Perbaikan",
 * sehingga tidak layak jalan atau berisiko membahayakan jika disewakan kepada pelanggan.
 * 
 * Modul 10 - Exception Handling & Validasi State Objek
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class KendaraanRusakException extends Exception {
    private String platNomor;
    private String kondisiMesin;

    public KendaraanRusakException(String message) {
        super(message);
    }

    public KendaraanRusakException(String message, String platNomor, String kondisiMesin) {
        super(message);
        this.platNomor = platNomor;
        this.kondisiMesin = kondisiMesin;
    }

    public KendaraanRusakException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getPlatNomor() {
        return platNomor;
    }

    public String getKondisiMesin() {
        return kondisiMesin;
    }
}

