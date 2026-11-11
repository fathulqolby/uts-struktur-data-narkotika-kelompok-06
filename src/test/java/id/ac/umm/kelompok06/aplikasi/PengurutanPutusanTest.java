package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PengurutanPutusanTest {

    private ArsipPutusan aplikasi;
    private PengurutanPutusan pengurutan;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        pengurutan = new PengurutanPutusan(aplikasi);
    }

    @Test
    @DisplayName("Urut menaik: setiap vonis tidak lebih besar dari vonis berikutnya")
    void menaik() {
        ArrayDinamis<Putusan> hasil = pengurutan.urutkanBerdasarkanVonis(false);

        assertEquals(50, hasil.size());
        for (int i = 0; i < hasil.size() - 1; i++) {
            assertTrue(hasil.get(i).getVonisBulan() <= hasil.get(i + 1).getVonisBulan());
        }
        assertEquals("P031", hasil.get(0).getCaseId());   // vonis 8 bulan, paling ringan
        hasil.validateInvariant();
    }

    @Test
    @DisplayName("Urut menurun: kode seumur hidup (999) di paling atas")
    void menurun() {
        ArrayDinamis<Putusan> hasil = pengurutan.urutkanBerdasarkanVonis(true);

        for (int i = 0; i < hasil.size() - 1; i++) {
            assertTrue(hasil.get(i).getVonisBulan() >= hasil.get(i + 1).getVonisBulan());
        }
        assertEquals(999, hasil.get(0).getVonisBulan());
        assertEquals("P031", hasil.get(49).getCaseId());
    }

    @Test
    @DisplayName("Pengurutan stabil: vonis sama tetap mengikuti urutan arsip")
    void stabil() {
        ArrayDinamis<Putusan> hasil = pengurutan.urutkanBerdasarkanVonis(true);

        assertEquals("P009", hasil.get(0).getCaseId());
        assertEquals("P010", hasil.get(1).getCaseId());
        assertEquals("P011", hasil.get(2).getCaseId());
    }

    @Test
    @DisplayName("Arsip asli tidak berubah urutan dan invariant tetap valid")
    void arsipTidakBerubah() {
        pengurutan.urutkanBerdasarkanVonis(false);

        for (int i = 0; i < 50; i++) {
            assertEquals(String.format("P%03d", i + 1), aplikasi.getArsip().get(i).getCaseId());
        }
        aplikasi.getArsip().validateInvariant();
    }

    @Test
    @DisplayName("Pencacah operasi: data yang sudah terurut hanya butuh n-1 perbandingan")
    void pencacahOperasi() {
        ArrayDinamis<Putusan> terurut = pengurutan.urutkanBerdasarkanVonis(false);
        aplikasi.getArsip().clear();
        for (int i = 0; i < terurut.size(); i++) {
            aplikasi.getArsip().add(terurut.get(i));
        }

        pengurutan.urutkanBerdasarkanVonis(false);

        assertEquals(49, pengurutan.getPerbandinganTerakhir());   // kasus terbaik O(n)
        assertEquals(0, pengurutan.getPergeseranTerakhir());
        assertTrue(aplikasi.getRiwayat().peek().contains("URUT"));
    }
}