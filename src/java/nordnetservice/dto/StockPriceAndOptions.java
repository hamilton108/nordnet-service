package nordnetservice.dto;

import harborview.dto.html.StockPriceDTO;
import java.util.List;

public class StockPriceAndOptions {
    private final StockPriceDTO stockPrice;
    private final List<OptionDTO> options;
    public StockPriceAndOptions (StockPriceDTO stockPrice, List<OptionDTO> options) {
        this.stockPrice = stockPrice;
        this.options = options;
    }

    public StockPriceDTO getStock() {
        return stockPrice;
    }

    public List<OptionDTO> getOptions() {
        return options;
    }
}
