package ittalents.javaee.model.pojo;


import ittalents.javaee.model.dto.AbstractDto;

public abstract class AbstractPojo<T extends AbstractDto> {

    abstract void fromDto(T dto);

    abstract T toDto();
}
