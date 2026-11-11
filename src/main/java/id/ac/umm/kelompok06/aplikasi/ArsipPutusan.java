package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.HasilImpor;
import id.ac.umm.kelompok06.io.PutusanCsvParser;
import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;
import id.ac.umm.kelompok06.struktur.StackRiwayat;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Pusat aplikasi: menyimpan seluruh putusan di struktur utama (ArrayDinamis)
 * dan mencatat setiap operasi pengguna di struktur sekunder (StackRiwayat).
 * Fitur lain (cari, filter, urut, agregasi, ekspor) bekerja di atas getArsip().
 */
public class ArsipPutusan {

    private static final DateTimeFormatter FORMAT_WAKTU =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ArrayDinamis<Putusan> arsip = new ArrayDinamis<>();
    private final StackRiwayat<String> riwayat = new StackRiwayat<>();
    private final PutusanCsvParser parser = new PutusanCsvParser();

    /**
     * Fitur Impor: mengosongkan arsip lalu memuat ulang dari file CSV.
     * Record tidak valid ditolak dan dilaporkan di HasilImpor, program tetap berjalan.
     */
    public HasilImpor impor(Path file) throws IOException {
        arsip.clear();
        HasilImpor hasil = parser.bacaFile(file, arsip::add);
        catat("IMPOR", file.getFileName() + ": " + hasil.getJumlahDiterima()
                + " diterima, " + hasil.getJumlahDitolak() + " ditolak");
        return hasil;
    }

    /**
     * Fitur Tampil: ringkasan semua putusan sesuai urutan di struktur utama.
     * Dibaca dengan get(i) dari indeks 0 sampai size-1.
     */
    public String tampil() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Arsip berisi %d putusan%n", arsip.size()));
        for (int i = 0; i < arsip.size(); i++) {
            sb.append(String.format("%3d. %s%n", i + 1, arsip.get(i)));
        }
        catat("TAMPIL", arsip.size() + " putusan");
        return sb.toString();
    }

    /** Struktur utama, dipakai oleh fitur-fitur lain. */
    public ArrayDinamis<Putusan> getArsip() {
        return arsip;
    }

    /** Struktur sekunder untuk fitur Audit. */
    public StackRiwayat<String> getRiwayat() {
        return riwayat;
    }

    /** Merekam satu operasi pengguna ke riwayat (puncak = operasi terbaru). */
    public void catat(String jenisOperasi, String detail) {
        riwayat.push(LocalDateTime.now().format(FORMAT_WAKTU) + " | " + jenisOperasi + " | " + detail);
    }
}