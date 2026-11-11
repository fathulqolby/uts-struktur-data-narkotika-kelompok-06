package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;

/**
 * Fitur Urutan: menghasilkan tampilan terurut berdasarkan vonis dengan insertion sort
 * pada SALINAN arsip, sehingga urutan dan invariant struktur utama tidak berubah (lihat ADR-002).
 *
 * Insertion sort dipilih karena stabil (vonis sama tetap urut sesuai arsip),
 * sederhana untuk ditelusuri manual, dan cukup cepat untuk ±50 data.
 * Kompleksitas: O(n^2) perbandingan pada kasus terburuk, O(n) jika data sudah terurut.
 */
public class PengurutanPutusan {

    private final ArsipPutusan aplikasi;
    private int perbandinganTerakhir;
    private int pergeseranTerakhir;

    public PengurutanPutusan(ArsipPutusan aplikasi) {
        this.aplikasi = aplikasi;
    }

    /**
     * @param menurun true = vonis terberat dulu (kode 999/9999 di paling atas),
     *                false = vonis teringan dulu
     * @return ArrayDinamis baru berisi putusan terurut; arsip asli tidak diubah
     */
    public ArrayDinamis<Putusan> urutkanBerdasarkanVonis(boolean menurun) {
        ArrayDinamis<Putusan> hasil = aplikasi.getArsip().salin();
        perbandinganTerakhir = 0;
        pergeseranTerakhir = 0;

        for (int i = 1; i < hasil.size(); i++) {
            Putusan kunci = hasil.get(i);
            int j = i - 1;
            // geser elemen yang "harus berada setelah" kunci satu langkah ke kanan
            while (j >= 0) {
                perbandinganTerakhir++;
                if (!harusSebelum(kunci, hasil.get(j), menurun)) {
                    break;
                }
                hasil.set(j + 1, hasil.get(j));
                pergeseranTerakhir++;
                j--;
            }
            hasil.set(j + 1, kunci);
        }

        aplikasi.catat("URUT", "vonis " + (menurun ? "menurun" : "menaik") + ", "
                + perbandinganTerakhir + " perbandingan, " + pergeseranTerakhir + " pergeseran");
        return hasil;
    }

    /** true jika a harus diletakkan sebelum b. Vonis sama -> false, sehingga urutan tetap stabil. */
    private boolean harusSebelum(Putusan a, Putusan b, boolean menurun) {
        return menurun ? a.getVonisBulan() > b.getVonisBulan()
                : a.getVonisBulan() < b.getVonisBulan();
    }

    /** Jumlah perbandingan pada pengurutan terakhir (pencacah operasi). */
    public int getPerbandinganTerakhir() {
        return perbandinganTerakhir;
    }

    /** Jumlah pergeseran elemen pada pengurutan terakhir. */
    public int getPergeseranTerakhir() {
        return pergeseranTerakhir;
    }
}