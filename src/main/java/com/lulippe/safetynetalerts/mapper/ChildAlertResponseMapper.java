package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.childAlert.model.ChildAlertResponse;
import com.lulippe.safetynetalerts.childAlert.model.ChildInformation;
import com.lulippe.safetynetalerts.childAlert.model.HouseholdMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChildAlertResponseMapper {
    ChildAlertResponseMapper INSTANCE = Mappers.getMapper(ChildAlertResponseMapper.class);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    HouseholdMember toHouseholdMember(final String firstName, final String lastName);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "age", source = "childAge")
    @Mapping(target = "householdMembers", source = "householdMembers")
    ChildInformation toChildInformation(String firstName, String lastName, int childAge, List<HouseholdMember> householdMembers);


    default ChildAlertResponse toChildAlertResponse(List<ChildInformation> childInformationList){
        ChildAlertResponse childAlertResponse = new ChildAlertResponse();
        childAlertResponse.setChildren(childInformationList);
        return childAlertResponse;
    }
}
