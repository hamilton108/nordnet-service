package nordnetservice.factory;

import java.time.LocalDate;
import critter.stock.Stock;
import critter.stock.StockPrice;
import critter.stockoption.StockOption;
import critter.stockoption.StockOptionPrice;
//import critter.stockoption.StockOptionPurchase;
import vega.financial.calculator.BlackScholes;
import vega.financial.calculator.OptionCalculator;
import vega.financial.StockOptionType;

public class StockMarketFactory {
    private final LocalDate currentDate;
    private final OptionCalculator optionCalculator = new BlackScholes();
    public StockMarketFactory(LocalDate currentDate) {
        this.currentDate = currentDate;
    }
    public Stock createStock(int oid) {
        var result = new Stock();
        result.setOid(oid);
        switch (oid) {
            case 1:
                result.setCompanyName("Norsk Hydro");
                result.setTicker("NHY");
                break;
            case 2:
                result.setCompanyName("Equinor");
                result.setTicker("EQNR");
                break;
            case 3:
                result.setCompanyName("Yara");
                result.setTicker("YAR");
                break;
        }
        return result;
    }
    public StockPrice createStockPrice(int oid,
                                          double opn,
                                          double hi,
                                          double lo,
                                          double cls) {
        var result = new StockPrice();

        var stock = createStock(oid);

        result.setOpn(opn);
        result.setHi(hi);
        result.setLo(lo);
        result.setCls(cls);
        result.setStock(stock);
        result.setVolume(1000);
        result.setLocalDx(currentDate);

        return result;
    }

    public StockOption createStockOption(String ticker,
                                         double x,
                                         StockOptionType optionType,
                                         StockPrice stockPrice) {

        StockOption so = new StockOption();
        so.setTicker(ticker);
        so.setLifeCycle(StockOption.LifeCycle.FROM_HTML);
        so.setOpType(optionType);
        so.setX(x);
        so.setCurrentDate(currentDate);
        so.setStock(stockPrice.getStock());
        return so;
    }

    public StockOptionPrice createStockOptionPrice(StockOption stockOption,
                                                   StockPrice stockPrice,
                                                   double bid,
                                                   double ask,
                                                   OptionCalculator optionCalculator) {

        StockOptionPrice price = new StockOptionPrice();
        price.setStockOption(stockOption);
        price.setStockPrice(stockPrice);
        price.setBuy(bid);
        price.setSell(ask);
        price.setCalculator(optionCalculator);
        return price;
    }

    public StockOptionPrice nhy() {
        StockPrice sp = createStockPrice(1, 69.52, 71.9, 68.94, 70.98);
        StockOption opt = createStockOption("NHY2L58", 58.0,
                                                StockOptionType.CALL, sp);
        return createStockOptionPrice(opt, sp, 16.00, 18.00, optionCalculator);
    }
    public StockOptionPrice yar(String optionTicker,
                                double x,
                                double bid,
                                double ask,
                                double stockPrice) {
        StockPrice sp = createStockPrice(3, 0.9*stockPrice, 1.1*stockPrice, 0.8*stockPrice, stockPrice);
        StockOption opt = createStockOption(optionTicker, x,
                StockOptionType.CALL, sp);
        return createStockOptionPrice(opt, sp, bid, ask, optionCalculator);
    }

    /*
    public StockOptionPurchase createPurchase(StockOptionPrice price) {
        var p = new StockOptionPurchase();
        p.setOptionName(price.getTicker());
        p.setStatus(11);
        p.setBuyAtPurchase(price.getBuy());
        p.setX(price.getX());
        p.setLocalDx(price.getStockPrice().getLocalDx());
        var expiry = ((StockOption)price.getStockOption()).getExpirySql();
        p.setExpirySql(expiry);
        return p;
    }
    */
}
