package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.CategoryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends AbstractPojo<CategoryDto> {

    public enum CategoryName {
        // general categories
        SHOPPING(Type.EXPENSE), GROCERIES(Type.EXPENSE), UTILITIES(Type.EXPENSE), TRAVEL(Type.EXPENSE),
        TRANSPORT(Type.EXPENSE), ENTERTAINMENT(Type.EXPENSE), HEALTH(Type.EXPENSE),
        RESTAURANTS(Type.EXPENSE), INVESTMENTS(Type.EXPENSE), INCOME(Type.INCOME), OTHERS(Type.EXPENSE);

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
