package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.struktur.ArrayDinamis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgregasiPutusanTest {

    private ArsipPutusan aplikasi;
    private AgregasiPutusan agregasi;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        agregasi = new AgregasiPutusan(aplikasi);
    }

    @Test
    @DisplayName("Jumlah perkara sama dengan size struktur utama")
    void jumlahPerkara() {
        assertEquals(50, agregasi.jumlahPerkara());
    }

    @Test
    @DisplayName("Rata-rata vonis mengecualikan kode 999/9999")
    void rataRataTanpaKodeKhusus() {
        assertEquals(3, agregasi.jumlahVonisKhusus());              // P009, P010, P011
        assertEquals(4642.0 / 47, agregasi.rataRataVonisBulan(), 0.0001);
    }

    @Test
    @DisplayName("Total denda seluruh putusan")
    void totalDenda() {
        assertEquals(51_300_000_000L, agregasi.totalDendaRupiah());
    }

    @Test
    @DisplayName("Distribusi jenis menghitung putusan campuran di setiap jenisnya")
    void distribusiJenis() {
        ArrayDinamis<JumlahJenis> d = agregasi.distribusiJenis();

        assertEquals(4, d.size());
        assertEquals(42, jumlahUntuk(d, "sabu"));
        assertEquals(14, jumlahUntuk(d, "ekstasi"));
        assertEquals(2, jumlahUntuk(d, "ganja"));
        assertEquals(1, jumlahUntuk(d, "lainnya"));
    }

    @Test
    @DisplayName("Agregasi berubah setelah putusan dihapus")
    void setelahHapus() {
        aplikasi.hapus("P031");                    // satu-satunya vonis 8 bulan, denda 0
        assertEquals(49, agregasi.jumlahPerkara());
        assertEquals((4642.0 - 8) / 46, agregasi.rataRataVonisBulan(), 0.0001);
    }

    @Test
    @DisplayName("Laporan memuat semua bagian dan tercatat di riwayat")
    void laporan() {
        String teks = agregasi.laporan();

        assertTrue(teks.contains("Jumlah perkara        : 50"));
        assertTrue(teks.contains("Rata-rata vonis"));
        assertTrue(teks.contains("Total denda"));
        assertTrue(teks.contains("sabu"));
        assertTrue(aplikasi.getRiwayat().peek().contains("AGREGASI"));
    }

    private int jumlahUntuk(ArrayDinamis<JumlahJenis> d, String jenis) {
        int idx = d.cari(x -> x.getJenis().equals(jenis));
        return idx < 0 ? 0 : d.get(idx).getJumlah();
    }
}