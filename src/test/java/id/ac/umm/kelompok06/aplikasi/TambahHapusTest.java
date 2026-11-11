package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.DataTidakValidException;
import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TambahHapusTest {

    private ArsipPutusan aplikasi;
    private ArrayDinamis<Putusan> arsip;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        arsip = aplikasi.getArsip();
    }

    @Test
    @DisplayName("Skenario 3: hapus elemen pertama, tengah, dan terakhir")
    void hapusPertamaTengahTerakhir() {
        assertEquals("P001", aplikasi.hapus("P001").getCaseId());
        assertEquals("P002", arsip.get(0).getCaseId());        // bergeser ke kiri
        arsip.validateInvariant();

        assertEquals("P026", aplikasi.hapus("P026").getCaseId()); // tengah (indeks 24)
        assertEquals("P027", arsip.get(24).getCaseId());
        arsip.validateInvariant();

        assertEquals("P050", aplikasi.hapus("P050").getCaseId());
        assertEquals("P049", arsip.get(arsip.size() - 1).getCaseId());
        assertEquals(47, arsip.size());
        arsip.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 4: tambah kembali tiga record, invariant dan urutan kembali seperti semula")
    void tambahKembaliTiga() {
        Putusan p001 = aplikasi.hapus("P001");
        Putusan p026 = aplikasi.hapus("P026");
        Putusan p050 = aplikasi.hapus("P050");

        aplikasi.tambahDi(0, p001);
        aplikasi.tambahDi(25, p026);
        aplikasi.tambah(p050);

        assertEquals(50, arsip.size());
        for (int i = 0; i < 50; i++) {
            assertEquals(String.format("P%03d", i + 1), arsip.get(i).getCaseId());
        }
        arsip.validateInvariant();
    }

    @Test
    @DisplayName("Hapus caseId yang tidak ada tidak mengubah arsip")
    void hapusTidakAda() {
        assertNull(aplikasi.hapus("P099"));
        assertEquals(50, arsip.size());
        assertTrue(aplikasi.getRiwayat().peek().contains("tidak ditemukan"));
    }

    @Test
    @DisplayName("Tambah caseId yang sudah ada ditolak")
    void tambahDuplikat() {
        Putusan p012 = arsip.get(11);

        assertThrows(IllegalArgumentException.class, () -> aplikasi.tambah(p012));
        assertEquals(50, arsip.size());
    }

    @Test
    @DisplayName("Tambah dari baris CSV yang valid")
    void tambahDariCsvValid() throws DataTidakValidException {
        String baris = "P051,1200/Pid.Sus/2024/PN Pbr,PN Pekanbaru,2024-12-20,T051,sabu,0.5,"
                + "114(1)|112(1),112(1),60,54,1000000000,1 paket sabu,tidak dukung program,"
                + "menyesal,identitas:1";

        Putusan baru = aplikasi.tambahDariBarisCsv(baris);

        assertEquals("P051", baru.getCaseId());
        assertEquals(51, arsip.size());
        assertEquals("P051", arsip.get(50).getCaseId());
        arsip.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 7: baris CSV tidak valid ditolak dengan pesan, struktur tetap konsisten")
    void tambahDariCsvTidakValid() {
        String baris = "P051,1200/Pid.Sus/2024/PN Pbr,PN Pekanbaru,20-12-2024,T051,sabu,0.5,"
                + "114(1),112(1),60,54,1000000000,1 paket sabu,x,y,identitas:1";

        DataTidakValidException e = assertThrows(DataTidakValidException.class,
                () -> aplikasi.tambahDariBarisCsv(baris));

        assertTrue(e.getMessage().contains("tanggalPutusan"));
        assertEquals(50, arsip.size());
        arsip.validateInvariant();
    }

    @Test
    @DisplayName("Posisi sisip di luar rentang ditolak tanpa mengubah arsip")
    void posisiDiLuarRentang() {
        Putusan p001 = aplikasi.hapus("P001");

        assertThrows(IndexOutOfBoundsException.class, () -> aplikasi.tambahDi(60, p001));
        assertEquals(49, arsip.size());
        arsip.validateInvariant();
    }
}