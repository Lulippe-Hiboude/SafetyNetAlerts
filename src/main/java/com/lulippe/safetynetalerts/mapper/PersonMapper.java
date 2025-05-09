package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.fireStation.model.ResidentInfo;
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
    void ToPerson(final String address, final String city, final String zip, final String phone, final String email, @MappingTarget final Person existingPerson);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", source = "person.firstName")
    @Mapping(target = "lastName", source = "person.lastName")
    @Mapping(target = "address", source = "person.address")
    @Mapping(target = "phone", source = "person.phone")
    ResidentInfo ToResidentInfo(final Person person);
}
