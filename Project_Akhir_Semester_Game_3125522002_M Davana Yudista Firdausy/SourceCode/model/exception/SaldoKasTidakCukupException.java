package model.exception;

/**
 * Exception saat saldo kas operasional perusahaan tidak mencukupi
 * untuk pembelian armada, gaji supir, bayar sewa garasi, atau biaya operasional lain.
 */
public class SaldoKasTidakCukupException extends Exception {
    private final double saldoTersedia;
    private final double biayaDibutuhkan;

    public SaldoKasTidakCukupException(String message, double saldoTersedia, double biayaDibutuhkan) {
        super(message);
        this.saldoTersedia = saldoTersedia;
        this.biayaDibutuhkan = biayaDibutuhkan;
    }

    public double getSaldoTersedia() {
        return saldoTersedia;
    }

    public double getBiayaDibutuhkan() {
        return biayaDibutuhkan;
    }
}
