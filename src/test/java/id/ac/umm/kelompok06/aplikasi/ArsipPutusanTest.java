package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.HasilImpor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArsipPutusanTest {

    private ArsipPutusan aplikasi;

    @BeforeEach
    void siapkan() {
        aplikasi = new ArsipPutusan();
    }

    @Test
    @DisplayName("Skenario 1: impor 50 record ke ArrayDinamis, size bernilai 50")
    void impor50Record() throws IOException {
        HasilImpor hasil = aplikasi.impor(Path.of("data/putusan-50.csv"));

        assertEquals(50, hasil.getJumlahDiterima());
        assertEquals(50, aplikasi.getArsip().size());
        assertEquals("P001", aplikasi.getArsip().get(0).getCaseId());
        assertEquals("P050", aplikasi.getArsip().get(49).getCaseId());
        aplikasi.getArsip().validateInvariant();
    }

    @Test
    @DisplayName("Impor ulang tidak menggandakan data")
    void imporUlangTidakGanda() throws IOException {
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        aplikasi.impor(Path.of("data/putusan-50.csv"));

        assertEquals(50, aplikasi.getArsip().size());
    }

    @Test
    @DisplayName("Tampil memuat seluruh putusan sesuai urutan struktur")
    void tampilSemua() throws IOException {
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        String teks = aplikasi.tampil();

        assertTrue(teks.startsWith("Arsip berisi 50 putusan"));
        assertTrue(teks.indexOf("P001") < teks.indexOf("P002"));
        assertTrue(teks.contains(" 50. P050"));
    }

    @Test
    @DisplayName("Impor dan tampil tercatat di riwayat audit (operasi terbaru di puncak)")
    void operasiTercatat() throws IOException {
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        aplikasi.tampil();

        assertEquals(2, aplikasi.getRiwayat().size());
        assertTrue(aplikasi.getRiwayat().peek().contains("TAMPIL"));
        assertTrue(aplikasi.getRiwayat().lihatDariAtas(2).contains("IMPOR"));
    }
}