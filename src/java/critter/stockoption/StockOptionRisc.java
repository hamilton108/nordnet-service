package critter.stockoption;

import java.util.Optional;

public class StockOptionRisc {
    private final double stockPrice;
    private final double optionPrice;

    private final StockOptionPrice price;

    public StockOptionRisc(
                           double stockPrice,
                           double optionPrice,
                           StockOptionPrice price) {
        this.stockPrice = stockPrice;
        this.optionPrice = optionPrice;
        this.price = price;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public double getOptionPrice() {
        return optionPrice;
    }

    public String getOptionTicker() {
        return price.getTicker();
    }

    public double getVolatility() {
        return price.getIvBuy().orElse(-1.0);
    }
    public Optional<Double> getBreakEven() {
        return price.getBreakEven();
    }
    public double getBuy() {
        return price.getBuy();
    }
    public double getSell() {
        return price.getSell();
    }
    public double getRisc() {
        return price.getSell() - optionPrice;
    }
}
