package SourceCode;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;
import model.Kendaraan;
import model.MobilReguler;
import model.MobilVIP;
import model.PerusahaanRental;
import model.exception.KapasitasPenuhException;
import model.exception.KendaraanRusakException;
import model.exception.KendaraanTidakTersediaException;

/**
 * CLI Interactive Menu Launcher - Modul 13 & Capstone Project
 * Mengintegrasikan seluruh operasi rental mobil, manajemen koleksi objek,
 * validasi defensif, serta persistensi file dengan mekanisme Dirty-State Tracking.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 * Kelas: A D3 TI-SM | PENS PSDKU Sumenep
 */
public class CliMain {

    private static final Path FILE_STORE = Paths.get("data", "armada_store.txt");

    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input, String pesan) {
        return baca(input, pesan + " (Ketik 'YA' untuk konfirmasi): ").equalsIgnoreCase("YA");
    }

    public static void main(String[] args) {
        PerusahaanRental rental = new PerusahaanRental("FleetSumenep Depot PSDKU");
        boolean berubah = false;

        System.out.println("==========================================================================");
        System.out.println("   FLEETSUMENEP CONSOLE MANAGEMENT SYSTEM (CAPSTONE & MODUL 13)");
        System.out.println("   Rental Car Fleet Monitoring, Bay Lots & Persistence Repository");
        System.out.println("   Mahasiswa: M. Davana Yudista Firdausy | NRP: 3125522002 | PENS PSDKU");
        System.out.println("==========================================================================");

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n--------------------------------------------------------------------------");
                System.out.println("MENU UTAMA SISTEM:");
                System.out.println("1. Tampilkan Seluruh Armada (Overview)");
                System.out.println("2. Tambah Armada Baru ke Pool Depot");
                System.out.println("3. Cari Armada Berdasarkan Plat Nomor");
                System.out.println("4. Transaksi Penyewaan Armada (Validasi Defensif)");
                System.out.println("5. Pengembalian Armada ke Pool Garasi (Standby)");
                System.out.println("6. Hapus Unit Armada dari Sistem");
                System.out.println("7. Simpan Seluruh Data ke File Teks (" + FILE_STORE + ")");
                System.out.println("8. Muat Ulang Data dari File Teks");
                System.out.println("0. Keluar dari Aplikasi");
                System.out.println("--------------------------------------------------------------------------");

                String statusInfo = berubah ? "[Status: Ada Perubahan Belum Disimpan]" : "[Status: Bersih (Tersimpan)]";
                System.out.println(statusInfo);
                String pilihan = baca(input, "Pilih Menu [0-8]: ");

                try {
                    switch (pilihan) {
                        case "1":
                            menuTampilkan(rental);
                            break;

                        case "2":
                            if (menuTambah(input, rental)) {
                                berubah = true;
                            }
                            break;

                        case "3":
                            menuCari(input, rental);
                            break;

                        case "4":
                            if (menuSewa(input, rental)) {
                                berubah = true;
                            }
                            break;

                        case "5":
                            if (menuKembalikan(input, rental)) {
                                berubah = true;
                            }
                            break;

                        case "6":
                            if (menuHapus(input, rental)) {
                                berubah = true;
                            }
                            break;

                        case "7":
                            menuSimpan(rental);
                            berubah = false;
                            break;

                        case "8":
                            if (berubah && !konfirmasi(input, "Perubahan saat ini belum disimpan ke file. Lanjutkan muat ulang?")) {
                                System.out.println("[BATAL] Pemuatan ulang dibatalkan. Data di memori dipertahankan.");
                                break;
                            }
                            menuMuat(rental);
                            berubah = false;
                            break;

                        case "0":
                            if (berubah && !konfirmasi(input, "Perubahan belum disimpan. Yakin ingin keluar?")) {
                                System.out.println("[BATAL] Keluar dibatalkan. Kembali ke menu utama.");
                                break;
                            }
                            System.out.println("\n[SELESAI] Terima kasih telah menggunakan FleetSumenep Console System.");
                            return;

                        default:
                            System.out.println("[PERINGATAN] Pilihan menu '" + pilihan + "' tidak tersedia. Silakan pilih 0-8.");
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("[VALIDASI DITOLAK] " + e.getMessage());
                } catch (KendaraanTidakTersediaException e) {
                    System.err.println("[SEWA DITOLAK] " + e.getMessage());
                } catch (KendaraanRusakException e) {
                    System.err.println("[KONDISI DITOLAK] " + e.getMessage());
                } catch (KapasitasPenuhException e) {
                    System.err.println("[KAPASITAS PENUH] " + e.getMessage());
                } catch (IllegalStateException e) {
                    System.err.println("[STATE TIDAK VALID] " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("[OPERASI FILE GAGAL] " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[ERROR SISTEM] " + e.getMessage());
                }
            }
        }
    }

    private static void menuTampilkan(PerusahaanRental rental) {
        System.out.println("\n>>> DAFTAR ARMADA & SLOT BAY PARKIR DEPOT SUMENEP <<<");
        rental.tampilkanSemuaKendaraan();
    }

    private static boolean menuTambah(Scanner input, PerusahaanRental rental) throws KapasitasPenuhException {
        System.out.println("\n>>> REGISTRASI KENDARAAN BARU <<<");
        String plat = baca(input, "Masukkan Plat Nomor (e.g. M 1234 XY): ");
        String merk = baca(input, "Masukkan Merk & Model Mobil         : ");
        String kat = baca(input, "Kategori (1. Reguler / 2. VIP)      : ");
        String kategori = kat.equals("2") ? "VIP" : "Reguler";

        double tarif = Double.parseDouble(baca(input, "Tarif Sewa Per Hari (Rp)            : "));
        int odo = Integer.parseInt(baca(input, "Odometer Awal (Km)                  : "));
        String kondisi = baca(input, "Kondisi Mesin (Sangat Baik/Baik/Dalam Perbaikan): ");
        String catatan = baca(input, "Catatan Fisik Kendaraan             : ");

        Kendaraan baru;
        if ("VIP".equalsIgnoreCase(kategori)) {
            double suhu = Double.parseDouble(baca(input, "Set Suhu Kabin Dingin (°C)          : "));
            baru = new MobilVIP(plat, merk, -7.0150, 113.8650, 100, suhu, tarif, odo, kondisi, catatan);
        } else {
            baru = new MobilReguler(plat, merk, -7.0150, 113.8650, 100, tarif, odo, kondisi, catatan);
        }

        boolean sukses = rental.tambahKendaraan(baru);
        if (sukses) {
            System.out.println("[SUKSES] Kendaraan " + plat + " (" + merk + ") berhasil ditambahkan ke " + baru.getSlotParkir());
            return true;
        }
        return false;
    }

    private static void menuCari(Scanner input, PerusahaanRental rental) {
        System.out.println("\n>>> PENCARIAN ARMADA <<<");
        String plat = baca(input, "Masukkan Plat Nomor yang dicari: ");
        Kendaraan k = rental.cariKendaraan(plat);

        if (k != null) {
            System.out.println("[DITEMUKAN] Data Detail Kendaraan:");
            System.out.println("  - Plat Nomor   : " + k.getPlatNomor());
            System.out.println("  - Merk / Seri  : " + k.getMerk() + " (" + k.getKategori() + ")");
            System.out.println("  - Status Sewa  : " + (k.isDisewa() ? "DISEWA (Penyewa: " + k.getPenyewa() + ")" : "STANDBY"));
            System.out.println("  - Bay Parkir   : " + k.getSlotParkir());
            System.out.printf(Locale.US, "  - Tarif Harian : Rp %,.0f / 24 Jam\n", k.getTarifPerHari());
            System.out.println("  - Kondisi Mesin: " + k.getKondisiMesin());
            System.out.println("  - Odometer     : " + k.getOdoMeterKm() + " Km");
            System.out.println("  - Catatan Fisik: " + k.getCatatanFisik());
            if (k instanceof MobilVIP) {
                System.out.printf(Locale.US, "  - Suhu Kabin   : %.1f °C\n", ((MobilVIP) k).getSuhuKabin());
            }
        } else {
            System.out.println("[TIDAK DITEMUKAN] Armada dengan plat " + plat + " tidak terdaftar di sistem.");
        }
    }

    private static boolean menuSewa(Scanner input, PerusahaanRental rental)
            throws KendaraanTidakTersediaException, KendaraanRusakException {
        System.out.println("\n>>> TRANSAKSI PENYEWAAN ARMADA <<<");
        String plat = baca(input, "Masukkan Plat Nomor Unit yang Disewa: ");
        String penyewa = baca(input, "Nama Pelanggan Penyewa              : ");
        int durasi = Integer.parseInt(baca(input, "Durasi Sewa (Jam)                   : "));
        String supirStr = baca(input, "Gunakan Layanan Pengemudi? (Y/N)    : ");
        boolean denganSupir = supirStr.equalsIgnoreCase("Y");
        double diskon = Double.parseDouble(baca(input, "Diskon Khusus (%) [0-100]           : "));

        String hasil = rental.prosesSewa(plat, penyewa, durasi, denganSupir, diskon);
        System.out.println("[TRANSAKSI SEWA] " + hasil);
        return true;
    }

    private static boolean menuKembalikan(Scanner input, PerusahaanRental rental) {
        System.out.println("\n>>> PENGEMBALIAN ARMADA KE POOL DEPOT <<<");
        String plat = baca(input, "Masukkan Plat Nomor Unit yang Dikembalikan: ");
        boolean sukses = rental.kembalikanArmada(plat);
        if (sukses) {
            Kendaraan k = rental.cariKendaraan(plat);
            System.out.println("[SUKSES] Armada " + plat + " berhasil kembali ke pool garasi pada " + 
                (k != null ? k.getSlotParkir() : "Depot") + " dalam status STANDBY.");
            return true;
        }
        return false;
    }

    private static boolean menuHapus(Scanner input, PerusahaanRental rental) {
        System.out.println("\n>>> PENGHAPUSAN UNIT DARI SISTEM <<<");
        String plat = baca(input, "Masukkan Plat Nomor Unit yang akan Dihapus: ");
        if (konfirmasi(input, "Apakah Anda yakin ingin menghapus permanen unit " + plat + "?")) {
            boolean sukses = rental.hapusKendaraan(plat);
            if (sukses) {
                System.out.println("[SUKSES] Armada " + plat + " berhasil dihapus dari sistem.");
                return true;
            }
        } else {
            System.out.println("[BATAL] Penghapusan dibatalkan.");
        }
        return false;
    }

    private static void menuSimpan(PerusahaanRental rental) throws IOException {
        System.out.println("\n>>> PENYIMPANAN DATA PERSISTEN KE FILE TEKS <<<");
        rental.simpanKeFile(FILE_STORE);
        System.out.println("[SUKSES] Seluruh data armada (" + rental.getArmada().size() + 
            " unit) berhasil disimpan ke berkas: " + FILE_STORE);
    }

    private static void menuMuat(PerusahaanRental rental) throws IOException {
        System.out.println("\n>>> PEMUATAN ULANG DATA DARI FILE TEKS <<<");
        rental.muatDariFile(FILE_STORE);
        System.out.println("[SUKSES] Data armada berhasil dimuat ulang dari file. Total unit saat ini: " + 
            rental.getArmada().size() + " unit.");
    }
}
