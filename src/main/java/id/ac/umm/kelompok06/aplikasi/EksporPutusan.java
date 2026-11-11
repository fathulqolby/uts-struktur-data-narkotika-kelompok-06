package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.PutusanCsvParser;
import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fitur Ekspor: menyimpan hasil pencarian/filter ke file CSV
 * dan laporan (misalnya agregasi atau audit) ke file TXT.
 *
 * File CSV hasil ekspor memakai header dan format yang sama dengan data/putusan-50.csv,
 * sehingga bisa diimpor kembali oleh PutusanCsvParser.
 */
public class EksporPutusan {

    private final ArsipPutusan aplikasi;

    public EksporPutusan(ArsipPutusan aplikasi) {
        this.aplikasi = aplikasi;
    }

    /**
     * Menulis daftar putusan ke file CSV (header + satu baris per putusan).
     *
     * @return jumlah putusan yang ditulis
     */
    public int keCsv(ArrayDinamis<Putusan> daftar, Path tujuan) throws IOException {
        siapkanFolder(tujuan);
        try (BufferedWriter tulis = Files.newBufferedWriter(tujuan, StandardCharsets.UTF_8)) {
            tulis.write(PutusanCsvParser.HEADER);
            tulis.newLine();
            for (int i = 0; i < daftar.size(); i++) {
                tulis.write(keBarisCsv(daftar.get(i)));
                tulis.newLine();
            }
        }
        aplikasi.catat("EKSPOR", daftar.size() + " putusan -> " + tujuan.getFileName());
        return daftar.size();
    }

    /** Menulis teks bebas (laporan agregasi, audit, dll) ke file TXT. */
    public void keTeks(String isi, Path tujuan) throws IOException {
        siapkanFolder(tujuan);
        Files.writeString(tujuan, isi, StandardCharsets.UTF_8);
        aplikasi.catat("EKSPOR", "laporan -> " + tujuan.getFileName());
    }

    /** Mengubah satu Putusan menjadi satu baris CSV dengan 16 kolom. */
    static String keBarisCsv(Putusan p) {
        String[] kolom = {
                p.getCaseId(),
                p.getNomorPerkara(),
                p.getPengadilan(),
                p.getTanggalPutusan().toString(),          // format ISO yyyy-MM-dd
                p.getTerdakwaKode(),
                p.getJenisNarkotika(),
                angka(p.getBeratBarangBuktiGram()),
                p.getPasalDakwaan(),
                p.getPasalTerbukti(),
                String.valueOf(p.getTuntutanBulan()),
                String.valueOf(p.getVonisBulan()),
                String.valueOf(p.getDendaRupiah()),
                p.getBarangBukti(),
                p.getKeadaanMemberatkan(),
                p.getKeadaanMeringankan(),
                p.getSourcePages()
        };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kolom.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(kutipJikaPerlu(kolom[i]));
        }
        return sb.toString();
    }

    /**
     * Nilai yang mengandung koma atau tanda kutip dibungkus tanda kutip,
     * dan tanda kutip di dalamnya digandakan (""), sesuai aturan CSV.
     */
    static String kutipJikaPerlu(String nilai) {
        if (nilai == null) {
            return "";
        }
        if (nilai.contains(",") || nilai.contains("\"")) {
            return "\"" + nilai.replace("\"", "\"\"") + "\"";
        }
        return nilai;
    }

    /** 1.63 -> "1.63", 100.0 -> "100", tanpa notasi ilmiah seperti 1.0E-4. */
    private static String angka(double nilai) {
        return BigDecimal.valueOf(nilai).stripTrailingZeros().toPlainString();
    }

    private static void siapkanFolder(Path tujuan) throws IOException {
        Path folder = tujuan.toAbsolutePath().getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }
    }
}