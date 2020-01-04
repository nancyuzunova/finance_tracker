package ittalents.javaee.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {
    private String message;

    public ApiError(String message) {
        this.message = message;
    }
}
