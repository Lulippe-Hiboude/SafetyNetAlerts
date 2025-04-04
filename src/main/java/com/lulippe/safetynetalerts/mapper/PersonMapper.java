package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.model.Person;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);


    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "zip", source = "zip")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "email", source = "email")
    Person ToPerson(final PersonDto personCreateDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "address", source = "address")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "zip", source = "zip")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "email", source = "email")
    Person ToPerson(final String address, final String city, final String zip, final String phone, final String email, @MappingTarget final Person existingPerson);
}
