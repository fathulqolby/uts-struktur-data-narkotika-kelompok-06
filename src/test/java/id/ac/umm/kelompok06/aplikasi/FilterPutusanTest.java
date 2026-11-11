package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterPutusanTest {

    private ArsipPutusan aplikasi;
    private FilterPutusan filter;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        filter = new FilterPutusan(aplikasi);
    }

    /** Mengubah hasil menjadi teks caseId berurutan, misalnya "P021,P022". */
    private String daftarId(ArrayDinamis<Putusan> hasil) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hasil.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(hasil.get(i).getCaseId());
        }
        return sb.toString();
    }

    @Test
    @DisplayName("Filter jenis ikut menghitung nilai gabungan seperti sabu|ekstasi")
    void jenis() {
        assertEquals(42, filter.berdasarkanJenis("sabu").size());
        assertEquals(14, filter.berdasarkanJenis("ekstasi").size());
        assertEquals("P003,P026", daftarId(filter.berdasarkanJenis("ganja")));
    }

    @Test
    @DisplayName("Filter rentang berat tidak mengikutkan berat 0 (tidak tercatat)")
    void rentangBerat() {
        assertEquals(13, filter.berdasarkanBerat(0, 1).size());
        assertEquals("P008,P015,P016,P019,P034,P042,P043",
                daftarId(filter.berdasarkanBerat(1000, 10000)));
    }

    @Test
    @DisplayName("Filter pasal terbukti mencocokkan awalan pasal")
    void pasalTerbukti() {
        assertEquals(12, filter.berdasarkanPasalTerbukti("112(1)").size());
        assertEquals("P031", daftarId(filter.berdasarkanPasalTerbukti("127")));
    }

    @Test
    @DisplayName("Filter rentang vonis tidak mengikutkan kode 999/9999")
    void rentangVonis() {
        assertEquals(17, filter.berdasarkanVonis(60, 72).size());
        assertEquals("P013,P014,P015,P016,P019,P034,P035,P042,P043,P050",
                daftarId(filter.berdasarkanVonis(120, 10000)));
    }

    @Test
    @DisplayName("Skenario 5: filter gabungan cocok dengan pemeriksaan provenance manual")
    void gabungan() {
        KriteriaFilter k = new KriteriaFilter()
                .jenis("sabu").pasalTerbukti("112(1)").vonis(60, 72).berat(0, 1);

        assertEquals("P021,P022,P023,P024,P046", daftarId(filter.filter(k)));
    }

    @Test
    @DisplayName("Filter tidak mengubah arsip asli dan tercatat di riwayat")
    void arsipTidakBerubah() {
        filter.berdasarkanJenis("ganja");

        assertEquals(50, aplikasi.getArsip().size());
        assertEquals("P001", aplikasi.getArsip().get(0).getCaseId());
        aplikasi.getArsip().validateInvariant();
        assertTrue(aplikasi.getRiwayat().peek().contains("FILTER"));
    }

    @Test
    @DisplayName("Rentang terbalik ditolak")
    void rentangTerbalik() {
        assertThrows(IllegalArgumentException.class, () -> new KriteriaFilter().vonis(100, 50));
        assertThrows(IllegalArgumentException.class, () -> new KriteriaFilter().berat(5, 1));
    }
}