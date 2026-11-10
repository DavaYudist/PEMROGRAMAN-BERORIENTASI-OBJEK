package model;

import model.exception.SaldoKasTidakCukupException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * P1-P4: Enkapsulasi Kas Operasional Perusahaan Rental Mobil.
 * Mengelola saldo likuid IDR dengan validasi defensif ketat dan pencatatan audit transaksi.
 */
public class KasPerusahaan implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Transaksi implements Serializable {
        private static final long serialVersionUID = 1L;
        private final long id;
        private final String tipe; // PEMASUKAN atau PENGELUARAN
        private final double jumlah;
        private final String keterangan;
        private final double saldoSetelah;

        public Transaksi(long id, String tipe, double jumlah, String keterangan, double saldoSetelah) {
            this.id = id;
            this.tipe = tipe;
            this.jumlah = jumlah;
            this.keterangan = keterangan;
            this.saldoSetelah = saldoSetelah;
        }

        public long getId() { return id; }
        public String getTipe() { return tipe; }
        public double getJumlah() { return jumlah; }
        public String getKeterangan() { return keterangan; }
        public double getSaldoSetelah() { return saldoSetelah; }

        public String toJson() {
            return String.format(Locale.US,
                "{\"id\":%d,\"tipe\":\"%s\",\"jumlah\":%.0f,\"keterangan\":\"%s\",\"saldoSetelah\":%.0f}",
                id, tipe, jumlah, keterangan.replace("\"", "\\\""), saldoSetelah);
        }
    }

    private double saldo;
    private final List<Transaksi> riwayatTransaksi;
    private long autoIncrementId = 1;

    public KasPerusahaan(double modalAwal) {
        if (modalAwal < 0) {
            throw new IllegalArgumentException("Modal awal kas tidak boleh negatif.");
        }
        this.saldo = modalAwal;
        this.riwayatTransaksi = new ArrayList<>();
        if (modalAwal > 0) {
            catatTransaksi("PEMASUKAN", modalAwal, "Modal Awal Perusahaan Rental");
        }
    }

    public synchronized void tambahPemasukan(double jumlah, String keterangan) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pemasukan harus lebih besar dari Rp 0.");
        }
        this.saldo += jumlah;
        catatTransaksi("PEMASUKAN", jumlah, keterangan);
    }

    public synchronized void kurangiPengeluaran(double jumlah, String keterangan) throws SaldoKasTidakCukupException {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pengeluaran harus lebih besar dari Rp 0.");
        }
        if (this.saldo < jumlah) {
            throw new SaldoKasTidakCukupException(
                String.format(Locale.US, "Saldo kas (Rp %,.0f) tidak mencukupi untuk biaya %s (Rp %,.0f).",
                    saldo, keterangan, jumlah), saldo, jumlah);
        }
        this.saldo -= jumlah;
        catatTransaksi("PENGELUARAN", jumlah, keterangan);
    }

    private void catatTransaksi(String tipe, double jumlah, String keterangan) {
        Transaksi t = new Transaksi(autoIncrementId++, tipe, jumlah, keterangan, this.saldo);
        riwayatTransaksi.add(t);
    }

    public synchronized double getSaldo() {
        return saldo;
    }

    public String getSaldoFormatted() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return nf.format(saldo);
    }

    public synchronized List<Transaksi> getRiwayatTransaksi() {
        return Collections.unmodifiableList(riwayatTransaksi);
    }

    public synchronized String getRiwayatJson(int limit) {
        StringBuilder sb = new StringBuilder("[");
        int start = Math.max(0, riwayatTransaksi.size() - limit);
        for (int i = riwayatTransaksi.size() - 1; i >= start; i--) {
            sb.append(riwayatTransaksi.get(i).toJson());
            if (i > start) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}

