package id.ac.umm.kelompok06.aplikasi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuKonsolTest {

    private ArsipPutusan aplikasi;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));
    }

    /** Menjalankan menu dengan input berskrip (satu jawaban per baris) dan mengembalikan output-nya. */
    private String jalankan(String... jawaban) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream keluaran = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        BufferedReader masukan = new BufferedReader(new StringReader(String.join("\n", jawaban) + "\n"));
        new MenuKonsol(aplikasi, masukan, keluaran).jalankan();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Menu tampil dengan jumlah putusan dan berhenti di pilihan 0")
    void keluar() throws IOException {
        String out = jalankan("0");
        assertTrue(out.contains("(50 putusan)"));
        assertTrue(out.contains("Selesai."));
    }

    @Test
    @DisplayName("Cari caseId lewat menu 5")
    void cari() throws IOException {
        String out = jalankan("5", "1", "P012", "0");
        assertTrue(out.contains("'P012' ditemukan di indeks 11 (12 langkah perbandingan)"));
    }

    @Test
    @DisplayName("Hapus lewat menu 4 mengurangi size, caseId tidak ada tidak mengubah arsip")
    void hapus() throws IOException {
        String out = jalankan("4", "P030", "4", "P099", "0");
        assertTrue(out.contains("Dihapus: P030 (size sekarang 49)"));
        assertTrue(out.contains("P099 tidak ditemukan"));
        assertEquals(49, aplikasi.getArsip().size());
    }

    @Test
    @DisplayName("Filter gabungan lewat menu 6 menghasilkan 5 putusan")
    void filterGabungan() throws IOException {
        String out = jalankan("6", "sabu", "112(1)", "0", "1", "60", "72", "0");
        assertTrue(out.contains("5 putusan cocok"));
        assertTrue(out.contains("P021") && out.contains("P046"));
    }

    @Test
    @DisplayName("Input tidak valid ditolak dengan pesan, arsip tetap konsisten (skenario 7)")
    void inputTidakValid() throws IOException {
        String out = jalankan("abc", "3", "P051,rusak", "6", "", "", "x", "", "", "", "0");
        assertTrue(out.contains("Pilihan 'abc' tidak dikenal"));
        assertTrue(out.contains("Data ditolak, arsip tidak berubah"));
        assertTrue(out.contains("Gagal: berat minimum harus angka"));
        assertEquals(50, aplikasi.getArsip().size());
        aplikasi.getArsip().validateInvariant();
    }

    @Test
    @DisplayName("Input habis (EOF) menghentikan menu tanpa error")
    void inputHabis() throws IOException {
        String out = jalankan("2");
        assertTrue(out.contains("Arsip berisi 50 putusan"));
        assertTrue(out.contains("Selesai."));
    }

    @Test
    @DisplayName("Info struktur menampilkan size, kapasitas, dan invariant OK")
    void infoStruktur() throws IOException {
        String out = jalankan("11", "0");
        assertTrue(out.contains("size=50, kapasitas=80"));
        assertTrue(out.contains("validateInvariant() kedua struktur: OK"));
    }
}