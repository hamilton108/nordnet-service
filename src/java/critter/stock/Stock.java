package critter.stock;

import critter.stockoption.StockOption;

import java.util.List;

public class Stock {
    private String companyName;
    private String ticker;
    private int status;
    private int oid;
    private List<StockOption> stockOptions;
    private int tickerCategory;

    public String toHtml() {
        return ticker;
    }

    public String getTicker() {
        return ticker;
    }

    public int getTickerCategory() {
        return tickerCategory;
    }

    public void setTickerCategory(int tickerCategory) {
        this.tickerCategory = tickerCategory;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getOid() {
        return oid;
    }
    public void setOid(int oid) {
        this.oid = oid;
    }



    private List<StockPrice> prices;
    public List<StockPrice> getPrices() {
        return prices;
    }
    public void setPrices(List<StockPrice> prices) {
        this.prices = prices;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<StockOption> getDerivatives() {
        return stockOptions;
    }

    public void setDerivatives(List<StockOption> stockOptions) {
        this.stockOptions = stockOptions;
    }

}
