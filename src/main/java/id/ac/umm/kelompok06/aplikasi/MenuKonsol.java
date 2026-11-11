package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.io.DataTidakValidException;
import id.ac.umm.kelompok06.io.HasilImpor;
import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Menu konsol yang menghubungkan semua fitur aplikasi.
 *
 * Input dan output diterima lewat konstruktor (bukan langsung System.in/System.out),
 * sehingga menu bisa diuji otomatis dengan input berskrip.
 */
public class MenuKonsol {

    static final String FILE_DEFAULT = "data/putusan-50.csv";
    static final String FOLDER_EKSPOR = "ekspor";

    private final ArsipPutusan aplikasi;
    private final BufferedReader masukan;
    private final PrintStream keluaran;

    private final PencarianPutusan pencarian;
    private final FilterPutusan filter;
    private final AgregasiPutusan agregasi;
    private final PengurutanPutusan pengurutan;
    private final AuditRiwayat audit;
    private final EksporPutusan ekspor;

    /** Hasil cari/filter/urut terakhir, untuk diekspor lewat menu 10. */
    private ArrayDinamis<Putusan> hasilTerakhir;
    private String namaHasilTerakhir = "";

    public MenuKonsol(ArsipPutusan aplikasi, BufferedReader masukan, PrintStream keluaran) {
        this.aplikasi = aplikasi;
        this.masukan = masukan;
        this.keluaran = keluaran;
        this.pencarian = new PencarianPutusan(aplikasi);
        this.filter = new FilterPutusan(aplikasi);
        this.agregasi = new AgregasiPutusan(aplikasi);
        this.pengurutan = new PengurutanPutusan(aplikasi);
        this.audit = new AuditRiwayat(aplikasi);
        this.ekspor = new EksporPutusan(aplikasi);
    }

    /** Menjalankan menu sampai pengguna memilih 0 atau input habis. */
    public void jalankan() throws IOException {
        while (true) {
            tampilkanMenu();
            String pilihan = tanya("Pilih menu");
            if (pilihan == null || pilihan.equals("0")) {
                keluaran.println("Selesai.");
                return;
            }
            try {
                prosesPilihan(pilihan);
            } catch (IllegalArgumentException | IndexOutOfBoundsException | IllegalStateException e) {
                // Kesalahan input pengguna: tampilkan pesan, struktur data tidak berubah.
                keluaran.println("Gagal: " + e.getMessage());
            }
            keluaran.println();
        }
    }

    private void tampilkanMenu() {
        keluaran.println("===== Arsip Putusan Narkotika PN Pekanbaru (" + aplikasi.getArsip().size() + " putusan) =====");
        keluaran.println(" 1. Impor CSV            7. Agregasi / statistik");
        keluaran.println(" 2. Tampil semua         8. Urutkan berdasarkan vonis");
        keluaran.println(" 3. Tambah putusan       9. Audit riwayat operasi");
        keluaran.println(" 4. Hapus putusan       10. Ekspor hasil / laporan");
        keluaran.println(" 5. Cari putusan        11. Info struktur data");
        keluaran.println(" 6. Filter putusan       0. Keluar");
    }

    private void prosesPilihan(String pilihan) throws IOException {
        switch (pilihan) {
            case "1": menuImpor(); break;
            case "2": keluaran.print(aplikasi.tampil()); break;
            case "3": menuTambah(); break;
            case "4": menuHapus(); break;
            case "5": menuCari(); break;
            case "6": menuFilter(); break;
            case "7": keluaran.print(agregasi.laporan()); break;
            case "8": menuUrut(); break;
            case "9": menuAudit(); break;
            case "10": menuEkspor(); break;
            case "11": menuInfoStruktur(); break;
            default: keluaran.println("Pilihan '" + pilihan + "' tidak dikenal. Ketik angka 0-11.");
        }
    }

