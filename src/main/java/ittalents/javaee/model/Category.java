package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Category {

    public enum CategoryName {
        // general categories
        FOOD_AND_DRINKS, SHOPPING, HOUSING, TRANSPORTATION, VEHICLE, LIFE_AND_ENTERTAINMENT,
        COMMUNICATION_PC, FINANCIAL_EXPENSES, INVESTMENTS, INCOME, OTHERS
    }

    //    private static int uniqueId;
    private int id;
    private CategoryName name;
    private String iconURL;
    private Type type;


    public Category(CategoryName name, String iconURL, Type type) {
        this.name = name;
        this.iconURL = iconURL;
        this.type = type;
    }
}
