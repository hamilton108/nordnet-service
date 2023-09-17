package critter.stockoption;

import critter.stock.StockPrice;
import oahu.exceptions.BinarySearchException;
import vega.financial.calculator.OptionCalculator;
import vega.financial.StockOptionType;

import java.util.Optional;

public class StockOptionPrice implements vega.financial.StockOptionPrice {
    private static boolean DEBUG = false;
    private StockOption stockOption;
    private StockPrice stockPrice;
    private double buy;
    private double sell;
    private int oid;
    private OptionCalculator calculator;

    public StockOptionPrice() {
    }

    public StockOptionPrice(StockPrice stockPrice,
                            StockOption stockOption,
                            double buy,
                            double sell,
                            OptionCalculator calculator) {
        this.stockPrice = stockPrice;
        this.stockOption = stockOption;
        this.buy = buy;
        this.sell = sell;
        this.calculator = calculator;
    }

    public StockOptionPrice(StockOption stockOption,
                            double buy,
                            double sell,
                            OptionCalculator calculator) {
        this.stockOption = stockOption;
        this.buy = buy;
        this.sell = sell;
        this.calculator = calculator;
    }
    public double getBuy() {
        return buy;
    }

    public double getSell() {
        return sell;
    }

    @Override
    public Optional<Double> ivBuy() {
        return getIvBuy();
    }


    private Optional<Double> _breakEven = null;
    public Optional<Double> getBreakEven() {
        try {
            if (_breakEven == null) {
                _breakEven = Optional.of(calculator.stockPriceFor(getSell(), this));
            }
        }
        catch (BinarySearchException ex) {
            System.out.println(String.format("[%s] %s",getTicker(),ex.getMessage()));
            _breakEven = Optional.empty();
        }
        return _breakEven;
    }

    public Optional<StockOptionRisc> riscStockPrice(double stockPrice) {
        if (getIvBuy().isPresent()) {
            var adjustedOptionPrice = optionPriceFor(stockPrice);
            var result = new StockOptionRisc(stockPrice,
                    adjustedOptionPrice,
                this);
            return Optional.of(result);
        }
        else {
            return Optional.empty();
        }
    }
    public Optional<StockOptionRisc> riscOptionPrice(double optionPrice) {

        if (getIvBuy().isPresent()) {
            var adjusteStockPrice = stockPriceFor(optionPrice);
            if (adjusteStockPrice.isPresent()) {
                var result = new StockOptionRisc(adjusteStockPrice.get(),
                        optionPrice,
                        this);

                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }


    //private double _currentRiscOptionValue;
    //private Double _currentRiscStockPrice = null;
    public Optional<Double> stockPriceFor(double optionValue) {
        try {
            //Double result = calculator.stockPriceFor(getSell() - optionValue,this);
            Double result = calculator.stockPriceFor(optionValue,this);
            return Optional.of(result);
        }
        catch (BinarySearchException ex) {
            System.out.println(String.format("[%s] %s",getTicker(),ex.getMessage()));
            return Optional.empty();
        }
    }

    public double optionPriceFor(double curStockPrice) {
        double strike = stockOption.getX();
        double expiry = getDays()/365.0;
        Optional<Double> ivBuy = getIvBuy();
        return ivBuy.map(aDouble -> stockOption.getOpType() == StockOptionType.CALL ?
                calculator.callPrice(curStockPrice, strike, expiry, aDouble) :
                calculator.putPrice(curStockPrice, strike, expiry, aDouble)).orElse(-1.0);
    }

    public int getOid() {
        return oid;
    }

    public int getStockId() {
        return stockPrice == null ? -1 : stockPrice.getStockId();
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getTicker() {
        return stockOption == null ? null : stockOption.getTicker();
    }

    public double getX() {
        return stockOption.getX();
    }

    public void setStockOption(StockOption stockOption) {
        this.stockOption = stockOption;
    }

    public int getDerivativeId() {
        return stockOption == null ? -1 : stockOption.getOid();
    }

    @Override
    public vega.financial.StockOption getStockOption() {
        return stockOption;
    }

    public StockPrice getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(StockPrice stockPrice) {
        this.stockPrice = stockPrice;
    }

    public int getStockPriceId() {
        return stockPrice == null ? -1 : stockPrice.getOid();
    }

    public double getDays() {
        return getStockOption().getDays();
        /*
        if (DEBUG) {
            return 209;
        }
        else {
            LocalDate dx = getStockPrice().getLocalDx();

            //LocalDate x = getDerivative().getExpiry().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate x = getDerivative().getExpiry();

            return ChronoUnit.DAYS.between(dx, x);
        }
         */
    }

    private Optional<Double> _ivBuy = null;
    public Optional<Double> getIvBuy() {
        if (_ivBuy == null) {
            try {
                _ivBuy = Optional.of(calculator.iv(this, StockOption.BUY));
            }
            catch (BinarySearchException ex) {
                System.out.println(String.format("[%s] %s",getTicker(),ex.getMessage()));
                _ivBuy = Optional.empty();
            }
        }
        return _ivBuy;
    }

    private Optional<Double> _ivSell = null;
    public Optional<Double> getIvSell() {
        if (_ivSell == null) {
            try {
                _ivSell = Optional.of(calculator.iv(this, StockOption.SELL));
            }
            catch (BinarySearchException ex) {
                System.out.println(String.format("[%s] %s",getTicker(),ex.getMessage()));
                _ivSell = Optional.empty();
            }
        }
        return _ivSell;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }

    public void setCalculator(OptionCalculator calculator) {
        this.calculator = calculator;
    }
}
