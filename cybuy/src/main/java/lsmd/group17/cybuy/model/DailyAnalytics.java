package lsmd.group17.cybuy.model;

public class DailyAnalytics {
    private int day_num;
    private int all_sales;
    private int male_sales;
    private int female_sales;
    private int young_sales;
    private int old_sales;

    public static DailyAnalytics DailyAnalyticsBuilder() { return new DailyAnalytics();}

    public DailyAnalytics build() {

        DailyAnalytics response = new DailyAnalytics();

        response.day_num = this.day_num;
        response.all_sales = this.all_sales;
        response.male_sales = this.male_sales;
        response.female_sales = this.female_sales;
        response.young_sales = this.young_sales;
        response.old_sales = this.old_sales;

        return response;
    }

    private DailyAnalytics(){

    }

    public int getAll_sales() {
        return all_sales;
    }

    public int getDay_num() {
        return day_num;
    }

    public int getFemale_sales() {
        return female_sales;
    }

    public int getMale_sales() {
        return male_sales;
    }

    public int getOld_sales() {
        return old_sales;
    }

    public int getYoung_sales() {
        return young_sales;
    }

    public DailyAnalytics setAll_sales(int all_sales) {
        this.all_sales = all_sales;
        return this;
    }

    public DailyAnalytics setDay_num(int day_num) {
        this.day_num = day_num;
        return this;
    }

    public DailyAnalytics setFemale_sales(int female_sales) {
        this.female_sales = female_sales;
        return this;
    }

    public DailyAnalytics setMale_sales(int male_sales) {
        this.male_sales = male_sales;
        return this;
    }

    public DailyAnalytics setOld_sales(int old_sales) {
        this.old_sales = old_sales;
        return this;
    }

    public DailyAnalytics setYoung_sales(int young_sales) {
        this.young_sales = young_sales;
        return this;
    }

}
