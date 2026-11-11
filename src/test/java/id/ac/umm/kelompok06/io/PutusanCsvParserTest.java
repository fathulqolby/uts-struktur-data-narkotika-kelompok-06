package id.ac.umm.kelompok06.io;

import id.ac.umm.kelompok06.model.Putusan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PutusanCsvParserTest {

    private final PutusanCsvParser parser = new PutusanCsvParser();

    /** Baris valid P001 diambil dari data/putusan-50.csv. */
    private static final String BARIS_VALID =
            "P001,329/Pid.Sus/2023/PN Pbr,PN Pekanbaru,2023-07-06,T001,sabu,1.63,"
                    + "114(1)|112(1),112(1),72,72,1000000000,"
                    + "\"12 bungkus sabu, 1 HP, uang Rp500 ribu, sepeda motor\","
                    + "bertentangan program pemberantasan narkotika; merusak generasi bangsa; pernah dihukum,"
                    + "mengakui dan menyesali perbuatan,"
                    + "identitas:1;dakwaan:8;tuntutan:3;barangbukti:5;pertimbangan:22;amar:23-24";

    @Test
    @DisplayName("Baris valid menghasilkan Putusan dengan nilai yang benar")
    void barisValid() throws DataTidakValidException {
        Putusan p = parser.parseBaris(BARIS_VALID, 2);

        assertEquals("P001", p.getCaseId());
        assertEquals("329/Pid.Sus/2023/PN Pbr", p.getNomorPerkara());
        assertEquals(LocalDate.of(2023, 7, 6), p.getTanggalPutusan());
        assertEquals(1.63, p.getBeratBarangBuktiGram(), 0.0001);
        assertEquals(72, p.getVonisBulan());
        assertEquals(1_000_000_000L, p.getDendaRupiah());
    }

    @Test
    @DisplayName("Koma di dalam tanda kutip tidak memecah kolom")
    void komaDalamTandaKutip() throws DataTidakValidException {
        Putusan p = parser.parseBaris(BARIS_VALID, 2);

        assertEquals("12 bungkus sabu, 1 HP, uang Rp500 ribu, sepeda motor", p.getBarangBukti());
        assertEquals("mengakui dan menyesali perbuatan", p.getKeadaanMeringankan());
    }

    @Test
    @DisplayName("Kode khusus 999 dan 9999 diterima dan dikenali")
    void kodeKhususDiterima() throws DataTidakValidException {
        String baris = BARIS_VALID.replace(",72,72,1000000000,", ",9999,999,0,");
        Putusan p = parser.parseBaris(baris, 2);

        assertEquals(9999, p.getTuntutanBulan());
        assertEquals(999, p.getVonisBulan());
        assertTrue(p.isVonisKhusus());
    }

    @Test
    @DisplayName("Kolom kurang dari 16 ditolak")
    void kolomKurang() {
        DataTidakValidException e = assertThrows(DataTidakValidException.class,
                () -> parser.parseBaris("P051,123/Pid.Sus/2024/PN Pbr,PN Pekanbaru", 52));

        assertEquals(52, e.getNomorBaris());
        assertTrue(e.getMessage().contains("jumlah kolom"));
    }

    @Test
    @DisplayName("Tanggal yang bukan format YYYY-MM-DD ditolak")
    void tanggalSalah() {
        String baris = BARIS_VALID.replace("2023-07-06", "06/07/2023");

        DataTidakValidException e = assertThrows(DataTidakValidException.class,
                () -> parser.parseBaris(baris, 2));
        assertTrue(e.getMessage().contains("tanggalPutusan"));
    }

    @Test
    @DisplayName("Berat yang bukan angka ditolak")
    void beratBukanAngka() {
        String baris = BARIS_VALID.replace(",1.63,", ",satu koma enam,");

        DataTidakValidException e = assertThrows(DataTidakValidException.class,
                () -> parser.parseBaris(baris, 2));
        assertTrue(e.getMessage().contains("beratBarangBuktiGram"));
    }

    @Test
    @DisplayName("Vonis negatif ditolak")
    void vonisNegatif() {
        String baris = BARIS_VALID.replace(",72,72,", ",72,-5,");

        DataTidakValidException e = assertThrows(DataTidakValidException.class,
                () -> parser.parseBaris(baris, 2));
        assertTrue(e.getMessage().contains("vonisBulan"));
    }

    @Test
    @DisplayName("caseId yang tidak berformat P + 3 digit ditolak")
    void caseIdSalah() {
        String baris = BARIS_VALID.replaceFirst("P001", "X1");

        assertThrows(DataTidakValidException.class, () -> parser.parseBaris(baris, 2));
    }

    @Test
    @DisplayName("Tanda kutip yang tidak ditutup ditolak")
    void kutipTidakDitutup() {
        String baris = BARIS_VALID.replace("sepeda motor\",", "sepeda motor,");

        assertThrows(DataTidakValidException.class, () -> parser.parseBaris(baris, 2));
    }

    @Test
    @DisplayName("File berisi baris rusak: baris rusak ditolak, baris valid tetap masuk")
    void fileDenganBarisRusak(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("uji.csv");
        String isi = PutusanCsvParser.HEADER + "\n"
                + BARIS_VALID + "\n"
                + "P051,x,PN Pekanbaru\n"                                   // kolom kurang
                + BARIS_VALID.replace("P001", "P002").replace("T001", "T002") + "\n"
                + BARIS_VALID.replace("2023-07-06", "2023-13-01") + "\n";  // bulan 13
        Files.writeString(file, isi, StandardCharsets.UTF_8);

        int[] jumlahMasuk = {0};
        HasilImpor hasil = parser.bacaFile(file, p -> jumlahMasuk[0]++);

        assertEquals(2, hasil.getJumlahDiterima());
        assertEquals(2, hasil.getJumlahDitolak());
        assertEquals(2, jumlahMasuk[0]);
        assertTrue(hasil.getLaporanKesalahan().contains("Baris 3"));
        assertTrue(hasil.getLaporanKesalahan().contains("Baris 5"));
    }

    @Test
    @DisplayName("Header yang tidak sesuai skema membuat impor gagal")
    void headerSalah(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("uji.csv");
        Files.writeString(file, "id,nomor\n" + BARIS_VALID + "\n", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> parser.bacaFile(file, p -> { }));
    }

    @Test
    @DisplayName("Skenario 1: data/putusan-50.csv menghasilkan tepat 50 record valid")
    void fileAsliBerisi50Record() throws IOException {
        int[] jumlah = {0};
        HasilImpor hasil = parser.bacaFile(Path.of("data/putusan-50.csv"), p -> jumlah[0]++);

        assertEquals(50, hasil.getJumlahDiterima());
        assertEquals(0, hasil.getJumlahDitolak());
        assertEquals(50, jumlah[0]);
    }
}