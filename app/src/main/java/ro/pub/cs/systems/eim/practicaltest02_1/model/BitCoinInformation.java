package ro.pub.cs.systems.eim.practicaltest02_1.model;

public class BitCoinInformation {

    public BitCoinInformation(String date_last_update, Double val_bitcoin) {
        this.date_last_update = date_last_update;
        this.val_bitcoin = val_bitcoin;
    }

    public String getDate_last_update() {
        return date_last_update;
    }

    public double getVal_bitcoin() {
        return val_bitcoin;
    }

    public void setDate_last_update(String date_last_update) {
        this.date_last_update = date_last_update;
    }

    public void setVal_bitcoin(double val_bitcoin) {
        this.val_bitcoin = val_bitcoin;
    }

    private String date_last_update;
    private Double val_bitcoin;

    @Override
    public String toString() {
        return "BitcoinInformation{" +
                "date last updated='" + date_last_update + '\'' +
                ", bitcoin value='" + val_bitcoin + '\'' +
                '}';
    }
}
