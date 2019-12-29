package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Currency {

    public enum CurrencyName {
        BGN, USD, EUR
    }

    private int id;
    private CurrencyName name;

    public Currency(CurrencyName name) {
        this.name = name;
    }
}
