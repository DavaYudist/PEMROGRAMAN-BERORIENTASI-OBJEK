package model.exception;

/**
 * Exception yang dilempar saat mobil sewa dibawa kabur / digelapkan
 * akibat penyewa berisiko tinggi tanpa jaminan yang memadai.
 */
public class MobilDibawaKaburException extends Exception {
    private final String platNomor;
    private final String namaPenyewa;

    public MobilDibawaKaburException(String message, String platNomor, String namaPenyewa) {
        super(message);
        this.platNomor = platNomor;
        this.namaPenyewa = namaPenyewa;
    }

    public String getPlatNomor() {
        return platNomor;
    }

    public String getNamaPenyewa() {
        return namaPenyewa;
    }
}
