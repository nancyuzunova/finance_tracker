package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public enum Type {

    EXPENSE(2), INCOME(1);

    private int id;

    Type(int id) {
        this.id = id;
    }
}
