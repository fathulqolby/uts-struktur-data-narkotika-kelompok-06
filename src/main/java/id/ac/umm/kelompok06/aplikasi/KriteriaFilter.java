package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;

/**
 * Kumpulan syarat filter. Syarat yang tidak diisi (null) diabaikan,
 * sehingga satu objek bisa dipakai untuk filter tunggal maupun gabungan.
 *
 * Contoh filter gabungan:
 *   new KriteriaFilter().jenis("sabu").pasalTerbukti("112(1)").vonis(60, 72).berat(0, 1)
 */
public class KriteriaFilter {

    private String jenis;
    private String pasalTerbukti;
    private Double beratMin;
    private Double beratMax;
    private Integer vonisMin;
    private Integer vonisMax;

    /** Jenis narkotika, misalnya "sabu". Nilai gabungan seperti "sabu|ekstasi" ikut cocok. */
    public KriteriaFilter jenis(String jenis) {
        this.jenis = jenis;
        return this;
    }

    /** Awalan pasal terbukti, misalnya "112(1)" cocok dengan "112(1) jo 132(1)". */
    public KriteriaFilter pasalTerbukti(String pasal) {
        this.pasalTerbukti = pasal;
        return this;
    }

    /** Rentang berat bersih dalam gram (inklusif). Berat 0 (tidak tercatat) tidak ikut. */
    public KriteriaFilter berat(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Berat minimum " + min + " lebih besar dari maksimum " + max);
        }
        this.beratMin = min;
        this.beratMax = max;
        return this;
    }

    /** Rentang vonis dalam bulan (inklusif). Kode khusus 999/9999 tidak ikut. */
    public KriteriaFilter vonis(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Vonis minimum " + min + " lebih besar dari maksimum " + max);
        }
        this.vonisMin = min;
        this.vonisMax = max;
        return this;
    }

    /** true jika putusan memenuhi semua syarat yang diisi. */
    public boolean cocok(Putusan p) {
        if (jenis != null && !p.mengandungJenis(jenis)) {
            return false;
        }
        if (pasalTerbukti != null && !p.getPasalTerbukti().startsWith(pasalTerbukti.trim())) {
            return false;
        }
        if (beratMin != null) {
            double b = p.getBeratBarangBuktiGram();
            if (b <= 0 || b < beratMin || b > beratMax) {
                return false;
            }
        }
        if (vonisMin != null) {
            if (p.isVonisKhusus() || p.getVonisBulan() < vonisMin || p.getVonisBulan() > vonisMax) {
                return false;
            }
        }
        return true;
    }

    /** Teks syarat untuk laporan dan riwayat audit. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (jenis != null) sb.append("jenis=").append(jenis).append(' ');
        if (pasalTerbukti != null) sb.append("pasal=").append(pasalTerbukti).append(' ');
        if (beratMin != null) sb.append("berat=").append(beratMin).append("..").append(beratMax).append("g ");
        if (vonisMin != null) sb.append("vonis=").append(vonisMin).append("..").append(vonisMax).append("bln ");
        return sb.length() == 0 ? "(tanpa syarat)" : sb.toString().trim();
    }
}