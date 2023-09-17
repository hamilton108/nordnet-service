package critter.util;


import oahu.dto.Tuple3;
import oahu.exceptions.FinancialException;
import vega.financial.StockOptionType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockOptionUtil {
    //private Map<String, LocalDate> seriesMap;
    private final LocalDate currentDate;
    private static final Pattern pattern = Pattern.compile("(\\D+)\\d(\\D)\\d+", Pattern.CASE_INSENSITIVE);

    public StockOptionUtil() {
        this.currentDate = LocalDate.now();
    }

    public StockOptionUtil(LocalDate currentDate) {
        this.currentDate = currentDate;
        //populate();
    }

    public static Tuple3<Integer, String, StockOptionType> stockOptionInfoFromTicker(String priceTicker) {
        Matcher m = pattern.matcher(priceTicker);
        if (m.find()) {
            String ticker = m.group(1);
            String optionType = m.group(2);
            int oid = stockTickerToOid(ticker);
            StockOptionType opt = asOptionType(optionType);
            return new Tuple3<>(oid,ticker,opt);
        }
        throw new FinancialException(String.format("No stock option info for %s", priceTicker));
    }

    private static StockOptionType asOptionType(String optionTypeStr) {
        switch (optionTypeStr) {
            case "A":
            case "B":
            case "C":
            case "D":
            case "E":
            case "F":
            case "G":
            case "H":
            case "I":
            case "J":
            case "K":
            case "L":
                return StockOptionType.CALL;
            case "M":
            case "N":
            case "O":
            case "P":
            case "Q":
            case "R":
            case "S":
            case "T":
            case "U":
            case "V":
            case "W":
            case "X":
                return StockOptionType.PUT;
        }
        throw new FinancialException(String.format("No option type for %s", optionTypeStr));
    }

    public static int stockTickerToOid(String ticker) {
        switch (ticker) {
            case "NHY": return 1;
            case "EQNR": return 2;
            case "YAR": return 3;
            case "TEL": return 6;
            case "OBX": return 7;
            case "ORK": return 9;
            case "REC": return 11;
            case "PGS": return 12;
            case "STB": return 14;
            case "TGS": return 16;
            case "TOM": return 17;
            case "AKSO": return 18;
            case "DNB": return 19;
            case "DNO": return 20;
            case "GJF": return 21;
            case "SUBC": return 23;
            case "AKERBP": return 25;
            case "BWLPG": return 26;
            case "BAKKA": return 27;
            case "GOGL": return 28;
            case "NAS": return 29;
        }
        throw new FinancialException(String.format("No oid for stock ticker %s", ticker));
    }
    /*
    oid | ticker |     company_name     | status | ticker_category
-----+--------+----------------------+--------+-----------------
    1 | NHY    | Norsk hydro          |      1 |               1
    2 | EQNR   | Equinor              |      1 |               1
    3 | YAR    | Yara                 |      1 |               1
    6 | TEL    | Telenor              |      1 |               1
    7 | OBX    | Total Return Index   |     -1 |               3
    9 | ORK    | Orkla                |      1 |               1
    11 | REC    | Renewable Energy Cor |      1 |               1
    12 | PGS    | Petroleum Geo-Serv   |      1 |               1
    14 | STB    | Storebrand           |      1 |               1
    16 | TGS    | TGS-NOPEC Geophysica |      1 |               1
    17 | TOM    | Tomra                |      1 |               1
    18 | AKSO   | Aker Solutions       |      1 |               1
    19 | DNB    | DNB                  |      1 |               1
    20 | DNO    | DNO International    |      1 |               1
    21 | GJF    | Gjensidige Forsikr   |      1 |               1
    23 | SUBC   | Subsea 7             |      1 |               1
    25 | AKERBP | Aker BP              |      1 |               1
    26 | BWLPG  | BW LPG               |      1 |               1
    27 | BAKKA  | Bakkafrost           |      1 |               1
    28 | GOGL   | Golden Ocean Group   |      1 |               1
    29 | NAS    | Norw. Air Shuttle    |      1 |               1
     */


    public static LocalDate seriesAsDate(String series) {
        String ys = series.substring(0,1);
        String ms = series.substring(1,2);
        int year = 0;
        int month = 0;
        switch (ys) {
            case "0":
                year = 2020;
                break;
            case "1":
                year = 2021;
                break;
            case "2":
                year = 2022;
                break;
            case "3":
                year = 2023;
                break;
            case "4":
                year = 2024;
                break;
        }
        switch (ms) {
            case "A":
            case "M":
                month = 1;
                break;
            case "B":
            case "N":
                month = 2;
                break;
            case "C":
            case "O":
                month = 3;
                break;
            case "D":
            case "P":
                month = 4;
                break;
            case "E":
            case "Q":
                month = 5;
                break;
            case "F":
            case "R":
                month = 6;
                break;
            case "G":
            case "S":
                month = 7;
                break;
            case "H":
            case "T":
                month = 8;
                break;
            case "I":
            case "U":
                month = 9;
                break;
            case "J":
            case "V":
                month = 10;
                break;
            case "K":
            case "W":
                month = 11;
                break;
            case "L":
            case "X":
                month = 12;
                break;
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        if (!startDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            startDate = startDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        }
        return startDate.plus(Period.ofDays(14));
    }

    /*
    private void populate() {
        seriesMap = new HashMap<>();
    }
     */

    public LocalDate getCurrentDate() {
        return currentDate;
    }
}
