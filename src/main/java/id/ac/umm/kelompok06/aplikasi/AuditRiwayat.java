package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.struktur.StackRiwayat;

/**
 * Fitur Audit: menampilkan dan mencari riwayat operasi pengguna
 * yang tersimpan di struktur sekunder (StackRiwayat).
 *
 * Melihat riwayat sengaja TIDAK dicatat ke riwayat itu sendiri,
 * agar tampilan audit tidak mengubah isi yang sedang dilihat.
 */
public class AuditRiwayat {

    private final ArsipPutusan aplikasi;

    public AuditRiwayat(ArsipPutusan aplikasi) {
        this.aplikasi = aplikasi;
    }

    /**
     * Menampilkan riwayat dari yang terbaru (puncak stack) ke yang terlama.
     *
     * @param maksimal jumlah entri yang ditampilkan; 0 atau negatif = semua
     */
    public String tampilkan(int maksimal) {
        StackRiwayat<String> riwayat = aplikasi.getRiwayat();
        int jumlah = (maksimal <= 0 || maksimal > riwayat.size()) ? riwayat.size() : maksimal;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Riwayat operasi (%d dari %d, terbaru di atas)%n", jumlah, riwayat.size()));
        for (int urutan = 1; urutan <= jumlah; urutan++) {
            sb.append(String.format("%3d. %s%n", urutan, riwayat.lihatDariAtas(urutan)));
        }
        return sb.toString();
    }

    /**
     * Mencari entri riwayat terbaru yang memuat kata tertentu (misalnya "HAPUS" atau "P012")
     * dengan searchPreserve: stack di-pop ke stack sementara lalu dikembalikan,
     * sehingga urutan riwayat tidak berubah (skenario 6).
     *
     * @return posisi dari puncak (1 = terbaru), atau -1 jika tidak ada
     */
    public int cari(String kata) {
        String kunci = kata == null ? "" : kata.trim().toLowerCase();
        return aplikasi.getRiwayat().searchPreserve(e -> e.toLowerCase().contains(kunci));
    }

    /** Teks hasil pencarian riwayat untuk ditampilkan. */
    public String cariDanTampilkan(String kata) {
        StackRiwayat<String> riwayat = aplikasi.getRiwayat();
        int posisi = cari(kata);
        if (posisi < 0) {
            return "'" + kata + "' tidak ada di riwayat (" + riwayat.getLangkahTerakhir() + " entri diperiksa)";
        }
        return "'" + kata + "' ditemukan di posisi " + posisi + " dari puncak ("
                + riwayat.getLangkahTerakhir() + " entri diperiksa):" + System.lineSeparator()
                + "    " + riwayat.lihatDariAtas(posisi);
    }
}