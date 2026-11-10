package model;

import model.exception.MobilDibawaKaburException;
import model.exception.SaldoKasTidakCukupException;

import java.io.Serializable;
import java.util.*;

/**
 * P8-P11: Pengelola Antrian Pemesanan & Krisis Penggelapan Mobil (Indonesian Rental Dynamics).
 */
public class ManajerPesanan implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<PesananMasuk> antrianPesanan;
    private final List<SesiSewa> daftarSesiSewa;
    private long autoIncrementId = 1;
    private long autoIncrementSesiId = 1;
    private final Random random = new Random();

    // Event Krisis Terkini
    public static class EventKrisis implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String plat;
        public final String penyewa;
        public final String pesan;
        public final boolean butuhTindakan;
        public String hasilTindakan;

        public EventKrisis(String plat, String penyewa, String pesan, boolean butuhTindakan) {
            this.plat = plat;
            this.penyewa = penyewa;
            this.pesan = pesan;
            this.butuhTindakan = butuhTindakan;
            this.hasilTindakan = "";
        }

        public String toJson() {
            return String.format(Locale.US,
                "{\"plat\":\"%s\",\"penyewa\":\"%s\",\"pesan\":\"%s\",\"butuhTindakan\":%b,\"hasil\":\"%s\"}",
                plat, penyewa.replace("\"", "\\\""), pesan.replace("\"", "\\\""), butuhTindakan, hasilTindakan.replace("\"", "\\\""));
        }
    }

    private final List<EventKrisis> riwayatKrisis;
    private EventKrisis krisisAktif;

    public ManajerPesanan() {
        this.antrianPesanan = new ArrayList<>();
        this.daftarSesiSewa = new ArrayList<>();
        this.riwayatKrisis = new ArrayList<>();
        this.krisisAktif = null;
    }

    /**
     * Generator Otomatis Pesanan Pelanggan Domestik Indonesia
     */
    public PesananMasuk generatePesananOtomatis(int hari, int jam) {
        String[] namaDepan = {"Bpk. Sugeng", "Ibu Sri Wahyuni", "Mas Rian", "Bpk. Bambang", "Pemuda Madura", "Tamu Hotel", "Bpk. Hendra", "Ibu Maya"};
        String[] namaBelakang = {"Sumenep", "Pamekasan", "Surabaya", "Jakarta", "Malang", "Bangkalan"};
        String nama = namaDepan[random.nextInt(namaDepan.length)] + " " + namaBelakang[random.nextInt(namaBelakang.length)];

        String[] keperluanList = {
            "Wisuda Putra di Kampus PENS",
            "Wisata Keluarga ke Pantai Lombang & Keraton",
            "Kunjungan Kerja Dinas Pemkab Sumenep",
            "Pernikahan Saudara di Kalianget",
            "Proyek Konstruksi Luar Kota",
            "Acara Mendesak Keluarga (Malam Hari)"
        };
        String keperluan = keperluanList[random.nextInt(keperluanList.length)];

        String[] kategoriList = {"LCGC", "KELUARGA", "MEDIUM_SUV", "MEWAH_VIP"};
        int katIdx = random.nextInt(kategoriList.length);
        String kategori = kategoriList[katIdx];

        int[] durasiOpsi = {12, 24, 24, 48, 72, 168}; // Jam
        int durasi = durasiOpsi[random.nextInt(durasiOpsi.length)];

        boolean dokumen = random.nextDouble() > 0.35; // 65% lengkap, 35% cuma KTP
        boolean noHpLama = random.nextDouble() > 0.40; // 60% no lama, 40% no baru
        boolean transfer = random.nextDouble() > 0.45; // 55% transfer, 45% tunai
        boolean blacklist = random.nextDouble() < 0.15; // 15% suspect blacklist
        boolean supir = (kategori.equals("MEWAH_VIP")) || (random.nextDouble() < 0.25);

        PesananMasuk p = new PesananMasuk(autoIncrementId++, nama, keperluan, kategori, durasi,
                supir, dokumen, noHpLama, transfer, blacklist);
        p.setHariMulai(hari);
        p.setJamMulai(jam);

        antrianPesanan.add(p);
        return p;
    }

    public synchronized void terimaPesanan(long idPesanan, String platNomor, WaktuSimulasi waktu,
                                          KasPerusahaan kas, PerusahaanRental rental)
            throws Exception {
        PesananMasuk target = cariPesanan(idPesanan);
        if (target == null) throw new IllegalArgumentException("Pesanan ID " + idPesanan + " tidak ditemukan.");
        if (target.getStatus() != PesananMasuk.StatusPesanan.PENDING) {
            throw new IllegalStateException("Pesanan sudah tidak dalam status PENDING.");
        }

        Kendaraan k = rental.findKendaraan(platNomor);
        if (k == null) throw new IllegalArgumentException("Kendaraan plat " + platNomor + " tidak ditemukan.");
        if (k.getStatusSewa() != Kendaraan.StatusSewa.SIAP) {
            throw new IllegalStateException("Kendaraan " + platNomor + " sedang tidak berstatus SIAP.");
        }

        // Kalkulasi Biaya Sewa
        double totalBiaya = k.hitungBiayaSewa(target.getDurasiJam(), target.isDenganSopir());

        // Mutasi State
        k.setStatusSewa(Kendaraan.StatusSewa.DISEWA);
        k.setPenyewaSaatIni(target.getNamaPenyewa());
        k.setDurasiJamSewa(target.getDurasiJam());
        k.setJamMulaiSewa(waktu.getJam());
        k.setDenganSopir(target.isDenganSopir());

        target.setStatus(PesananMasuk.StatusPesanan.DISEWA);
        target.setPlatDialokasikan(platNomor);

        // Tambah Kas Pemasukan
        kas.tambahPemasukan(totalBiaya, "Sewa " + k.getMerk() + " [" + platNomor + "] oleh " + target.getNamaPenyewa());

        // Buat Sesi Sewa
        SesiSewa sesi = new SesiSewa(autoIncrementSesiId++, platNomor, target.getNamaPenyewa(),
                waktu.getHari(), waktu.getJam(), target.getDurasiJam(), totalBiaya, target.isDenganSopir());
        daftarSesiSewa.add(sesi);
    }

    public synchronized void tolakPesanan(long idPesanan) {
        PesananMasuk target = cariPesanan(idPesanan);
        if (target != null) {
            target.setStatus(PesananMasuk.StatusPesanan.DITOLAK);
        }
    }

    public synchronized void mintaJaminan(long idPesanan) {
        PesananMasuk target = cariPesanan(idPesanan);
        if (target != null) {
            target.mintaJaminan();
        }
    }

    public synchronized void tawarkanSopir(long idPesanan) {
        PesananMasuk target = cariPesanan(idPesanan);
        if (target != null) {
            target.tawarkanSopir();
        }
    }

    /**
     * Evaluasi Waktu: Memeriksa apakah ada mobil sewa yang jatuh tempo atau dibawa kabur!
     */
    public synchronized void evaluasiWaktu(WaktuSimulasi waktu, KasPerusahaan kas, PerusahaanRental rental)
            throws MobilDibawaKaburException {
        int currentTotalJam = (waktu.getHari() - 1) * 24 + waktu.getJam();

        for (SesiSewa sesi : daftarSesiSewa) {
            if (sesi.isSelesai() || sesi.isDibawaKabur()) continue;

            int selesaiTotalJam = (sesi.getHariMulai() - 1) * 24 + sesi.getJamMulai() + sesi.getDurasiJam();
            if (currentTotalJam >= selesaiTotalJam) {
                Kendaraan k = rental.findKendaraan(sesi.getPlatNomor());
                if (k == null) continue;

                // Cek kemungkinan dibawa kabur berdasarkan skor risiko pesanan
                PesananMasuk refPesanan = null;
                for (PesananMasuk p : antrianPesanan) {
                    if (p.getPlatDialokasikan().equals(k.getPlatNomor()) && p.getStatus() == PesananMasuk.StatusPesanan.DISEWA) {
                        refPesanan = p;
                        break;
                    }
                }

                int risiko = (refPesanan != null) ? refPesanan.hitungSkorRisiko() : 20;

                // Jika risiko tinggi dan random trigger kena -> Mobil Dibawa Kabur!
                if (!sesi.isDenganSopir() && risiko >= 50 && (random.nextInt(100) < (risiko - 25))) {
                    sesi.setDibawaKabur(true);
                    k.setStatusSewa(Kendaraan.StatusSewa.HILANG);
                    if (refPesanan != null) refPesanan.setStatus(PesananMasuk.StatusPesanan.KABUR_HILANG);

                    String pesan = String.format("PERINGATAN KRISIS! Mobil %s [%s] tidak kembali dan dicurigai digelapkan oleh %s (Skor Risiko: %d)!",
                            k.getMerk(), k.getPlatNomor(), sesi.getNamaPenyewa(), risiko);

                    EventKrisis krisis = new EventKrisis(k.getPlatNomor(), sesi.getNamaPenyewa(), pesan, true);
                    this.krisisAktif = krisis;
                    this.riwayatKrisis.add(krisis);

                    throw new MobilDibawaKaburException(pesan, k.getPlatNomor(), sesi.getNamaPenyewa());
                } else {
                    // Pengembalian Normal Berhasil
                    sesi.setSelesai(true);
                    k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
                    k.setPenyewaSaatIni("-");
                    k.kurangiKondisi(5.0 + random.nextInt(5)); // Mesin aus 5-10%
                    k.tambahOdometer(sesi.getDurasiJam() * 25); // Tambah km
                    if (refPesanan != null) refPesanan.setStatus(PesananMasuk.StatusPesanan.SELESAI);
                }
            }
        }
    }

    // ==========================================================
    // 5 OPSI TINDAKAN RESOLUSI KRISIS PENGGELAPAN MOBIL
    // ==========================================================

    public synchronized String aksiTeleponPenyewa(String plat) {
        return "Hasil Panggilan: 'Nomor yang Anda tuju sedang dialihkan atau tidak dapat menerima panggilan.' Kontak penyewa sengaja dimatikan!";
    }

    public synchronized String aksiMatikanMesinRemote(String plat, PerusahaanRental rental) {
        Kendaraan k = rental.findKendaraan(plat);
        if (k == null) return "Armada tidak ditemukan.";

        if (!k.isGPSInstalled()) {
            return "GAGAL! Armada " + plat + " belum dipasangi modul GPS Tracker & Remote Immobilizer. Mesin tidak bisa dimatikan dari jauh!";
        }

        boolean sukses = k.matikanMesinRemote();
        if (sukses) {
            k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
            k.setPenyewaSaatIni("-");
            if (krisisAktif != null && krisisAktif.plat.equals(plat)) {
                krisisAktif.hasilTindakan = "SUKSES! Mesin berhasil dimatikan secara remote. Tim recovery berhasil mengamankan kembali unit " + plat + "!";
                krisisAktif = null;
            }
            return "SUKSES BESAR! Relai pemutus arus mesin aktif. Mobil mogok di tepi jalan dan berhasil ditarik kembali ke garasi!";
        }
        return "Gagal mengirim sinyal telemetri remote.";
    }

    public synchronized String aksiLaporPolisi(String plat, KasPerusahaan kas, PerusahaanRental rental)
            throws SaldoKasTidakCukupException {
        double biayaPolisi = 5000000.0; // Biaya operasional penyelidikan & laporan
        kas.kurangiPengeluaran(biayaPolisi, "Biaya Laporan & Penyelidikan Polisi [Unit " + plat + "]");

        Kendaraan k = rental.findKendaraan(plat);
        boolean berhasilDitemukan = random.nextDouble() < 0.35; // 35% sukses, 65% gagal/pungli

        if (berhasilDitemukan && k != null) {
            k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
            k.setPenyewaSaatIni("-");
            if (krisisAktif != null && krisisAktif.plat.equals(plat)) {
                krisisAktif.hasilTindakan = "Polisi berhasil menemukan mobil di gudang penadah. Unit dikembalikan ke garasi.";
                krisisAktif = null;
            }
            return "ALHAMDULILLAH! Tim kepolisian berhasil menggerebek lokasi sindikat dan mengembalikan unit " + plat + "!";
        } else {
            return "KECEWA! Setelah bayar biaya administrasi Rp 5.000.000, penyelidikan polisi nihil / lamban. Mobil masih belum ditemukan.";
        }
    }

    public synchronized String aksiByOneAkamsi(String plat, KasPerusahaan kas, PerusahaanRental rental)
            throws SaldoKasTidakCukupException {
        Kendaraan k = rental.findKendaraan(plat);
        boolean suksesNego = random.nextDouble() < 0.65; // 65% unit didapat

        if (suksesNego && k != null) {
            double biayaTebusan = 2500000.0; // Uang rokok / mediasi ormas akamsi
            kas.kurangiPengeluaran(biayaTebusan, "Biaya Mediasi & Recovery Akamsi [Unit " + plat + "]");
            k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
            k.setPenyewaSaatIni("-");
            if (krisisAktif != null && krisisAktif.plat.equals(plat)) {
                krisisAktif.hasilTindakan = "Unit berhasil ditarik paksa dari penadah dengan uang tebusan damai Rp 2.500.000.";
                krisisAktif = null;
            }
            return "NEGO BERHASIL! Tim ormas & akamsi berhasil mengamankan kunci mobil. Anda membayar tebusan damai Rp 2.500.000.";
        } else {
            double biayaMedis = 1000000.0;
            kas.kurangiPengeluaran(biayaMedis, "Biaya Medis & Pengamanan Bentrok Akamsi");
            return "GAGAL & RICUH! Terjadi percekcokan di lokasi penadah. Tim recovery mundur untuk keselamatan. Mobil belum bisa diambil!";
        }
    }

    public synchronized String aksiIkhlas(String plat, PerusahaanRental rental) {
        Kendaraan k = rental.findKendaraan(plat);
        if (k != null) {
            rental.hapusKendaraan(plat);
            if (krisisAktif != null && krisisAktif.plat.equals(plat)) {
                krisisAktif.hasilTindakan = "Unit " + plat + " resmi dihapus dari pembukuan (Total Asset Loss / Ikhlas).";
                krisisAktif = null;
            }
            return "IKHLAS TOTAL: Unit " + plat + " resmi dihapus dari pembukuan aset perusahaan sebagai kerugian penggelapan.";
        }
        return "Armada tidak ditemukan.";
    }

    private PesananMasuk cariPesanan(long id) {
        for (PesananMasuk p : antrianPesanan) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public List<PesananMasuk> getAntrianPesanan() {
        return Collections.unmodifiableList(antrianPesanan);
    }

    public List<SesiSewa> getDaftarSesiSewa() {
        return Collections.unmodifiableList(daftarSesiSewa);
    }

    public EventKrisis getKrisisAktif() {
        return krisisAktif;
    }

    public String getAntrianJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < antrianPesanan.size(); i++) {
            sb.append(antrianPesanan.get(i).toJson());
            if (i < antrianPesanan.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getSesiSewaJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < daftarSesiSewa.size(); i++) {
            sb.append(daftarSesiSewa.get(i).toJson());
            if (i < daftarSesiSewa.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
