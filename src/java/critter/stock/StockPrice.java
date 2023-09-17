package critter.stock;

import critter.stockoption.StockOptionPrice;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public class StockPrice implements vega.financial.StockPrice {
    private LocalDate localDx;
    private LocalTime tm;
    private double opn;
    private double hi;
    private double lo;
    private double cls;
    private double marketValue;
    private long volume;
    private int oid = -1;
    private List<StockOptionPrice> optionPrices;


    public StockPrice() {
    }

    public StockPrice(Date dx,
                      double opn,
                      double hi,
                      double lo,
                      double cls,
                      long volume) {
        this(dx.toLocalDate(), opn, hi, lo, cls, volume);
    }
    public StockPrice(LocalDate dx,
                      double opn,
                      double hi,
                      double lo,
                      double cls,
                      long volume) {
        this.localDx = dx;
        this.opn = opn;
        this.hi = hi;
        this.lo = lo;
        this.cls = cls;
        this.volume = volume;
    }
    public StockPrice(LocalDate dx,
                      LocalTime tm,
                      double opn,
                      double hi,
                      double lo,
                      double cls,
                      long volume) {
        this(dx,opn,hi,lo,cls,volume);
        this.setTm(tm);
    }


    private Stock stock = null;
    public void setStock(Stock stock) {
        this.stock = stock;
    }
    public Stock getStock() {
        return stock;
    }

    public int getStockId() {
        return stock == null ? -1 : stock.getOid();
    }
    public void assign(StockPrice other) {
        this.setCls(other.getCls());
        this.setHi(other.getHi());
        this.setLo(other.getLo());
        this.setOpn(other.getOpn());
        this.setVolume(other.getVolume());
    }

    public List<StockOptionPrice> getOptionPrices() {
        return optionPrices;
    }
    public void setOptionPrices(List<StockOptionPrice> value) {
        this.optionPrices = value;
    }


    //region Time

    public LocalTime getTm() {
        if (tm == null) {
            tm = LocalTime.now();
        }
        return tm;
    }
    public void setTm(LocalTime tm) {
        this.tm = tm;
    }
    public Time getSqlTime() {
        return new Time(getTm().getHour(),
                getTm().getMinute(),
                getTm().getSecond());
    }

    public Date getDx() {
        return Date.valueOf(localDx);
    }

    public void setDx(Date value) {
        localDx = value.toLocalDate();
    }

    public LocalDate getLocalDx() {
       return localDx;
    }

    public void setLocalDx(LocalDate value) {
        this.localDx = value;
    }
    //endregion Time


    public String toString() {
        return String.format("[%s] dx: %s, tm: %s",
                stock == null ? "-" :stock.getTicker(),
                localDx,
                tm);
    }

    public double getOpn() {
        return opn;
    }
    public void setOpn(double value) {
        opn = value;
    }

    public double getHi() {
        return hi;
    }
    public void setHi(double value) {
        hi = value;
    }

    public double getLo() {
        return lo;
    }
    public void setLo(double value) {
        lo = value;
    }

    public double getCls() {
        return cls;
    }
    public void setCls(double value) {
        cls = value;
    }

    public long getVolume() {
        return volume;
    }
    public void setVolume(long value) {
        volume = value;
    }

    public double getValue() {
        return cls;
    }

    public double getMarketValue() {
        return marketValue;
    }
    public void setMarketValue(double value) {
        marketValue = value;
    }
    public int getOid() {
        return oid;
    }

    //region Non-interface Properties
    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getTicker() {
        return getStock() == null ? "N/A": getStock().getTicker();
    }

    public int getTickerId() {
        return getStock().getOid();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof StockPrice)) return false;

        if (obj == this) return true;

        StockPrice other = (StockPrice)obj;

        if (!localDx.isEqual(other.getLocalDx())) {
            return false;
        }
        if (tm != null) {
            if (other.getTm() == null)   {
                return false;
            }
            if (!tm.equals(other.getTm())) {
                return false;
            }
        }
        if (!isEqual(opn,other.getOpn())) {
            return false;
        }
        if (!isEqual(hi,other.getHi())) {
            return false;
        }
        if (!isEqual(lo,other.getLo())) {
            return false;
        }
        if (!isEqual(cls,other.getCls())) {
            return false;
        }
        if (volume != other.volume) {
            return false;
        }
        return true;
    }
    private static final double TOLERANCE = 0.01;
    private boolean isEqual(double v1, double v2) {
        double diff = Math.abs(v1 - v2);
        return diff < TOLERANCE;
    }
    //endregion
}
