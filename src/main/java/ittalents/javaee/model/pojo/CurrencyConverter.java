package ittalents.javaee.model.pojo;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

public class CurrencyConverter {

    public static double convert(Currency currencyFrom, Currency currencyTo, double amount) {
        MonetaryAmount from = Monetary.getDefaultAmountFactory().setCurrency(currencyFrom.toString())
                .setNumber(amount).create();
        CurrencyConversion conversion = MonetaryConversions.getConversion(currencyTo.toString());
        MonetaryAmount result = from.with(conversion);

        double value = result.getNumber().doubleValueExact();
        String formattedString = String.format("%.2f", value);

        return Double.parseDouble(formattedString);
    }
}
