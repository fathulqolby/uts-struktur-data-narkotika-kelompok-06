package id.ac.umm.kelompok06.io;

import id.ac.umm.kelompok06.model.Putusan;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

/**
 * Membaca data/putusan-50.csv dan mengubah setiap baris menjadi objek Putusan.
 * Baris yang tidak valid ditolak dan dilaporkan tanpa menghentikan program.
 *
 * Catatan: kelas ini tidak menyimpan data di koleksi java.util.
 * Setiap Putusan yang valid langsung diserahkan ke "tujuan"
 * (misalnya method add milik ArrayDinamis).
 */
public class PutusanCsvParser {

    public static final String HEADER =
            "caseId,nomorPerkara,pengadilan,tanggalPutusan,terdakwaKode,jenisNarkotika,"
                    + "beratBarangBuktiGram,pasalDakwaan,pasalTerbukti,tuntutanBulan,vonisBulan,"
                    + "dendaRupiah,barangBukti,keadaanMemberatkan,keadaanMeringankan,sourcePages";

    public static final int JUMLAH_KOLOM = 16;

    /**
     * Membaca seluruh file. Setiap Putusan valid diserahkan ke tujuan,
     * setiap baris tidak valid dicatat di laporan.
     */
    public HasilImpor bacaFile(Path file, Consumer<Putusan> tujuan) throws IOException {
        int diterima = 0;
        int ditolak = 0;
        StringBuilder laporan = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("File kosong: " + file);
            }
            header = hapusBom(header).trim();
            if (!header.equals(HEADER)) {
                throw new IOException("Header CSV tidak sesuai skema 2.2: " + header);
            }

