package model;

import java.io.Serializable;
import java.util.Locale;

/**
 * P8-P11: Model Pesanan Masuk dengan Penilaian Risiko Domestik Indonesia.
 * Mengintegrasikan variabel profiling KTP, riwayat asosiasi, dan mitigasi jaminan/supir.
 */
public class PesananMasuk implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum StatusPesanan {
        PENDING,
        DITERIMA,
        DITOLAK,
        DISEWA,
        KABUR_HILANG,
        SELESAI
    }

    private final long id;
    private final String namaPenyewa;
    private final String keperluan;
    private final String kategoriDiminta;
    private final int durasiJam;
    private boolean denganSopir;
    private boolean jaminanTambahan; // Setelah aksi 'Minta Jaminan'

    // Risk Badges (Indikator Risiko Penggelapan)
    private final boolean dokumenLengkap; // true = KTP+KK, false = Cuma KTP
    private final boolean nomorHpLama;    // true = HP Lama, false = Nomor Baru
    private final boolean transferBank;   // true = Transfer Bank, false = Tunai
    private final boolean blacklistAsosiasi; // true = Pernah bermasalah di grup rental

    private StatusPesanan status;
    private String platDialokasikan;
    private int jamMulai;
    private int hariMulai;

    public PesananMasuk(long id, String namaPenyewa, String keperluan, String kategoriDiminta, int durasiJam,
                        boolean denganSopir, boolean dokumenLengkap, boolean nomorHpLama,
                        boolean transferBank, boolean blacklistAsosiasi) {
        this.id = id;
        this.namaPenyewa = namaPenyewa;
        this.keperluan = keperluan;
        this.kategoriDiminta = kategoriDiminta;
        this.durasiJam = durasiJam;
        this.denganSopir = denganSopir;
        this.dokumenLengkap = dokumenLengkap;
        this.nomorHpLama = nomorHpLama;
        this.transferBank = transferBank;
        this.blacklistAsosiasi = blacklistAsosiasi;
        this.jaminanTambahan = false;
        this.status = StatusPesanan.PENDING;
        this.platDialokasikan = "-";
    }

    /**
     * Menghitung skor risiko penggelapan (0 - 100).
     * Jika menggunakan supir, risiko penggelapan = 0 (dijamin aman).
     */
    public int hitungSkorRisiko() {
        if (denganSopir) return 0;

        int skor = 10; // Basis awal
        if (!dokumenLengkap) skor += 35; // Cuma KTP
        if (!nomorHpLama) skor += 25;    // Nomor HP baru aktif
        if (!transferBank) skor += 20;   // Tunai muka
        if (blacklistAsosiasi) skor += 45; // Nama muncul di grup wa asosiasi

        if (jaminanTambahan) {
            skor = (int) (skor * 0.6); // Minta jaminan motor/ijazah mereduksi risiko 40%
        }

        return Math.min(100, skor);
    }

    public String getTingkatBahaya() {
        int skor = hitungSkorRisiko();
        if (skor >= 65) return "BAHAYA_TINGGI";
        if (skor >= 35) return "SEDANG_WASPADA";
        return "AMAN";
    }

    public void mintaJaminan() {
        this.jaminanTambahan = true;
    }

    public void tawarkanSopir() {
        this.denganSopir = true;
    }

    // Getters & Setters
    public long getId() { return id; }
    public String getNamaPenyewa() { return namaPenyewa; }
    public String getKeperluan() { return keperluan; }
    public String getKategoriDiminta() { return kategoriDiminta; }
    public int getDurasiJam() { return durasiJam; }
    public boolean isDenganSopir() { return denganSopir; }
    public void setDenganSopir(boolean denganSopir) { this.denganSopir = denganSopir; }
    public boolean isJaminanTambahan() { return jaminanTambahan; }
    public boolean isDokumenLengkap() { return dokumenLengkap; }
    public boolean isNomorHpLama() { return nomorHpLama; }
    public boolean isTransferBank() { return transferBank; }
    public boolean isBlacklistAsosiasi() { return blacklistAsosiasi; }
    public StatusPesanan getStatus() { return status; }
    public void setStatus(StatusPesanan status) { this.status = status; }
    public String getPlatDialokasikan() { return platDialokasikan; }
    public void setPlatDialokasikan(String platDialokasikan) { this.platDialokasikan = platDialokasikan; }
    public int getJamMulai() { return jamMulai; }
    public void setJamMulai(int jamMulai) { this.jamMulai = jamMulai; }
    public int getHariMulai() { return hariMulai; }
    public void setHariMulai(int hariMulai) { this.hariMulai = hariMulai; }

    public String toJson() {
        return String.format(Locale.US,
            "{\"id\":%d,\"nama\":\"%s\",\"keperluan\":\"%s\",\"kategori\":\"%s\",\"durasiJam\":%d," +
            "\"sopir\":%b,\"dokumenLengkap\":%b,\"nomorLama\":%b,\"transfer\":%b,\"blacklist\":%b," +
            "\"jaminan\":%b,\"skorRisiko\":%d,\"tingkatBahaya\":\"%s\",\"status\":\"%s\",\"plat\":\"%s\"," +
            "\"hariMulai\":%d,\"jamMulai\":%d}",
            id, namaPenyewa.replace("\"", "\\\""), keperluan.replace("\"", "\\\""),
            kategoriDiminta, durasiJam, denganSopir, dokumenLengkap, nomorHpLama, transferBank,
            blacklistAsosiasi, jaminanTambahan, hitungSkorRisiko(), getTingkatBahaya(),
            status.name(), platDialokasikan, hariMulai, jamMulai);
    }
}

