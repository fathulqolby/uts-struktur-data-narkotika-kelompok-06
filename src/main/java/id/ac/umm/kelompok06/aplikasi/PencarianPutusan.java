package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;

/**
 * Fitur Cari: mencari putusan berdasarkan caseId atau nomor perkara
 * dengan pencarian linear di struktur utama, lalu melaporkan jumlah langkah.
 * Setiap pencarian dicatat di riwayat audit.
 */
public class PencarianPutusan {

    private final ArsipPutusan aplikasi;

    public PencarianPutusan(ArsipPutusan aplikasi) {
        this.aplikasi = aplikasi;
    }

    /** Mencari berdasarkan caseId, tidak peka huruf besar/kecil (p012 = P012). O(n). */
    public HasilCari cariCaseId(String caseId) {
        String kunci = caseId == null ? "" : caseId.trim();
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        int indeks = arsip.cari(p -> p.getCaseId().equalsIgnoreCase(kunci));
        return selesai("CARI caseId", kunci, indeks, arsip.getLangkahTerakhir());
    }

    /** Mencari berdasarkan nomor perkara persis seperti di putusan (spasi di tepi diabaikan). O(n). */
    public HasilCari cariNomorPerkara(String nomorPerkara) {
        String kunci = nomorPerkara == null ? "" : nomorPerkara.trim();
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        int indeks = arsip.cari(p -> p.getNomorPerkara().equalsIgnoreCase(kunci));
        return selesai("CARI nomorPerkara", kunci, indeks, arsip.getLangkahTerakhir());
    }

    private HasilCari selesai(String operasi, String kunci, int indeks, int langkah) {
        Putusan ditemukan = indeks >= 0 ? aplikasi.getArsip().get(indeks) : null;
        aplikasi.catat(operasi, kunci + " -> "
                + (ditemukan != null ? "indeks " + indeks : "tidak ditemukan")
                + ", " + langkah + " langkah");
        return new HasilCari(kunci, ditemukan, indeks, langkah);
    }
}