            String baris;
            int nomorBaris = 1;
            while ((baris = reader.readLine()) != null) {
                nomorBaris++;
                if (baris.isBlank()) {
                    continue; // baris kosong dilewati, bukan dianggap error
                }
                try {
                    tujuan.accept(parseBaris(baris, nomorBaris));
                    diterima++;
                } catch (DataTidakValidException e) {
                    ditolak++;
                    laporan.append("  - ").append(e.getMessage()).append(System.lineSeparator());
                }
            }
        }
        return new HasilImpor(diterima, ditolak, laporan.toString());
    }

    /** Mengubah satu baris CSV menjadi Putusan, atau melempar exception jika tidak valid. */
    public Putusan parseBaris(String baris, int nomorBaris) throws DataTidakValidException {
        String[] k = pecahKolom(baris, nomorBaris);
        if (k.length != JUMLAH_KOLOM) {
            throw new DataTidakValidException(nomorBaris,
                    "jumlah kolom " + k.length + ", seharusnya " + JUMLAH_KOLOM);
        }

        String caseId = wajibIsi(k[0], "caseId", nomorBaris);
        if (!caseId.matches("P\\d{3}")) {
            throw new DataTidakValidException(nomorBaris, "caseId '" + caseId + "' harus berformat P + 3 digit");
        }
        String nomorPerkara = wajibIsi(k[1], "nomorPerkara", nomorBaris);
        String pengadilan = wajibIsi(k[2], "pengadilan", nomorBaris);
        LocalDate tanggal = parseTanggal(k[3], nomorBaris);
        String terdakwaKode = wajibIsi(k[4], "terdakwaKode", nomorBaris);
        if (!terdakwaKode.matches("T\\d{3}")) {
            throw new DataTidakValidException(nomorBaris, "terdakwaKode '" + terdakwaKode + "' harus berformat T + 3 digit");
        }
        String jenis = wajibIsi(k[5], "jenisNarkotika", nomorBaris);
        double berat = parseDouble(k[6], "beratBarangBuktiGram", nomorBaris);
        String pasalDakwaan = wajibIsi(k[7], "pasalDakwaan", nomorBaris);
        String pasalTerbukti = wajibIsi(k[8], "pasalTerbukti", nomorBaris);
        int tuntutan = parseInt(k[9], "tuntutanBulan", nomorBaris);
        int vonis = parseInt(k[10], "vonisBulan", nomorBaris);
        long denda = parseLong(k[11], "dendaRupiah", nomorBaris);

        return new Putusan(caseId, nomorPerkara, pengadilan, tanggal, terdakwaKode, jenis,
                berat, pasalDakwaan, pasalTerbukti, tuntutan, vonis, denda,
                k[12].trim(), k[13].trim(), k[14].trim(), k[15].trim());
    }

    /**
     * Memecah satu baris CSV menjadi kolom.
     * Mendukung nilai bertanda kutip yang berisi koma, misalnya "1 HP, 1 dompet",
     * serta tanda kutip ganda "" di dalam nilai.
     */
    String[] pecahKolom(String baris, int nomorBaris) throws DataTidakValidException {
        String[] hasil = new String[JUMLAH_KOLOM + 1]; // +1 untuk mendeteksi kolom berlebih
        int jumlah = 0;
        StringBuilder isi = new StringBuilder();
        boolean dalamKutip = false;

        for (int i = 0; i < baris.length(); i++) {
            char c = baris.charAt(i);
            if (dalamKutip) {
                if (c == '"') {
                    if (i + 1 < baris.length() && baris.charAt(i + 1) == '"') {
                        isi.append('"'); // "" berarti satu tanda kutip
                        i++;
                    } else {
                        dalamKutip = false;
                    }
                } else {
                    isi.append(c);
                }
            } else if (c == '"') {
                dalamKutip = true;
            } else if (c == ',') {
                if (jumlah == hasil.length) {
                    throw new DataTidakValidException(nomorBaris, "jumlah kolom lebih dari " + JUMLAH_KOLOM);
                }
                hasil[jumlah++] = isi.toString();
                isi.setLength(0);
            } else {
                isi.append(c);
            }
        }
        if (dalamKutip) {
            throw new DataTidakValidException(nomorBaris, "tanda kutip tidak ditutup");
        }
        if (jumlah == hasil.length) {
            throw new DataTidakValidException(nomorBaris, "jumlah kolom lebih dari " + JUMLAH_KOLOM);
        }
        hasil[jumlah++] = isi.toString();

        String[] tepat = new String[jumlah];
        for (int i = 0; i < jumlah; i++) {
            tepat[i] = hasil[i];
        }
        return tepat;
    }

    // ---------- validasi per kolom ----------

    private String wajibIsi(String nilai, String nama, int nomorBaris) throws DataTidakValidException {
        String n = nilai.trim();
        if (n.isEmpty()) {
            throw new DataTidakValidException(nomorBaris, nama + " tidak boleh kosong");
        }
        return n;
    }

    private LocalDate parseTanggal(String nilai, int nomorBaris) throws DataTidakValidException {
        String n = wajibIsi(nilai, "tanggalPutusan", nomorBaris);
        try {
            return LocalDate.parse(n); // format ISO YYYY-MM-DD (aturan data no. 4)
        } catch (DateTimeParseException e) {
            throw new DataTidakValidException(nomorBaris,
                    "tanggalPutusan '" + n + "' harus berformat YYYY-MM-DD");
        }
    }

    private double parseDouble(String nilai, String nama, int nomorBaris) throws DataTidakValidException {
        String n = wajibIsi(nilai, nama, nomorBaris);
        try {
            double d = Double.parseDouble(n);
            if (d < 0 || Double.isNaN(d) || Double.isInfinite(d)) {
                throw new DataTidakValidException(nomorBaris, nama + " tidak boleh negatif: " + n);
            }
            return d;
        } catch (NumberFormatException e) {
            throw new DataTidakValidException(nomorBaris, nama + " bukan angka: '" + n + "'");
        }
    }

    private int parseInt(String nilai, String nama, int nomorBaris) throws DataTidakValidException {
        String n = wajibIsi(nilai, nama, nomorBaris);
        try {
            int x = Integer.parseInt(n);
            if (x < 0) {
                throw new DataTidakValidException(nomorBaris, nama + " tidak boleh negatif: " + n);
            }
            return x;
        } catch (NumberFormatException e) {
            throw new DataTidakValidException(nomorBaris, nama + " bukan bilangan bulat: '" + n + "'");
        }
    }

    private long parseLong(String nilai, String nama, int nomorBaris) throws DataTidakValidException {
        String n = wajibIsi(nilai, nama, nomorBaris);
        try {
            long x = Long.parseLong(n);
            if (x < 0) {
                throw new DataTidakValidException(nomorBaris, nama + " tidak boleh negatif: " + n);
            }
            return x;
        } catch (NumberFormatException e) {
            throw new DataTidakValidException(nomorBaris, nama + " bukan bilangan bulat: '" + n + "'");
        }
    }

    private String hapusBom(String s) {
        return (!s.isEmpty() && s.charAt(0) == '﻿') ? s.substring(1) : s;
    }
}