package ittalents.javaee.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryDto {

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
