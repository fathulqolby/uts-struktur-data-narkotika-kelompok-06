package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.DataTidakValidException;
import id.ac.umm.kelompok06.io.HasilImpor;
import id.ac.umm.kelompok06.io.PutusanCsvParser;
import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;
import id.ac.umm.kelompok06.struktur.StackRiwayat;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Pusat aplikasi: menyimpan seluruh putusan di struktur utama (ArrayDinamis)
 * dan mencatat setiap operasi pengguna di struktur sekunder (StackRiwayat).
 * Fitur lain (cari, filter, urut, agregasi, ekspor) bekerja di atas getArsip().
 */
public class ArsipPutusan {

    private static final DateTimeFormatter FORMAT_WAKTU =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ArrayDinamis<Putusan> arsip = new ArrayDinamis<>();
    private final StackRiwayat<String> riwayat = new StackRiwayat<>();
    private final PutusanCsvParser parser = new PutusanCsvParser();

    /**
     * Fitur Impor: mengosongkan arsip lalu memuat ulang dari file CSV.
     * Record tidak valid ditolak dan dilaporkan di HasilImpor, program tetap berjalan.
     */
    public HasilImpor impor(Path file) throws IOException {
        arsip.clear();
        HasilImpor hasil = parser.bacaFile(file, arsip::add);
        catat("IMPOR", file.getFileName() + ": " + hasil.getJumlahDiterima()
                + " diterima, " + hasil.getJumlahDitolak() + " ditolak");
        return hasil;
    }

    /**
     * Fitur Tampil: ringkasan semua putusan sesuai urutan di struktur utama.
     * Dibaca dengan get(i) dari indeks 0 sampai size-1.
     */
    public String tampil() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Arsip berisi %d putusan%n", arsip.size()));
        for (int i = 0; i < arsip.size(); i++) {
            sb.append(String.format("%3d. %s%n", i + 1, arsip.get(i)));
        }
        catat("TAMPIL", arsip.size() + " putusan");
        return sb.toString();
    }

    // ================= Fitur Tambah dan Hapus =================

    /**
     * Menambah putusan di akhir arsip memakai add() milik ArrayDinamis.
     * Ditolak jika caseId sudah ada (contains memakai equals berdasarkan caseId).
     */
    public void tambah(Putusan putusan) {
        if (arsip.contains(putusan)) {
            throw new IllegalArgumentException("caseId " + putusan.getCaseId() + " sudah ada di arsip");
        }
        arsip.add(putusan);
        catat("TAMBAH", putusan.getCaseId() + " di indeks " + (arsip.size() - 1));
    }

    /**
     * Menyisipkan putusan pada posisi tertentu memakai insert() milik ArrayDinamis.
     * Elemen dari posisi itu bergeser ke kanan.
     */
    public void tambahDi(int posisi, Putusan putusan) {
        if (arsip.contains(putusan)) {
            throw new IllegalArgumentException("caseId " + putusan.getCaseId() + " sudah ada di arsip");
        }
        arsip.insert(posisi, putusan);
        catat("TAMBAH", putusan.getCaseId() + " di indeks " + posisi);
    }

    /**
     * Menambah dari satu baris CSV yang diketik pengguna.
     * Baris divalidasi oleh parser yang sama dengan impor (skenario 7):
     * jika tidak valid, exception dilempar dan arsip tidak berubah.
     */
    public Putusan tambahDariBarisCsv(String baris) throws DataTidakValidException {
        Putusan putusan = parser.parseBaris(baris, arsip.size() + 2);
        tambah(putusan);
        return putusan;
    }

    /**
     * Menghapus putusan berdasarkan caseId memakai cari() lalu remove() milik ArrayDinamis.
     * Elemen di kanannya bergeser ke kiri.
     *
     * @return putusan yang dihapus, atau null jika caseId tidak ada
     */
    public Putusan hapus(String caseId) {
        String kunci = caseId == null ? "" : caseId.trim();
        int indeks = arsip.cari(p -> p.getCaseId().equalsIgnoreCase(kunci));
        if (indeks < 0) {
            catat("HAPUS", kunci + " -> tidak ditemukan");
            return null;
        }
        Putusan dihapus = arsip.remove(indeks);
        catat("HAPUS", kunci + " dari indeks " + indeks);
        return dihapus;
    }

    /** Menghapus putusan pada posisi tertentu memakai remove() milik ArrayDinamis. */
    public Putusan hapusDi(int posisi) {
        Putusan dihapus = arsip.remove(posisi);
        catat("HAPUS", dihapus.getCaseId() + " dari indeks " + posisi);
        return dihapus;
    }

    /** Struktur utama, dipakai oleh fitur-fitur lain. */
    public ArrayDinamis<Putusan> getArsip() {
        return arsip;
    }

    /** Struktur sekunder untuk fitur Audit. */
    public StackRiwayat<String> getRiwayat() {
        return riwayat;
    }

    /** Merekam satu operasi pengguna ke riwayat (puncak = operasi terbaru). */
    public void catat(String jenisOperasi, String detail) {
        riwayat.push(LocalDateTime.now().format(FORMAT_WAKTU) + " | " + jenisOperasi + " | " + detail);
    }
}