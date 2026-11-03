package model.exception;

/**
 * Custom Checked Exception: KendaraanTidakTersediaException
 * Dilempar ketika armada kendaraan yang diminta sedang disewa oleh pelanggan lain (status DISEWA)
 * sehingga tidak tersedia untuk transaksi penyewaan baru.
 * 
 * Modul 10 - Exception Handling & Validasi State Objek
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class KendaraanTidakTersediaException extends Exception {
    private String platNomor;

    public KendaraanTidakTersediaException(String message) {
        super(message);
    }

    public KendaraanTidakTersediaException(String message, String platNomor) {
        super(message);
        this.platNomor = platNomor;
    }

    public KendaraanTidakTersediaException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getPlatNomor() {
        return platNomor;
    }
}
