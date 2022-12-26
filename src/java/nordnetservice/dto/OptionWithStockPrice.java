package nordnetservice.dto;

public class OptionWithStockPrice {
    private final StockPriceDTO stockPrice;
    private final OptionDTO option;
    
    public OptionWithStockPrice (OptionDTO option, StockPriceDTO stockPrice) {
        this.stockPrice = stockPrice;
        this.option = option;
    }

    public StockPriceDTO getStock() {
        return stockPrice;
    }

    public OptionDTO getOption() {
        return option;
    }
}
