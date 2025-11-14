package ir.fidar.parking.service.mapper;



import ir.fidar.parking.service.domain.entity.dto.PaginationDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Utility mapper for converting Spring Data Page objects to pagination DTOs.
 * Works alongside MapStruct for entity-to-DTO conversions.
 */
@Component
public class PaginationMapper {

    public PaginationDto pageToPageDto(Page<?> page) {
        return PaginationDto.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }
}