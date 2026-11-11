package id.ac.umm.kelompok06.aplikasi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditRiwayatTest {

    private ArsipPutusan aplikasi;
    private AuditRiwayat audit;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));          // riwayat 1: IMPOR
        new PencarianPutusan(aplikasi).cariCaseId("P012");        // riwayat 2: CARI
        aplikasi.hapus("P030");                                   // riwayat 3: HAPUS
        new FilterPutusan(aplikasi).berdasarkanJenis("ganja");    // riwayat 4: FILTER
        audit = new AuditRiwayat(aplikasi);
    }

    @Test
    @DisplayName("Riwayat ditampilkan dari operasi terbaru")
    void terbaruDiAtas() {
        String teks = audit.tampilkan(0);

        assertTrue(teks.startsWith("Riwayat operasi (4 dari 4"));
        assertTrue(teks.indexOf("FILTER") < teks.indexOf("HAPUS"));
        assertTrue(teks.indexOf("HAPUS") < teks.indexOf("CARI"));
        assertTrue(teks.indexOf("CARI") < teks.indexOf("IMPOR"));
    }

    @Test
    @DisplayName("Tampilan bisa dibatasi beberapa entri terbaru")
    void dibatasi() {
        String teks = audit.tampilkan(2);

        assertTrue(teks.startsWith("Riwayat operasi (2 dari 4"));
        assertTrue(teks.contains("FILTER"));
        assertTrue(teks.contains("HAPUS"));
        assertTrue(!teks.contains("IMPOR"));
    }

    @Test
    @DisplayName("Melihat riwayat tidak menambah entri riwayat")
    void melihatTidakDicatat() {
        audit.tampilkan(0);
        assertEquals(4, aplikasi.getRiwayat().size());
    }

    @Test
    @DisplayName("Skenario 6: pencarian riwayat tidak mengubah urutan akhir stack")
    void cariTidakMengubahUrutan() {
        String sebelum = aplikasi.getRiwayat().gambarIsi();

        assertEquals(3, audit.cari("CARI"));   // posisi ke-3 dari puncak
        assertEquals(-1, audit.cari("EKSPOR"));

        assertEquals(sebelum, aplikasi.getRiwayat().gambarIsi());
        assertEquals(4, aplikasi.getRiwayat().size());
        aplikasi.getRiwayat().validateInvariant();
    }

    @Test
    @DisplayName("Pencarian riwayat berdasarkan caseId, tidak peka huruf besar/kecil")
    void cariCaseId() {
        assertEquals(2, audit.cari("p030"));
        assertTrue(audit.cariDanTampilkan("P030").contains("posisi 2"));
    }
}