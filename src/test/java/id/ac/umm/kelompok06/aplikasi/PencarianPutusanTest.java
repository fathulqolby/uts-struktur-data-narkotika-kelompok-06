package id.ac.umm.kelompok06.aplikasi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PencarianPutusanTest {

    private ArsipPutusan aplikasi;
    private PencarianPutusan pencarian;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        pencarian = new PencarianPutusan(aplikasi);
    }

    @Test
    @DisplayName("Skenario 2: perkara yang ada ditemukan dengan jumlah langkah yang benar")
    void caseIdAda() {
        HasilCari hasil = pencarian.cariCaseId("P012");

        assertTrue(hasil.ditemukan());
        assertEquals(11, hasil.getIndeks());
        assertEquals(12, hasil.getLangkah());
        assertEquals("1036/Pid.Sus/2022/PN Pbr", hasil.getPutusan().getNomorPerkara());
    }

    @Test
    @DisplayName("Skenario 2: perkara yang tidak ada memeriksa seluruh 50 elemen")
    void caseIdTidakAda() {
        HasilCari hasil = pencarian.cariCaseId("P099");

        assertFalse(hasil.ditemukan());
        assertEquals(-1, hasil.getIndeks());
        assertEquals(50, hasil.getLangkah());
    }

    @Test
    @DisplayName("Skenario 2: record di posisi batas (pertama dan terakhir)")
    void posisiBatas() {
        HasilCari pertama = pencarian.cariCaseId("P001");
        HasilCari terakhir = pencarian.cariCaseId("P050");

        assertEquals(0, pertama.getIndeks());
        assertEquals(1, pertama.getLangkah());
        assertEquals(49, terakhir.getIndeks());
        assertEquals(50, terakhir.getLangkah());
    }

    @Test
    @DisplayName("Pencarian caseId tidak peka huruf besar/kecil dan spasi")
    void caseIdHurufKecil() {
        assertTrue(pencarian.cariCaseId("  p031 ").ditemukan());
    }

    @Test
    @DisplayName("Cari berdasarkan nomor perkara")
    void nomorPerkara() {
        HasilCari hasil = pencarian.cariNomorPerkara("523/Pid.Sus/2023/PN.Pbr");

        assertTrue(hasil.ditemukan());
        assertEquals("P005", hasil.getPutusan().getCaseId());
        assertEquals(5, hasil.getLangkah());
    }

    @Test
    @DisplayName("Setiap pencarian tercatat di riwayat audit")
    void tercatatDiRiwayat() {
        pencarian.cariCaseId("P099");

        assertTrue(aplikasi.getRiwayat().peek().contains("CARI caseId"));
        assertTrue(aplikasi.getRiwayat().peek().contains("tidak ditemukan"));
    }
}