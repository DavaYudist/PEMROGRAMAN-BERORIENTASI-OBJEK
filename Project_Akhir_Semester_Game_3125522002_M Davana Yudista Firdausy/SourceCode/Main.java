package SourceCode;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.*;
import netscape.javascript.JSObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main JavaFX WebView Application Host untuk Indonesian Car Rental Tycoon.
 * Menghubungkan Backend Java OOP murni ke Web UI responsif via DesktopBridge.
 */
public class Main extends Application {

    private PerusahaanRental rental;
    private WebView webView;
    private WebEngine webEngine;
    private Timer gameTimer;
    private final GameSaveRepository saveRepo = new GameSaveRepository();

    @Override
    public void start(Stage primaryStage) {
        // Inisialisasi Perusahaan Rental Tycoon (Modal awal Rp 250.000.000)
        rental = new PerusahaanRental("Garasi Magang Sumenep", 250000000.0);

        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // Setup Desktop Bridge
        DesktopBridge bridge = new DesktopBridge();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject win = (JSObject) webEngine.executeScript("window");
                    win.setMember("javaBridge", bridge);
                    pushGameState();
                } catch (Exception e) {
                    System.err.println("Gagal menghubungkan javaBridge: " + e.getMessage());
                }
            }
        });

        // Load local index.html
        File htmlFile = new File("SourceCode/web/index.html");
        if (htmlFile.exists()) {
            webEngine.load(htmlFile.toURI().toString());
        } else {
            webEngine.loadContent("<html><body style='font-family:sans-serif;'><h2>Berkas index.html tidak ditemukan di SourceCode/web/</h2></body></html>");
        }

        StackPane root = new StackPane(webView);
        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setTitle("FleetSumenep Tycoon - Indonesian Car Rental Management Simulation");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (gameTimer != null) gameTimer.cancel();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        // Start In-Game Simulation Clock Loop
        startGameSimulationLoop();
    }

    private void startGameSimulationLoop() {
        gameTimer = new Timer(true);
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            private int orderCounter = 0;

            @Override
            public void run() {
                if (!rental.getWaktu().isPaused() && rental.getWaktu().getKecepatan() > 0) {
                    int deltaMenit = (int) (15 * rental.getWaktu().getKecepatan());
                    rental.stepSimulation(deltaMenit);

                    orderCounter++;
                    // Generator pesanan masuk baru setiap ~12 jam in-game
                    if (orderCounter >= 6) {
                        orderCounter = 0;
                        if (rental.getManajerPesanan().getAntrianPesanan().size() < 8) {
                            rental.getManajerPesanan().generatePesananOtomatis(
                                rental.getWaktu().getHari(), rental.getWaktu().getJam());
                        }
                    }

                    pushGameState();
                }
            }
        }, 1000, 1000);
    }

    private void pushGameState() {
        String json = rental.getGameStateJson();
        Platform.runLater(() -> {
            try {
                webEngine.executeScript("if (typeof updateGameState === 'function') { updateGameState(" + json + "); }");
            } catch (Exception ignored) {}
        });
    }

    /**
     * Bridge Object untuk interaksi dua arah JavaScript ke Java
     */
    public class DesktopBridge {

        public String getGameState() {
            return rental.getGameStateJson();
        }

        public void terimaPesanan(long id, String plat) {
            Platform.runLater(() -> {
                try {
                    rental.getManajerPesanan().terimaPesanan(id, plat, rental.getWaktu(), rental.getKas(), rental);
                    pushGameState();
                } catch (Exception e) {
                    showToast("Gagal menerima pesanan: " + e.getMessage(), false);
                }
            });
        }

        public void tolakPesanan(long id) {
            Platform.runLater(() -> {
                rental.getManajerPesanan().tolakPesanan(id);
                pushGameState();
            });
        }

        public void mintaJaminan(long id) {
            Platform.runLater(() -> {
                rental.getManajerPesanan().mintaJaminan(id);
                pushGameState();
            });
        }

        public void tawarkanSopir(long id) {
            Platform.runLater(() -> {
                rental.getManajerPesanan().tawarkanSopir(id);
                pushGameState();
            });
        }

        public void beliMobil(String kategori, String plat, String merk, double harga, double tarif) {
            Platform.runLater(() -> {
                try {
                    rental.beliMobilBaru(kategori, plat, merk, harga, tarif);
                    showToast("Sukses membeli " + merk + " [" + plat + "]!", true);
                    pushGameState();
                } catch (Exception e) {
                    showToast("Pembelian gagal: " + e.getMessage(), false);
                }
            });
        }

        public void pasangGPS(String plat) {
            Platform.runLater(() -> {
                try {
                    rental.pasangModulGPS(plat);
                    showToast("Modul GPS Pro & Remote Cutoff berhasil dipasang pada " + plat, true);
                    pushGameState();
                } catch (Exception e) {
                    showToast("Gagal pasang GPS: " + e.getMessage(), false);
                }
            });
        }

        public void servisMobil(String plat) {
            Platform.runLater(() -> {
                try {
                    rental.servisKendaraan(plat);
                    showToast("Servis mesin & penggantian oli selesai untuk " + plat, true);
                    pushGameState();
                } catch (Exception e) {
                    showToast("Gagal servis: " + e.getMessage(), false);
                }
            });
        }

        public void perluasGarasi() {
            Platform.runLater(() -> {
                try {
                    rental.perluasKapasitasGarasi();
                    showToast("Kapasitas garasi diperluas +4 Bay Slot!", true);
                    pushGameState();
                } catch (Exception e) {
                    showToast("Gagal ekspansi garasi: " + e.getMessage(), false);
                }
            });
        }

        public void rekrutSupir() {
            Platform.runLater(() -> {
                try {
                    rental.rekrutSupirBaru();
                    showToast("Supir baru berhasil direkrut!", true);
                    pushGameState();
                } catch (Exception e) {
                    showToast("Gagal rekrut supir: " + e.getMessage(), false);
                }
            });
        }

        public void setKecepatan(double speed) {
            Platform.runLater(() -> {
                rental.getWaktu().setKecepatan(speed);
                pushGameState();
            });
        }

        public void togglePause() {
            Platform.runLater(() -> {
                rental.getWaktu().togglePause();
                pushGameState();
            });
        }

        // Resolusi Krisis
        public void aksiTelepon(String plat) {
            Platform.runLater(() -> {
                String hasil = rental.getManajerPesanan().aksiTeleponPenyewa(plat);
                showToast(hasil, false);
                pushGameState();
            });
        }

        public void aksiMatikanMesin(String plat) {
            Platform.runLater(() -> {
                String hasil = rental.getManajerPesanan().aksiMatikanMesinRemote(plat, rental);
                showToast(hasil, hasil.contains("SUKSES"));
                pushGameState();
            });
        }

        public void aksiLaporPolisi(String plat) {
            Platform.runLater(() -> {
                try {
                    String hasil = rental.getManajerPesanan().aksiLaporPolisi(plat, rental.getKas(), rental);
                    showToast(hasil, hasil.contains("ALHAMDULILLAH"));
                    pushGameState();
                } catch (Exception e) {
                    showToast("Laporan polisi gagal: " + e.getMessage(), false);
                }
            });
        }

        public void aksiByOne(String plat) {
            Platform.runLater(() -> {
                try {
                    String hasil = rental.getManajerPesanan().aksiByOneAkamsi(plat, rental.getKas(), rental);
                    showToast(hasil, hasil.contains("BERHASIL"));
                    pushGameState();
                } catch (Exception e) {
                    showToast("Aksi akamsi gagal: " + e.getMessage(), false);
                }
            });
        }

        public void aksiIkhlas(String plat) {
            Platform.runLater(() -> {
                String hasil = rental.getManajerPesanan().aksiIkhlas(plat, rental);
                showToast(hasil, true);
                pushGameState();
            });
        }

        public void simpanKeFile() {
            Platform.runLater(() -> {
                try {
                    Path p = Paths.get("data", "gamestate.txt");
                    saveRepo.simpan(p, rental);
                    showToast("State permainan berhasil disimpan ke " + p.toString() + "!", true);
                } catch (Exception e) {
                    showToast("Gagal simpan game: " + e.getMessage(), false);
                }
            });
        }

        public void muatDariFile() {
            Platform.runLater(() -> {
                try {
                    Path p = Paths.get("data", "gamestate.txt");
                    saveRepo.muat(p, rental);
                    showToast("State permainan berhasil dimuat dari file!", true);
                    pushGameState();
                } catch (Exception e) {
                    showToast("Gagal memuat game: " + e.getMessage(), false);
                }
            });
        }

        private void showToast(String msg, boolean isSuccess) {
            String sanitized = msg.replace("'", "\\'").replace("\n", " ");
            webEngine.executeScript("if (typeof showToastAlert === 'function') { showToastAlert('" + sanitized + "', " + isSuccess + "); }");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
