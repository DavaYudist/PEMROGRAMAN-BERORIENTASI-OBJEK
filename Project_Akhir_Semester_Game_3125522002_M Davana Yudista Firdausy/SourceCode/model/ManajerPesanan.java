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
        public final int skorRisiko;
        public final boolean butuhTindakan;
        public String hasilTindakan;
        public String tahap; // "AWAL", "TERHUBUNG", "ESKALASI"

        public EventKrisis(String plat, String penyewa, String pesan, int skorRisiko, boolean butuhTindakan) {
            this.plat = plat;
            this.penyewa = penyewa;
            this.pesan = pesan;
            this.skorRisiko = skorRisiko;
            this.butuhTindakan = butuhTindakan;
            this.hasilTindakan = "";
            this.tahap = "AWAL";
        }

        public String toJson() {
            return String.format(Locale.US,
                "{\"plat\":\"%s\",\"penyewa\":\"%s\",\"pesan\":\"%s\",\"skorRisiko\":%d,\"butuhTindakan\":%b,\"hasil\":\"%s\",\"tahap\":\"%s\"}",
                plat, penyewa.replace("\"", "\\\""), pesan.replace("\"", "\\\""), skorRisiko, butuhTindakan,
                hasilTindakan.replace("\"", "\\\""), tahap);
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

    public static int getTierLevel(String kategori) {
        if (kategori == null) return 0;
        switch (kategori.toUpperCase()) {
            case "LCGC": return 0;
            case "KELUARGA": return 1;
            case "MEDIUM_SUV": return 2;
            case "MEWAH_VIP": return 3;
            default: return 1;
        }
    }

    public static double getTarifDasarKategori(String kategori) {
        if (kategori == null) return 350000.0;
        switch (kategori.toUpperCase()) {
            case "LCGC": return 350000.0;
            case "KELUARGA": return 550000.0;
            case "MEDIUM_SUV": return 900000.0;
            case "MEWAH_VIP": return 2800000.0;
            default: return 550000.0;
        }
    }

    /**
     * Memeriksa apakah jadwal sewa untuk kendaraan tertentu bentrok dengan sewa aktif atau reservasi yang sudah ada.
     */
    public boolean cekJadwalBentrok(String platNomor, int jamMulai, int durasiJam) {
        return cekJadwalBentrok(platNomor, 0, jamMulai, durasiJam, false);
    }

    public boolean cekJadwalBentrok(String platNomor, int hariMulai, int jamMulai, int durasiJam, boolean isCarWashInhouse) {
        int targetMulai = (hariMulai > 0) ? ((hariMulai - 1) * 24 + jamMulai) : jamMulai;
        int targetSelesai = targetMulai + durasiJam + (isCarWashInhouse ? 0 : 6);

        for (SesiSewa sesi : daftarSesiSewa) {
            if (!sesi.getPlatNomor().equalsIgnoreCase(platNomor)) continue;
            if (sesi.isSelesai() || sesi.isDibawaKabur()) continue;

            int sesiMulai = (sesi.getHariMulai() - 1) * 24 + sesi.getJamMulai();
            int sesiSelesai = sesiMulai + sesi.getDurasiJam() + (isCarWashInhouse ? 0 : 6);

            if (targetMulai < sesiSelesai && sesiMulai < targetSelesai) {
                return true; // Bentrok jadwal
            }
        }
        return false; // Kosong
    }

    /**
     * Konfirmasi Alokasi Unit Kendaraan Spesifik untuk Pesanan (Taploka "Pilih Unit").
     * Mendukung upgrade gratis jika dialokasikan kendaraan dengan kelas lebih tinggi.
     */
    public synchronized boolean konfirmasiSewaDenganUnit(long idPesanan, String platNomor, WaktuSimulasi waktu,
                                                        KasPerusahaan kas, PerusahaanRental rental)
            throws Exception {
        PesananMasuk target = cariPesanan(idPesanan);
        if (target == null) throw new IllegalArgumentException("Pesanan ID " + idPesanan + " tidak ditemukan.");
        if (target.getStatus() != PesananMasuk.StatusPesanan.PENDING) {
            throw new IllegalStateException("Pesanan ID " + idPesanan + " sudah diproses (Status: " + target.getStatus() + ").");
        }

        Kendaraan k = rental.findKendaraan(platNomor);
        if (k == null) throw new IllegalArgumentException("Kendaraan plat " + platNomor + " tidak ditemukan di armada.");
        if (k.getStatusSewa() == Kendaraan.StatusSewa.HILANG || k.getStatusSewa() == Kendaraan.StatusSewa.SERVIS) {
            throw new IllegalStateException("Kendaraan " + platNomor + " sedang tidak dapat disewa (Status: " + k.getStatusSewa() + ").");
        }

        // 1. Invariant: Validasi Kompatibilitas Kelas (Upgrade gratis diperbolehkan, downgrade ditolak)
        int orderTier = getTierLevel(target.getKategoriDiminta());
        int carTier = getTierLevel(k.getKategori());
        if (carTier < orderTier) {
            throw new IllegalStateException("Kelas unit " + k.getKategori() + " lebih rendah dari permintaan pelanggan (" + target.getKategoriDiminta() + ").");
        }

        // 2. Invariant: Validasi Tabrakan Jadwal (Collision Check)
        boolean bentrok = cekJadwalBentrok(platNomor, target.getHariMulai(), target.getJamMulai(), target.getDurasiJam(), rental.isCarWashInhouse());
        if (bentrok) {
            throw new IllegalStateException("Jadwal bentrok dengan sewa aktif untuk unit " + platNomor + ".");
        }

        // 3. Kalkulasi Biaya: Jika upgrade gratis, tarif dasar dihitung berdasarkan kelas yang diminta
        double totalBiaya;
        if (carTier > orderTier) {
            double tarifDasar = getTarifDasarKategori(target.getKategoriDiminta());
            totalBiaya = (tarifDasar / 24.0) * target.getDurasiJam();
            if (target.isDenganSopir()) {
                totalBiaya += (150000.0 / 24.0) * target.getDurasiJam();
            }
        } else {
            totalBiaya = k.hitungBiayaSewa(target.getDurasiJam(), target.isDenganSopir());
        }

        if (rental.getEventAktif() != null) {
            totalBiaya *= rental.getEventAktif().multiplierTarif;
        }

        // 4. Mutasi State Kendaraan
        k.setStatusSewa(Kendaraan.StatusSewa.DISEWA);
        k.setPenyewaSaatIni(target.getNamaPenyewa());
        k.setDurasiJamSewa(target.getDurasiJam());
        k.setJamMulaiSewa(waktu.getJam());
        k.setDenganSopir(target.isDenganSopir());

        // 5. Mutasi State Pesanan
        target.setStatus(PesananMasuk.StatusPesanan.DISEWA);
        target.setPlatDialokasikan(platNomor);

        // 6. Catat Transaksi Finansial
        String upgradeNote = (carTier > orderTier) ? " (Upgrade Gratis dari " + target.getKategoriDiminta() + ")" : "";
        kas.tambahPemasukan(totalBiaya, "Sewa " + k.getMerk() + " [" + platNomor + "] oleh " + target.getNamaPenyewa() + upgradeNote);

        // 7. Buat Sesi Sewa untuk Gantt Timeline
        SesiSewa sesi = new SesiSewa(autoIncrementSesiId++, platNomor, target.getNamaPenyewa(),
                target.getHariMulai(), target.getJamMulai(), target.getDurasiJam(), totalBiaya, target.isDenganSopir());
        daftarSesiSewa.add(sesi);

        return true;
    }

    public synchronized boolean konfirmasiSewaDenganUnit(String idPesanan, String platNomor, WaktuSimulasi waktu,
                                                        KasPerusahaan kas, PerusahaanRental rental)
            throws Exception {
        return konfirmasiSewaDenganUnit(Long.parseLong(idPesanan.trim()), platNomor, waktu, kas, rental);
    }

    public synchronized void terimaPesanan(long idPesanan, String platNomor, WaktuSimulasi waktu,
                                          KasPerusahaan kas, PerusahaanRental rental)
            throws Exception {
        konfirmasiSewaDenganUnit(idPesanan, platNomor, waktu, kas, rental);
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

                    EventKrisis krisis = new EventKrisis(k.getPlatNomor(), sesi.getNamaPenyewa(), pesan, risiko, true);
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
    // TINDAKAN RESOLUSI KRISIS & BRANCHING ENGINE (SUB-AGENT 1)
    // ==========================================================

    /**
     * Skenario Percabangan 1: Telepon Nomor Penyewa.
     * Evaluasi skor risiko:
     * - Skenario A (45% peluang jika risiko < 70%): Panggilan terjawab & kooperatif, berikan toleransi (grace period), denda keterlambatan +Rp 100.000, batalkan krisis.
     * - Skenario B (55% base atau 100% jika risiko >= 70%): Nomor tidak aktif, eskalasi ke tindakan darurat lanjutan.
     */
    public synchronized String aksiTeleponPenyewa(String plat, PerusahaanRental rental) {
        Kendaraan k = rental.findKendaraan(plat);
        PesananMasuk refPesanan = null;
        for (PesananMasuk p : antrianPesanan) {
            if (p.getPlatDialokasikan().equalsIgnoreCase(plat) && (p.getStatus() == PesananMasuk.StatusPesanan.KABUR_HILANG || p.getStatus() == PesananMasuk.StatusPesanan.DISEWA)) {
                refPesanan = p;
                break;
            }
        }

        int risiko = (refPesanan != null) ? refPesanan.hitungSkorRisiko() : (krisisAktif != null ? krisisAktif.skorRisiko : 50);
        String namaPenyewa = (refPesanan != null) ? refPesanan.getNamaPenyewa() : (krisisAktif != null ? krisisAktif.penyewa : "Penyewa");

        // Scenario A: Panggilan Terhubung & Konfirmasi Lokasi (45% chance jika risk < 70%)
        boolean isAnswered = (risiko < 70) && (random.nextDouble() < 0.45);

        if (isAnswered) {
            String pesan = String.format("📞 Panggilan Terhubung! Penyewa (%s) kooperatif: 'Maaf Mas, ban bocor di Arosbaya, sedang dalam perjalanan pulang (estimasi 2 jam lagi).'", namaPenyewa);
            // Tambahkan denda keterlambatan Rp 100.000 ke kas perusahaan
            rental.getKas().tambahPemasukan(100000.0, "Denda Keterlambatan Sewa [" + plat + "] - " + namaPenyewa);
            if (k != null) {
                k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
                k.setPenyewaSaatIni("-");
                k.kurangiKondisi(3.0);
            }
            for (SesiSewa s : daftarSesiSewa) {
                if (s.getPlatNomor().equalsIgnoreCase(plat) && !s.isSelesai()) {
                    s.setSelesai(true);
                    s.setDibawaKabur(false);
                }
            }
            if (refPesanan != null) {
                refPesanan.setStatus(PesananMasuk.StatusPesanan.SELESAI);
            }
            if (krisisAktif != null && krisisAktif.plat.equalsIgnoreCase(plat)) {
                krisisAktif.tahap = "TERHUBUNG";
                krisisAktif.hasilTindakan = pesan;
                krisisAktif = null;
            }
            return "TERHUBUNG:" + pesan;
        } else {
            // Scenario B: Nomor Tidak Aktif / Dialihkan (Indikasi Penggelapan)
            String pesan = "⚠️ Panggilan Gagal! Nomor dialihkan atau tidak aktif. Indikasi kuat penggelapan unit! Silakan gunakan tindakan eskalasi di bawah.";
            if (krisisAktif != null && krisisAktif.plat.equalsIgnoreCase(plat)) {
                krisisAktif.tahap = "ESKALASI";
                krisisAktif.hasilTindakan = pesan;
            }
            return "GAGAL:" + pesan;
        }
    }

    public synchronized String aksiTeleponPenyewa(String plat) {
        return "⚠️ Panggilan Gagal! Nomor dialihkan atau tidak aktif. Indikasi penggelapan unit!";
    }

    /**
     * Skenario Percabangan 2: Matikan Mesin Remote & Kirim Tim Penjemput (Wajib GPS).
     * Jika GPS terpasang: 100% GARANSI UNIT KEMBALI dengan potongan biaya operasional tim Rp 500.000.
     */
    public synchronized String aksiMatikanMesinRemote(String plat, PerusahaanRental rental)
            throws SaldoKasTidakCukupException {
        Kendaraan k = rental.findKendaraan(plat);
        if (k == null) return "GAGAL:Armada tidak ditemukan.";

        if (!k.isGPSInstalled()) {
            return "GAGAL:Gagal! Modul GPS Tracker belum terpasang pada unit ini. Pasang modul GPS di menu Usaha!";
        }

        // 1. Eksekusi remote engine cutoff contract
        k.matikanMesinRemote();

        // 2. Dispatch recovery towing & driver crew (deducts Rp 500.000)
        rental.getKas().kurangiPengeluaran(500000.0, "Biaya Operasional Tim Recovery & Derek GPS [" + plat + "]");

        // 3. Guaranteed recovery: unit kembali ke garasi
        k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
        k.setPenyewaSaatIni("-");
        k.kurangiKondisi(5.0); // Minor wear
        k.hidupkanMesinRemote(); // Reset relai agar siap disewakan kembali

        for (SesiSewa s : daftarSesiSewa) {
            if (s.getPlatNomor().equalsIgnoreCase(plat) && !s.isSelesai()) {
                s.setSelesai(true);
                s.setDibawaKabur(false);
            }
        }
        for (PesananMasuk p : antrianPesanan) {
            if (p.getPlatDialokasikan().equalsIgnoreCase(plat) && p.getStatus() == PesananMasuk.StatusPesanan.KABUR_HILANG) {
                p.setStatus(PesananMasuk.StatusPesanan.SELESAI);
            }
        }

        String hasil = String.format("SUKSES:✅ Relai Pemutus Arus Aktif! Mesin mati total di koordinat GPS terakhir (%.6f, %.6f). Tim penjemput berhasil mengamankan kembali unit %s [%s] ke garasi!",
                k.getLatitude(), k.getLongitude(), k.getMerk(), plat);

        if (krisisAktif != null && krisisAktif.plat.equalsIgnoreCase(plat)) {
            krisisAktif.hasilTindakan = hasil;
            krisisAktif = null;
        }

        return hasil;
    }

    public synchronized String aksiLaporPolisi(String plat, KasPerusahaan kas, PerusahaanRental rental)
            throws SaldoKasTidakCukupException {
        double biayaPolisi = 5000000.0; // Biaya operasional penyelidikan & laporan BAP
        kas.kurangiPengeluaran(biayaPolisi, "Biaya Laporan & Penyelidikan Polisi [Unit " + plat + "]");

        Kendaraan k = rental.findKendaraan(plat);
        boolean berhasilDitemukan = random.nextDouble() < 0.35; // 35% sukses, 65% gagal/mandek

        if (berhasilDitemukan && k != null) {
            k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
            k.setPenyewaSaatIni("-");
            for (SesiSewa s : daftarSesiSewa) {
                if (s.getPlatNomor().equalsIgnoreCase(plat)) s.setSelesai(true);
            }
            if (krisisAktif != null && krisisAktif.plat.equalsIgnoreCase(plat)) {
                krisisAktif.hasilTindakan = "Polisi berhasil menemukan mobil di gudang penadah. Unit dikembalikan ke garasi.";
                krisisAktif = null;
            }
            return "SUKSES:ALHAMDULILLAH! Tim kepolisian berhasil menggerebek lokasi sindikat dan mengembalikan unit " + plat + "!";
        } else {
            return "GAGAL:KECEWA! Setelah bayar biaya administrasi Rp 5.000.000, penyelidikan polisi nihil / lamban. Mobil masih belum ditemukan.";
        }
    }

    public synchronized String aksiByOneAkamsi(String plat, KasPerusahaan kas, PerusahaanRental rental)
            throws SaldoKasTidakCukupException {
        Kendaraan k = rental.findKendaraan(plat);
        boolean suksesNego = random.nextDouble() < 0.65; // 65% sukses negosiasi

        if (suksesNego && k != null) {
            double biayaTebusan = 2500000.0; // Uang rokok & mediasi ormas akamsi
            kas.kurangiPengeluaran(biayaTebusan, "Biaya Mediasi & Recovery Akamsi [Unit " + plat + "]");
            k.setStatusSewa(Kendaraan.StatusSewa.SIAP);
            k.setPenyewaSaatIni("-");
            for (SesiSewa s : daftarSesiSewa) {
                if (s.getPlatNomor().equalsIgnoreCase(plat)) s.setSelesai(true);
            }
            if (krisisAktif != null && krisisAktif.plat.equalsIgnoreCase(plat)) {
                krisisAktif.hasilTindakan = "Unit berhasil ditarik paksa dari penadah dengan uang tebusan damai Rp 2.500.000.";
                krisisAktif = null;
            }
            return "SUKSES:NEGO BERHASIL! Tim ormas & akamsi berhasil mengamankan kunci mobil. Anda membayar tebusan damai Rp 2.500.000.";
        } else {
            double biayaMedis = 1000000.0;
            kas.kurangiPengeluaran(biayaMedis, "Biaya Medis & Pengamanan Bentrok Akamsi");
            return "GAGAL:GAGAL & RICUH! Terjadi percekcokan di lokasi penadah. Unit dipindahkan ke lokasi tak dikenal!";
        }
    }

    public synchronized String aksiIkhlas(String plat, PerusahaanRental rental) {
        Kendaraan k = rental.findKendaraan(plat);
        if (k != null) {
            try {
                rental.getKas().kurangiPengeluaran(0, "Write-off Kerugian Total Penggelapan Unit " + plat + " (" + k.getMerk() + ")");
            } catch (SaldoKasTidakCukupException ignored) {}
            rental.hapusKendaraan(plat);
            for (SesiSewa s : daftarSesiSewa) {
                if (s.getPlatNomor().equalsIgnoreCase(plat)) {
                    s.setSelesai(true);
                    s.setDibawaKabur(true);
                }
            }
            if (krisisAktif != null && krisisAktif.plat.equalsIgnoreCase(plat)) {
                krisisAktif.hasilTindakan = "Unit " + plat + " resmi dihapus dari pembukuan (Total Asset Loss / Ikhlas).";
                krisisAktif = null;
            }
            return "SUKSES:IKHLAS TOTAL: Unit " + plat + " resmi dihapus dari pembukuan aset perusahaan sebagai kerugian penggelapan.";
        }
        return "Armada tidak ditemukan.";
    }

    private PesananMasuk cariPesanan(long id) {
        for (PesananMasuk p : antrianPesanan) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public void tambahPesanan(PesananMasuk p) {
        if (p != null) {
            antrianPesanan.add(p);
        }
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

