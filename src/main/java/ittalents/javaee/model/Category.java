package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    public enum CategoryName {
        // general categories
        FOOD_AND_DRINKS(Type.EXPENSE), SHOPPING(Type.EXPENSE), HOUSING(Type.EXPENSE), TRANSPORTATION(Type.EXPENSE),
        VEHICLE(Type.EXPENSE), LIFE_AND_ENTERTAINMENT(Type.EXPENSE), COMMUNICATION_PC(Type.EXPENSE),
        FINANCIAL_EXPENSES(Type.EXPENSE), INVESTMENTS(Type.EXPENSE), INCOME(Type.INCOME), OTHERS(Type.EXPENSE);

        private Type type;

        CategoryName(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private CategoryName name;
    private String iconURL;

    @Enumerated(EnumType.STRING)
    private Type type;

    public Category(CategoryName name, String iconURL){
        this.name = name;
        this.iconURL = iconURL;
        this.type = name.getType();
    }

    public void fromDto(CategoryDto categoryDto) {
        this.name = categoryDto.getCategoryName();
        this.iconURL = categoryDto.getIconURL();
//        this.type = categoryDto.getType();
    }

    public CategoryDto toDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setCategoryName(name);
        categoryDto.setIconURL(iconURL);
//        categoryDto.setType(type);
        return categoryDto;
    }
}
