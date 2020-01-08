package ittalents.javaee.model.pojo;


import ittalents.javaee.model.dto.AbstractDto;

public abstract class AbstractPojo<G, T extends AbstractDto> {

    abstract void fromDto(T dto);

    abstract G toDto();
}
