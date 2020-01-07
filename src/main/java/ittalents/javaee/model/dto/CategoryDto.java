package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.Type;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryDto extends AbstractDto{

    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category.CategoryName categoryName;

    @NotNull
    private String iconURL;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;
}
