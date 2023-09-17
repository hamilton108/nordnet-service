package critter.stockoption;

import critter.stock.Stock;
import critter.util.StockOptionUtil;
import vega.financial.StockOptionType;

import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;

public class StockOption implements vega.financial.StockOption {

    //private StockOptionUtil stockOptionUtil;
    private LocalDate currentDate;
    private final Pattern p = Pattern.compile("\\D+(\\d\\D)\\d+");
    private Stock stock;


    public static int BUY = 1;
    public static int SELL = 2;


    //public enum OptionType { CALL, PUT, UNDEF };

    public enum LifeCycle { FROM_HTML, SAVED_TO_DATABASE, FROM_DATABASE }

    public StockOption() {
    }


    public StockOption(String ticker,
                       StockOptionType opType,
                       double x,
                       Stock stock,
                   LocalDate currentDate) {
        this.ticker = ticker;
        this.opType = opType;
        this.x = x;
        this.stock = stock;
        this.currentDate = currentDate;
    }

    /*
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getTicker());
           buf.append("\n\texpiry: ").append(getExpiry())
           .append("\n\toption type: ").append(getOpType() == OptionType.CALL ? "apply" : "put")
           .append("\n\tx: ").append(getX())
           //.append("\n\tbuy ").append(getBuy())
           //.append("\n\tsell ").append(getSell())
           .append("\n\tseries ").append(getSeries());
           //.append("\n\twatermark ").append(getWatermark());
        return buf.toString();
    }
    //*/

    //--------------------------------------------------
    //------------- Parent
    //--------------------------------------------------
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
    private Integer _stockId = null;
    public int getStockId() {
        return stock == null ? _stockId : stock.getOid();
    }
    public void setStockId(int value) {
        _stockId = value;
    }


    //--------------------------------------------------
    //------------- Id
    //--------------------------------------------------
    private int oid;
    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    //--------------------------------------------------
    //------------- Expiry
    //--------------------------------------------------
    private LocalDate expiry;
    public LocalDate getExpiry() {
        if (expiry == null) {
            String series = getSeries();
            expiry = StockOptionUtil.seriesAsDate(series);
        }
        return expiry;
    }
    public void setExpiry(LocalDate value) {
        expiry = value;
    }

    public Date getExpirySql() {
        return Date.valueOf(getExpiry());
    }

    public void setExpirySql(Date expirySql) {
        this.expiry = expirySql.toLocalDate();
    }

    //--------------------------------------------------
    //------------- OpType
    //--------------------------------------------------
    private StockOptionType opType;
    public StockOptionType getOpType() {
        return opType;
    }


    public void setOpType(StockOptionType value) {
            opType = value;
    }

    public String getOpTypeStr() {
        return getOpType() == StockOptionType.CALL ? "c" : "p";
    }
    public void setOpTypeStr(String value) {
        if (value.equals("c")) {
            setOpType(StockOptionType.CALL);
        }
        else {
            setOpType(StockOptionType.PUT);
        }
    }

    //--------------------------------------------------
    //--------------- Life Cycle -----------------------
    //--------------------------------------------------
    private LifeCycle lifeCycle;
    public LifeCycle getLifeCycle() {
        return lifeCycle;
    }
    public void setLifeCycle(LifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }


    //--------------------------------------------------
    //------------- Series
    //--------------------------------------------------

    private String series;
    public String getSeries() {
        if (series == null) {
            Matcher m = p.matcher(getTicker());
            if (m.find()) {
                series = m.group(1);
            }
            else {
                //series = "??";
                throw new RuntimeException("Series undefined for " + ticker);
            }
        }
        return series;
    }

    public long getDays() {
        return DAYS.between(getCurrentDate(),getExpiry());
    }

    public void setSeries(String value) {
        series = value;
    }

    //--------------------------------------------------
    //------------- X
    //--------------------------------------------------
    private double x;
    public double getX() {
        return x;
    }
    public void setX(double value) {
        x = value;
    }


    //--------------------------------------------------
    //------------- Ticker
    //--------------------------------------------------
    private String ticker;
    public String getTicker() {
        return ticker;
    }



    public void setTicker(String value) {
        ticker = value;
    }

    /*
    public void setStockOptionUtil(StockOptionUtil stockOptionUtil) {
        this.stockOptionUtil = stockOptionUtil;
    }

    public StockOptionUtil getStockOptionUtil() {
        return stockOptionUtil;
    }
     */

    public LocalDate getCurrentDate() {
        /*
        if (currentDate == null) {
            //currentDate = getExpiry().minusDays(60);
            currentDate = stockOptionUtil.getCurrentDate();
        }
         */
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }
}
