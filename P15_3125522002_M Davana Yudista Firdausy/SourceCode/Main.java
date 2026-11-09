package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input) {
        return baca(
            input,
            "Perubahan belum disimpan. Abaikan perubahan? Ketik YA: "
        ).equalsIgnoreCase("YA");
    }

    public static boolean menuTambah(Scanner input, Perpustakaan perpustakaan) {
        String kode = baca(input, "Masukkan kode buku  : ");
        String judul = baca(input, "Masukkan judul buku : ");

        try {
            perpustakaan.tambahBuku(new Buku(kode, judul));
            System.out.println("[SUKSES] Buku berhasil ditambahkan.");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("[GAGAL] " + e.getMessage());
            return false;
        }
    }

    public static void menuCari(Scanner input, Perpustakaan perpustakaan) {
        String kode = baca(input, "Masukkan kode buku yang dicari: ");
        Buku buku = perpustakaan.cariBuku(kode);

        if (buku != null) {
            System.out.println("[DITEMUKAN] " + buku.getKode() + " - " + buku.getJudul());
        } else {
            System.out.println("[INFO] Buku dengan kode tersebut tidak ditemukan.");
        }
    }

    public static void menuCariSebagian(Scanner input, Perpustakaan perpustakaan) {
        String kataKunci = baca(input, "Masukkan kata kunci judul: ");
        List<Buku> hasil = perpustakaan.cariBukuSebagianJudul(kataKunci);

        if (hasil.isEmpty()) {
            System.out.println("[INFO] Tidak ada buku yang judulnya memuat: '" + kataKunci + "'.");
        } else {
            System.out.println("[DITEMUKAN " + hasil.size() + " BUKU]:");
            for (Buku b : hasil) {
                System.out.println(" - " + b.getKode() + " : " + b.getJudul());
            }
        }
    }

    public static boolean menuHapus(Scanner input, Perpustakaan perpustakaan) {
        String kode = baca(input, "Masukkan kode buku yang akan dihapus: ");

        if (perpustakaan.hapusBuku(kode)) {
            System.out.println("[SUKSES] Buku berhasil dihapus.");
            return true;
        } else {
            System.out.println("[GAGAL] Buku tidak ditemukan.");
            return false;
        }
    }

    public static boolean menuSimpan(Perpustakaan perpustakaan, Path lokasi) {
        try {
            perpustakaan.simpan(lokasi);
            System.out.println("[SUKSES] Data berhasil disimpan ke " + lokasi);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("[GAGAL SIMPAN] " + e.getMessage());
            return false;
        }
    }

    public static boolean menuMuat(Perpustakaan perpustakaan, Path lokasi) {
        try {
            perpustakaan.muat(lokasi);
            System.out.println("[SUKSES] Data berhasil dimuat ulang dari " + lokasi);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("[GAGAL MUAT] " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        Perpustakaan perpustakaan = new Perpustakaan();
        Path lokasi = Paths.get("data", "data_sampel.txt");
        boolean berubah = false;
        Scanner input = new Scanner(System.in);

        try {
            if (java.nio.file.Files.exists(lokasi)) {
                perpustakaan.muat(lokasi);
                System.out.println("[INFO] Berkas sampel berhasil dimuat (" + perpustakaan.getJumlahBuku() + " buku).");
            }
        } catch (Exception e) {
            System.out.println("[PERINGATAN] Gagal memuat berkas: " + e.getMessage());
        }

        while (true) {
            System.out.println("\n=================================================");
            System.out.println(" DEMONSTRASI PROYEK PBO (MODUL 15)              ");
            System.out.println(" Mahasiswa: M. Davana Yudista Firdausy          ");
            System.out.println(" Status Data: " + (berubah ? "[TERDAPAT PERUBAHAN]" : "[TERSINKRONISASI]"));
            System.out.println("=================================================");
            System.out.println("1. Tambah Buku");
            System.out.println("2. Tampilkan Semua Buku");
            System.out.println("3. Cari Buku Berdasarkan Kode");
            System.out.println("4. Cari Buku Berdasarkan Sebagian Judul (Fitur Baru)");
            System.out.println("5. Hapus Buku");
            System.out.println("6. Simpan ke File");
            System.out.println("7. Muat Ulang dari File");
            System.out.println("0. Keluar");
            System.out.println("-------------------------------------------------");

            String pilihan = baca(input, "Pilih menu (0-7): ");

            if (pilihan.equals("1")) {
                if (menuTambah(input, perpustakaan)) berubah = true;
            } else if (pilihan.equals("2")) {
                perpustakaan.tampilkanBuku();
            } else if (pilihan.equals("3")) {
                menuCari(input, perpustakaan);
            } else if (pilihan.equals("4")) {
                menuCariSebagian(input, perpustakaan);
            } else if (pilihan.equals("5")) {
                if (menuHapus(input, perpustakaan)) berubah = true;
            } else if (pilihan.equals("6")) {
                if (menuSimpan(perpustakaan, lokasi)) berubah = false;
            } else if (pilihan.equals("7")) {
                if (berubah && !konfirmasi(input)) {
                    System.out.println("[BATAL] Pemuatan ulang dibatalkan.");
                    continue;
                }
                if (menuMuat(perpustakaan, lokasi)) berubah = false;
            } else if (pilihan.equals("0")) {
                if (berubah && !konfirmasi(input)) {
                    System.out.println("[BATAL] Pembatalan keluar.");
                    continue;
                }
                System.out.println("Demonstrasi selesai. Terima kasih.");
                break;
            } else {
                System.out.println("[ERROR] Menu tidak valid.");
            }
        }
        input.close();
    }
}
