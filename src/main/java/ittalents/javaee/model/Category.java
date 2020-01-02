package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    public enum CategoryName {
        // general categories
        FOOD_AND_DRINKS, SHOPPING, HOUSING, TRANSPORTATION, VEHICLE, LIFE_AND_ENTERTAINMENT,
        COMMUNICATION_PC, FINANCIAL_EXPENSES, INVESTMENTS, INCOME, OTHERS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private CategoryName name;
    private String iconURL;

    @Enumerated(EnumType.STRING)
    private Type type;

    public void fromDto(CategoryDto categoryDto) {
        this.name = categoryDto.getCategoryName();
        this.iconURL = categoryDto.getIconURL();
        this.type = categoryDto.getType();
    }

    public CategoryDto toDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setCategoryName(name);
        categoryDto.setIconURL(iconURL);
        categoryDto.setType(type);
        return categoryDto;
    }
}
