package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.HasilImpor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Titik masuk program. Versi awal: impor lalu tampil.
 * Menu lengkap ditambahkan bertahap seiring fitur lain selesai.
 */
public class Main {

    public static void main(String[] args) {
        Path file = Path.of(args.length > 0 ? args[0] : "data/putusan-50.csv");
        ArsipPutusan aplikasi = new ArsipPutusan();

        try {
            HasilImpor hasil = aplikasi.impor(file);
            System.out.println("=== Impor " + file + " ===");
            System.out.println(hasil);
            System.out.println();
            System.out.println(aplikasi.tampil());
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
        }
    }
}