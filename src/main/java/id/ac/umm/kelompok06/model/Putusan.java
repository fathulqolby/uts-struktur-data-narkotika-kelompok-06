package id.ac.umm.kelompok06.model;

import java.time.LocalDate;

/**
 * Satu putusan pidana narkotika sesuai skema minimum panduan UTS Bagian 2.2.
 * Objek bersifat immutable: nilainya tidak berubah setelah dibuat.
 */
public class Putusan {

    /** Kode bulan untuk pidana penjara seumur hidup (aturan data no. 17). */
    public static final int SEUMUR_HIDUP = 999;
    /** Kode bulan untuk pidana mati (aturan data no. 18). */
    public static final int PIDANA_MATI = 9999;

    private final String caseId;
    private final String nomorPerkara;
    private final String pengadilan;
    private final LocalDate tanggalPutusan;
    private final String terdakwaKode;
    private final String jenisNarkotika;
    private final double beratBarangBuktiGram;
    private final String pasalDakwaan;
    private final String pasalTerbukti;
    private final int tuntutanBulan;
    private final int vonisBulan;
    private final long dendaRupiah;
    private final String barangBukti;
    private final String keadaanMemberatkan;
    private final String keadaanMeringankan;
    private final String sourcePages;

    public Putusan(String caseId, String nomorPerkara, String pengadilan,
                   LocalDate tanggalPutusan, String terdakwaKode, String jenisNarkotika,
                   double beratBarangBuktiGram, String pasalDakwaan, String pasalTerbukti,
                   int tuntutanBulan, int vonisBulan, long dendaRupiah,
                   String barangBukti, String keadaanMemberatkan,
                   String keadaanMeringankan, String sourcePages) {
        this.caseId = caseId;
        this.nomorPerkara = nomorPerkara;
        this.pengadilan = pengadilan;
        this.tanggalPutusan = tanggalPutusan;
        this.terdakwaKode = terdakwaKode;
        this.jenisNarkotika = jenisNarkotika;
        this.beratBarangBuktiGram = beratBarangBuktiGram;
        this.pasalDakwaan = pasalDakwaan;
        this.pasalTerbukti = pasalTerbukti;
        this.tuntutanBulan = tuntutanBulan;
        this.vonisBulan = vonisBulan;
        this.dendaRupiah = dendaRupiah;
        this.barangBukti = barangBukti;
        this.keadaanMemberatkan = keadaanMemberatkan;
        this.keadaanMeringankan = keadaanMeringankan;
        this.sourcePages = sourcePages;
    }

    // ---------- Getter ----------
    public String getCaseId() { return caseId; }
    public String getNomorPerkara() { return nomorPerkara; }
    public String getPengadilan() { return pengadilan; }
    public LocalDate getTanggalPutusan() { return tanggalPutusan; }
    public String getTerdakwaKode() { return terdakwaKode; }
    public String getJenisNarkotika() { return jenisNarkotika; }
    public double getBeratBarangBuktiGram() { return beratBarangBuktiGram; }
    public String getPasalDakwaan() { return pasalDakwaan; }
    public String getPasalTerbukti() { return pasalTerbukti; }
    public int getTuntutanBulan() { return tuntutanBulan; }
    public int getVonisBulan() { return vonisBulan; }
    public long getDendaRupiah() { return dendaRupiah; }
    public String getBarangBukti() { return barangBukti; }
    public String getKeadaanMemberatkan() { return keadaanMemberatkan; }
    public String getKeadaanMeringankan() { return keadaanMeringankan; }
    public String getSourcePages() { return sourcePages; }

    // ---------- Method bantu ----------

    /** true jika vonis berupa kode khusus (seumur hidup / mati), bukan jumlah bulan. */
    public boolean isVonisKhusus() {
        return vonisBulan == SEUMUR_HIDUP || vonisBulan == PIDANA_MATI;
    }

    /** true jika tuntutan berupa kode khusus (seumur hidup / mati). */
    public boolean isTuntutanKhusus() {
        return tuntutanBulan == SEUMUR_HIDUP || tuntutanBulan == PIDANA_MATI;
    }

    /**
     * true jika putusan memuat jenis narkotika tertentu.
     * Nilai gabungan seperti "sabu|ekstasi" dipecah per tanda "|".
     */
    public boolean mengandungJenis(String jenis) {
        if (jenis == null) {
            return false;
        }
        String[] daftar = jenisNarkotika.split("\\|");
        for (String j : daftar) {
            if (j.trim().equalsIgnoreCase(jenis.trim())) {
                return true;
            }
        }
        return false;
    }

    /** Vonis dalam bentuk teks yang mudah dibaca. */
    public String vonisTeks() {
        if (vonisBulan == SEUMUR_HIDUP) {
            return "seumur hidup";
        }
        if (vonisBulan == PIDANA_MATI) {
            return "pidana mati";
        }
        return (vonisBulan / 12) + " th " + (vonisBulan % 12) + " bln";
    }

    // ---------- equals, hashCode, toString ----------

    /** Dua putusan dianggap sama jika caseId-nya sama. Dipakai oleh contains(). */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Putusan)) {
            return false;
        }
        Putusan lain = (Putusan) o;
        return caseId != null && caseId.equals(lain.caseId);
    }

    @Override
    public int hashCode() {
        return caseId == null ? 0 : caseId.hashCode();
    }

    /** Ringkasan satu baris untuk fitur tampil. */
    @Override
    public String toString() {
        return String.format("%s | %-26s | %s | %-13s | %8.2f g | vonis %-14s | denda Rp%,d",
                caseId, nomorPerkara, tanggalPutusan, jenisNarkotika,
                beratBarangBuktiGram, vonisTeks(), dendaRupiah);
    }
}