    // ---------------------------------------------------------------- 1
    private void menuImpor() throws IOException {
        String file = tanya("Path file CSV [" + FILE_DEFAULT + "]");
        Path path = Path.of(kosong(file) ? FILE_DEFAULT : file);
        HasilImpor hasil = aplikasi.impor(path);
        keluaran.println(hasil);
    }

    // ---------------------------------------------------------------- 3
    private void menuTambah() throws IOException {
        keluaran.println("Tempel satu baris CSV berisi 16 kolom (format sama dengan data/putusan-50.csv).");
        String baris = tanya("Baris CSV");
        if (kosong(baris)) {
            keluaran.println("Dibatalkan: baris kosong.");
            return;
        }
        try {
            Putusan p = aplikasi.tambahDariBarisCsv(baris);
            keluaran.println("Ditambahkan: " + p.getCaseId() + " (size sekarang " + aplikasi.getArsip().size() + ")");
        } catch (DataTidakValidException e) {
            keluaran.println("Data ditolak, arsip tidak berubah. " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- 4
    private void menuHapus() throws IOException {
        String caseId = tanya("caseId yang dihapus (misal P030)");
        if (kosong(caseId)) {
            keluaran.println("Dibatalkan: caseId kosong.");
            return;
        }
        Putusan dihapus = aplikasi.hapus(caseId);
        if (dihapus == null) {
            keluaran.println(caseId.trim() + " tidak ditemukan, arsip tidak berubah.");
        } else {
            keluaran.println("Dihapus: " + dihapus.getCaseId() + " (size sekarang " + aplikasi.getArsip().size() + ")");
        }
    }

    // ---------------------------------------------------------------- 5
    private void menuCari() throws IOException {
        String mode = tanya("Cari berdasarkan 1) caseId  2) nomor perkara");
        String kunci = tanya("Kata kunci");
        if (kosong(kunci)) {
            keluaran.println("Dibatalkan: kata kunci kosong.");
            return;
        }
        HasilCari hasil = "2".equals(mode) ? pencarian.cariNomorPerkara(kunci) : pencarian.cariCaseId(kunci);
        keluaran.println(hasil);

        ArrayDinamis<Putusan> daftar = new ArrayDinamis<>();
        if (hasil.ditemukan()) {
            daftar.add(hasil.getPutusan());
        }
        simpanHasil(daftar, "cari-" + kunci.trim());
    }

    // ---------------------------------------------------------------- 6
    private void menuFilter() throws IOException {
        keluaran.println("Isi syarat filter. Kosongkan untuk mengabaikan syarat itu.");
        KriteriaFilter kriteria = new KriteriaFilter();

        String jenis = tanya("Jenis narkotika (misal sabu)");
        if (!kosong(jenis)) {
            kriteria.jenis(jenis.trim());
        }
        String pasal = tanya("Pasal terbukti (misal 112(1))");
        if (!kosong(pasal)) {
            kriteria.pasalTerbukti(pasal.trim());
        }
        String beratMin = tanya("Berat minimum gram");
        String beratMax = tanya("Berat maksimum gram");
        if (!kosong(beratMin) || !kosong(beratMax)) {
            double min = kosong(beratMin) ? 0 : angkaDesimal(beratMin, "berat minimum");
            double max = kosong(beratMax) ? Double.MAX_VALUE : angkaDesimal(beratMax, "berat maksimum");
            kriteria.berat(min, max);
        }
        String vonisMin = tanya("Vonis minimum bulan");
        String vonisMax = tanya("Vonis maksimum bulan");
        if (!kosong(vonisMin) || !kosong(vonisMax)) {
            int min = kosong(vonisMin) ? 0 : angkaBulat(vonisMin, "vonis minimum");
            int max = kosong(vonisMax) ? Integer.MAX_VALUE : angkaBulat(vonisMax, "vonis maksimum");
            kriteria.vonis(min, max);
        }

        ArrayDinamis<Putusan> hasil = filter.filter(kriteria);
        keluaran.println("Syarat: " + kriteria);
        keluaran.print(FilterPutusan.format(hasil));
        simpanHasil(hasil, "filter");
    }

    // ---------------------------------------------------------------- 8
    private void menuUrut() throws IOException {
        String arah = tanya("Urutan 1) vonis terberat dulu  2) vonis teringan dulu");
        boolean menurun = !"2".equals(arah);
        ArrayDinamis<Putusan> hasil = pengurutan.urutkanBerdasarkanVonis(menurun);
        keluaran.print(FilterPutusan.format(hasil));
        keluaran.println("Insertion sort: " + pengurutan.getPerbandinganTerakhir() + " perbandingan, "
                + pengurutan.getPergeseranTerakhir() + " pergeseran. Urutan arsip asli tidak berubah.");
        simpanHasil(hasil, menurun ? "urut-vonis-menurun" : "urut-vonis-menaik");
    }

    // ---------------------------------------------------------------- 9
    private void menuAudit() throws IOException {
        String mode = tanya("Audit 1) tampilkan riwayat  2) cari di riwayat");
        if ("2".equals(mode)) {
            String kata = tanya("Kata yang dicari (misal HAPUS atau P030)");
            if (kosong(kata)) {
                keluaran.println("Dibatalkan: kata kosong.");
                return;
            }
            keluaran.println(audit.cariDanTampilkan(kata));
        } else {
            String n = tanya("Jumlah entri terbaru yang ditampilkan [semua]");
            keluaran.print(audit.tampilkan(kosong(n) ? 0 : angkaBulat(n, "jumlah entri")));
        }
    }

    // ---------------------------------------------------------------- 10
    private void menuEkspor() throws IOException {
        String jenis = tanya("Ekspor 1) hasil cari/filter/urut terakhir ke CSV  2) laporan agregasi ke TXT");
        if ("2".equals(jenis)) {
            Path tujuan = Path.of(FOLDER_EKSPOR, "laporan-agregasi.txt");
            ekspor.keTeks(agregasi.laporan(), tujuan);
            keluaran.println("Laporan disimpan di " + tujuan);
            return;
        }
        if (hasilTerakhir == null) {
            keluaran.println("Belum ada hasil. Jalankan menu 5, 6, atau 8 terlebih dahulu.");
            return;
        }
        Path tujuan = Path.of(FOLDER_EKSPOR, namaHasilTerakhir + ".csv");
        int jumlah = ekspor.keCsv(hasilTerakhir, tujuan);
        keluaran.println(jumlah + " putusan disimpan di " + tujuan);
    }

    // ---------------------------------------------------------------- 11
    private void menuInfoStruktur() {
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        arsip.validateInvariant();
        aplikasi.getRiwayat().validateInvariant();
        keluaran.println("Struktur utama  : ArrayDinamis<Putusan>, size=" + arsip.size()
                + ", kapasitas=" + arsip.kapasitas());
        keluaran.println("Struktur sekunder: StackRiwayat<String>, size=" + aplikasi.getRiwayat().size());
        keluaran.println("validateInvariant() kedua struktur: OK");
    }

    // ================================================================ bantuan

    private String tanya(String pertanyaan) throws IOException {
        keluaran.print(pertanyaan + ": ");
        String baris = masukan.readLine();
        return baris == null ? null : baris.trim();
    }

    private void simpanHasil(ArrayDinamis<Putusan> hasil, String nama) {
        hasilTerakhir = hasil;
        namaHasilTerakhir = nama.replaceAll("[^A-Za-z0-9-]", "_");
    }

    private static boolean kosong(String s) {
        return s == null || s.isBlank();
    }

    private static int angkaBulat(String teks, String nama) {
        try {
            return Integer.parseInt(teks.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nama + " harus bilangan bulat, bukan '" + teks.trim() + "'");
        }
    }

    private static double angkaDesimal(String teks, String nama) {
        try {
            return Double.parseDouble(teks.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nama + " harus angka, bukan '" + teks.trim() + "'");
        }
    }
}