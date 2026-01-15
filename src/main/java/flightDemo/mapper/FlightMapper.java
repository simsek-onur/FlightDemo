package flightDemo.mapper;

import flightDemo.dto.FlightDTO;
import flightDemo.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FlightMapper {

    FlightDTO mapToDTO(Flight flight);
}
