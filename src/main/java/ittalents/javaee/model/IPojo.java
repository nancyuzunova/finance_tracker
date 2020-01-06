package ittalents.javaee.model;


public interface IPojo {

    void fromDto(IDto dto);

    IDto toDto();
}
