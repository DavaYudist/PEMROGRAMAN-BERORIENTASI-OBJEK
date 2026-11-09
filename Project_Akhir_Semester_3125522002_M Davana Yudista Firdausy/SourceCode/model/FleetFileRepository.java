package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Repository Persistensi File Teks (P12 & Capstone Project)
 * Bertanggung jawab menyimpan dan memuat kumpulan objek armada ke berkas teks
 * dengan format pipe-delimited (|).
 * Menerapkan prinsip pemisahan tanggung jawab (Separation of Concerns) dan validasi defensif.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class FleetFileRepository {

    /**
     * Menyimpan seluruh armada ke file teks.
     * Melakukan validasi karakter terlarang sebelum penulisan.
     */
    public void simpan(Path lokasi, List<Kendaraan> armada) throws IOException {
        if (lokasi == null) {
            throw new IllegalArgumentException("Lokasi file penyimpanan tidak boleh null.");
        }
        if (armada == null) {
            throw new IllegalArgumentException("Daftar armada tidak boleh null.");
        }

        List<String> barisList = new ArrayList<>();

        for (Kendaraan k : armada) {
            String plat = k.getPlatNomor();
            String merk = k.getMerk();
            String kategori = k.getKategori();
            double lat = k.getLat();
            double lng = k.getLng();
            int baterai = k.getBaterai();
            double tarif = k.getTarifPerHari();
            int odo = k.getOdoMeterKm();
            String kondisi = k.getKondisiMesin();
            String catatan = k.getCatatanFisik();
            String slot = k.getSlotParkir();
            String status = k.isDisewa() ? "DISEWA" : "STANDBY";
            double suhu = (k instanceof MobilVIP) ? ((MobilVIP) k).getSuhuKabin() : 20.0;
            String penyewa = k.getPenyewa();
            boolean denganSupir = k.isDenganSupir();

            // Validasi karakter pemisah terlarang
            String[] teksValidasi = new String[]{plat, merk, kondisi, catatan, slot, penyewa};
            for (String str : teksValidasi) {
                if (str != null && (str.contains("|") || str.contains("\n") || str.contains("\r"))) {
                    throw new IllegalArgumentException("Data armada " + plat + " mengandung pemisah terlarang ('|' atau ganti baris).");
                }
            }

            // Format baris 15 kolom:
            // plat|merk|kategori|lat|lng|baterai|tarif|odo|kondisi|catatan|slot|status|suhu|penyewa|denganSupir
            String baris = String.join("|",
                plat,
                merk,
                kategori,
                String.valueOf(lat),
                String.valueOf(lng),
                String.valueOf(baterai),
                String.valueOf(tarif),
                String.valueOf(odo),
                kondisi,
                catatan,
                slot,
                status,
                String.valueOf(suhu),
                (penyewa != null ? penyewa : ""),
                String.valueOf(denganSupir)
            );

            barisList.add(baris);
        }

        if (lokasi.getParent() != null && !Files.exists(lokasi.getParent())) {
            Files.createDirectories(lokasi.getParent());
        }

        Files.write(lokasi, barisList, StandardCharsets.UTF_8);
    }

    /**
     * Membaca file teks dan merekonstruksi kembali kumpulan objek Kendaraan.
     * Menggunakan koleksi penampung sementara (defensive loading) agar koleksi aktif
     * tidak rusak apabila file mengalami korupsi data di tengah baris.
     */
    public List<Kendaraan> muat(Path lokasi) throws IOException {
        if (lokasi == null || !Files.exists(lokasi)) {
            throw new IOException("File data tidak ditemukan pada lokasi: " + lokasi);
        }

        List<String> barisList = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        List<Kendaraan> hasilSementara = new ArrayList<>();
        Set<String> platTerdaftar = new HashSet<>();

        for (int i = 0; i < barisList.size(); i++) {
            String baris = barisList.get(i).trim();
            if (baris.isEmpty()) continue; // Abaikan baris kosong

            String[] bagian = baris.split("\\|", -1);
            if (bagian.length != 15) {
                throw new IOException("Format baris salah pada baris ke-" + (i + 1) + 
                    " (Ekspektasi 15 kolom, ditemukan " + bagian.length + " kolom).");
            }

            try {
                String plat = bagian[0].trim();
                String merk = bagian[1].trim();
                String kategori = bagian[2].trim();
                double lat = Double.parseDouble(bagian[3].trim());
                double lng = Double.parseDouble(bagian[4].trim());
                int baterai = Integer.parseInt(bagian[5].trim());
                double tarif = Double.parseDouble(bagian[6].trim());
                int odo = Integer.parseInt(bagian[7].trim());
                String kondisi = bagian[8].trim();
                String catatan = bagian[9].trim();
                String slot = bagian[10].trim();
                String status = bagian[11].trim();
                double suhu = Double.parseDouble(bagian[12].trim());
                String penyewa = bagian[13].trim();
                boolean denganSupir = Boolean.parseBoolean(bagian[14].trim());

                if (platTerdaftar.contains(plat.toUpperCase())) {
                    throw new IOException("Duplikasi nomor plat terdeteksi pada baris " + (i + 1) + ": " + plat);
                }

                Kendaraan k;
                if ("VIP".equalsIgnoreCase(kategori)) {
                    k = new MobilVIP(plat, merk, lat, lng, baterai, suhu, tarif, odo, kondisi, catatan);
                } else {
                    k = new MobilReguler(plat, merk, lat, lng, baterai, tarif, odo, kondisi, catatan);
                }

                k.setSlotParkir(slot);
                if ("DISEWA".equalsIgnoreCase(status)) {
                    k.setDisewa(true);
                    k.setPenyewa(!penyewa.isEmpty() ? penyewa : "Pelanggan Prioritas");
                    k.setDenganSupir(denganSupir);
                } else {
                    k.setDisewa(false);
                    k.setPenyewa("");
                    k.setDenganSupir(false);
                }

                hasilSementara.add(k);
                platTerdaftar.add(plat.toUpperCase());

            } catch (NumberFormatException e) {
                throw new IOException("Konversi angka gagal pada baris ke-" + (i + 1) + ": " + e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                throw new IOException("Data domain tidak valid pada baris ke-" + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        return hasilSementara;
    }
}
