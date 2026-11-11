package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.HasilImpor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Titik masuk program: impor data awal, lalu menjalankan menu konsol.
 * Argumen pertama (opsional) adalah path file CSV; default data/putusan-50.csv.
 */
public class Main {

    public static void main(String[] args) {
        Path file = Path.of(args.length > 0 ? args[0] : MenuKonsol.FILE_DEFAULT);
        ArsipPutusan aplikasi = new ArsipPutusan();

        try {
            HasilImpor hasil = aplikasi.impor(file);
            System.out.println("=== Impor " + file + " ===");
            System.out.println(hasil);
            System.out.println();

            BufferedReader masukan = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            new MenuKonsol(aplikasi, masukan, System.out).jalankan();
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
        }
    }
}