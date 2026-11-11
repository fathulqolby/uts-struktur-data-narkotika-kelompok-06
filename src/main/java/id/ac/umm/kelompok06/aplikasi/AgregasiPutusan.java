package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;

/**
 * Fitur Agregasi: jumlah perkara, rata-rata vonis, total denda, dan distribusi jenis narkotika.
 * Semua dihitung dengan satu kali penelusuran struktur utama (O(n)),
 * kecuali distribusi yang juga menelusuri daftar jenis (O(n x k), k = banyak jenis).
 */
public class AgregasiPutusan {

    private final ArsipPutusan aplikasi;

    public AgregasiPutusan(ArsipPutusan aplikasi) {
        this.aplikasi = aplikasi;
    }

    public int jumlahPerkara() {
        return aplikasi.getArsip().size();
    }

    /** Banyaknya putusan dengan vonis kode khusus (seumur hidup / mati). */
    public int jumlahVonisKhusus() {
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        int n = 0;
        for (int i = 0; i < arsip.size(); i++) {
            if (arsip.get(i).isVonisKhusus()) {
                n++;
            }
        }
        return n;
    }

    /**
     * Rata-rata vonis dalam bulan. Kode 999/9999 dikecualikan (aturan data no. 19).
     * Mengembalikan 0 jika tidak ada vonis biasa.
     */
    public double rataRataVonisBulan() {
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        long total = 0;
        int n = 0;
        for (int i = 0; i < arsip.size(); i++) {
            Putusan p = arsip.get(i);
            if (!p.isVonisKhusus()) {
                total += p.getVonisBulan();
                n++;
            }
        }
        return n == 0 ? 0 : (double) total / n;
    }

    public long totalDendaRupiah() {
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        long total = 0;
        for (int i = 0; i < arsip.size(); i++) {
            total += arsip.get(i).getDendaRupiah();
        }
        return total;
    }

    /**
     * Distribusi jenis narkotika. Putusan campuran (misalnya "sabu|ekstasi")
     * dihitung di setiap jenisnya, sehingga jumlah total bisa lebih dari banyak perkara.
     * Hasil disimpan di ArrayDinamis, tanpa HashMap.
     */
    public ArrayDinamis<JumlahJenis> distribusiJenis() {
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        ArrayDinamis<JumlahJenis> hasil = new ArrayDinamis<>();
        for (int i = 0; i < arsip.size(); i++) {
            String[] daftar = arsip.get(i).getJenisNarkotika().split("\\|");
            for (String jenis : daftar) {
                String j = jenis.trim().toLowerCase();
                int idx = hasil.cari(x -> x.getJenis().equals(j));
                if (idx < 0) {
                    hasil.add(new JumlahJenis(j));
                    idx = hasil.size() - 1;
                }
                hasil.get(idx).tambahSatu();
            }
        }
        return hasil;
    }

    /** Laporan lengkap agregasi untuk ditampilkan atau diekspor. */
    public String laporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Agregasi Putusan PN Pekanbaru ===").append(System.lineSeparator());
        sb.append(String.format("Jumlah perkara        : %d%n", jumlahPerkara()));
        sb.append(String.format("Rata-rata vonis       : %.2f bulan (tanpa %d vonis seumur hidup/mati)%n",
                rataRataVonisBulan(), jumlahVonisKhusus()));
        sb.append(String.format("Total denda           : Rp%,d%n", totalDendaRupiah()));
        sb.append("Distribusi jenis      :").append(System.lineSeparator());
        ArrayDinamis<JumlahJenis> distribusi = distribusiJenis();
        for (int i = 0; i < distribusi.size(); i++) {
            JumlahJenis j = distribusi.get(i);
            sb.append(String.format("  - %-8s : %d perkara%n", j.getJenis(), j.getJumlah()));
        }
        aplikasi.catat("AGREGASI", jumlahPerkara() + " perkara");
        return sb.toString();
    }
}