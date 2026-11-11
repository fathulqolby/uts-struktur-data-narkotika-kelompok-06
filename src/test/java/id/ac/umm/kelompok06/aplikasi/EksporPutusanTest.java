package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.HasilImpor;
import id.ac.umm.kelompok06.io.PutusanCsvParser;
import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EksporPutusanTest {

    private ArsipPutusan aplikasi;
    private EksporPutusan ekspor;

    @BeforeEach
    void siapkan() throws IOException {
        aplikasi = new ArsipPutusan();
        aplikasi.impor(Path.of("data/putusan-50.csv"));
        ekspor = new EksporPutusan(aplikasi);
    }

    @Test
    @DisplayName("Ekspor 50 putusan lalu impor ulang: 50 diterima, isi sama")
    void eksporLaluImporUlang(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("semua.csv");
        assertEquals(50, ekspor.keCsv(aplikasi.getArsip(), file));

        ArrayDinamis<Putusan> hasilBaca = new ArrayDinamis<>();
        HasilImpor hasil = new PutusanCsvParser().bacaFile(file, hasilBaca::add);

        assertEquals(50, hasil.getJumlahDiterima());
        assertEquals(0, hasil.getJumlahDitolak(), hasil.getLaporanKesalahan());
        for (int i = 0; i < 50; i++) {
            Putusan asli = aplikasi.getArsip().get(i);
            Putusan baca = hasilBaca.get(i);
            assertEquals(asli.getCaseId(), baca.getCaseId());
            assertEquals(asli.getBarangBukti(), baca.getBarangBukti());
            assertEquals(asli.getBeratBarangBuktiGram(), baca.getBeratBarangBuktiGram(), 0.0001);
            assertEquals(asli.getVonisBulan(), baca.getVonisBulan());
            assertEquals(asli.getSourcePages(), baca.getSourcePages());
        }
    }

    @Test
    @DisplayName("Ekspor hasil filter ganja hanya berisi P003 dan P026")
    void eksporHasilFilter(@TempDir Path folder) throws IOException {
        ArrayDinamis<Putusan> ganja = new FilterPutusan(aplikasi).berdasarkanJenis("ganja");
        Path file = folder.resolve("ganja.csv");

        assertEquals(2, ekspor.keCsv(ganja, file));

        String[] baris = Files.readString(file).split("\\R");
        assertEquals(3, baris.length);                      // header + 2 baris
        assertEquals(PutusanCsvParser.HEADER, baris[0]);
        assertTrue(baris[1].startsWith("P003,"));
        assertTrue(baris[2].startsWith("P026,"));
    }

    @Test
    @DisplayName("Nilai berisi koma atau tanda kutip dibungkus kutip")
    void kutipCsv() {
        assertEquals("sabu", EksporPutusan.kutipJikaPerlu("sabu"));
        assertEquals("\"1 HP, 1 dompet\"", EksporPutusan.kutipJikaPerlu("1 HP, 1 dompet"));
        assertEquals("\"merek \"\"Poco\"\"\"", EksporPutusan.kutipJikaPerlu("merek \"Poco\""));
    }

    @Test
    @DisplayName("Ekspor laporan agregasi ke TXT dan tercatat di riwayat")
    void eksporTeksDanRiwayat(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("laporan/agregasi.txt");   // folder dibuat otomatis
        String laporan = new AgregasiPutusan(aplikasi).laporan();

        ekspor.keTeks(laporan, file);

        assertEquals(laporan, Files.readString(file));
        assertTrue(aplikasi.getRiwayat().peek().contains("EKSPOR"));
    }

    @Test
    @DisplayName("Ekspor daftar kosong tetap menulis header")
    void eksporKosong(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("kosong.csv");
        assertEquals(0, ekspor.keCsv(new ArrayDinamis<>(), file));
        assertEquals(PutusanCsvParser.HEADER, Files.readString(file).trim());
    }
}