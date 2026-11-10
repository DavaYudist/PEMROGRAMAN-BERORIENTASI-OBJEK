package model.exception;

/**
 * Exception saat alokasi armada baru atau pengembalian mobil
 * melebihi batas kapasitas slot parkir garasi yang tersedia.
 */
public class GarasiPenuhException extends Exception {
    private final int kapasitasMaksimal;
    private final int totalArmada;

    public GarasiPenuhException(String message, int kapasitasMaksimal, int totalArmada) {
        super(message);
        this.kapasitasMaksimal = kapasitasMaksimal;
        this.totalArmada = totalArmada;
    }

    public int getKapasitasMaksimal() {
        return kapasitasMaksimal;
    }

    public int getTotalArmada() {
        return totalArmada;
    }
}

