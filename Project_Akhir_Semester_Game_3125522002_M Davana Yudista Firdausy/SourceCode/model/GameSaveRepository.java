package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Locale;

/**
 * P12-P13: Repository Penyimpanan State Simulasi Game ke File Teks / JSON.
 */
public class GameSaveRepository {

    public void simpan(Path path, PerusahaanRental rental) throws IOException {
        if (path.getParent() != null && !Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("# FLEETSUMENEP TYCOON SAVEGAME STATE (P12)\n");
        sb.append(String.format(Locale.US, "METADATA|%s|%.0f|%d|%d|%d|%d|%d|%.1f\n",
            rental.getNamaGarasi(),
            rental.getKas().getSaldo(),
            rental.getKapasitasGarasi(),
            rental.getJumlahSupir(),
            rental.getWaktu().getHari(),
            rental.getWaktu().getJam(),
            rental.getWaktu().getMenit(),
            rental.getWaktu().getKecepatan()
        ));

        for (Kendaraan k : rental.getArmada()) {
            sb.append(String.format(Locale.US, "ARMADA|%s|%s|%s|%.0f|%.0f|%d|%.1f|%b|%s|%s|%s|%.6f|%.6f\n",
                k.getKategori(),
                k.getPlatNomor(),
                k.getMerk(),
                k.getHargaBeliRealistis(),
                k.getTarifHarian(),
                k.getOdoMeterKm(),
                k.getKondisiMesin(),
                k.isGPSInstalled(),
                k.getStatusSewa().name(),
                k.getPenyewaSaatIni(),
                k.getSlotGarasi(),
                k.getLatitude(),
                k.getLongitude()
            ));
        }

        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    public void muat(Path path, PerusahaanRental target) throws IOException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File savegame tidak ditemukan: " + path);
        }

        java.util.List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] p = line.split("\\|");
            if (p[0].equals("METADATA") && p.length >= 8) {
                double saldo = Double.parseDouble(p[2]);
                double diff = saldo - target.getKas().getSaldo();
                if (diff > 0) {
                    target.getKas().tambahPemasukan(diff, "Sinkronisasi Savegame");
                }
            } else if (p[0].equals("ARMADA") && p.length >= 13) {
                String plat = p[2];
                Kendaraan exist = target.findKendaraan(plat);
                if (exist != null) {
                    exist.setTarifHarian(Double.parseDouble(p[5]));
                    exist.setStatusSewa(Kendaraan.StatusSewa.valueOf(p[9]));
                    exist.setPenyewaSaatIni(p[10]);
                    if (Boolean.parseBoolean(p[8])) {
                        exist.pasangGPS();
                    }
                }
            }
        }
    }
}

