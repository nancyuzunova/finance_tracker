package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.AbstractDto;

public abstract class AbstractPojo<F, T extends AbstractDto> {

    abstract void fromDto(F dto);

    abstract T toDto();
}
