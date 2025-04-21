package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.medicalRecord.model.AllergyUpdateEntry;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicationUpdateEntry;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedicalRecordMapper {
    Logger log = LoggerFactory.getLogger(MedicalRecordMapper.class);
    MedicalRecordMapper INSTANCE = Mappers.getMapper(MedicalRecordMapper.class);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "birthdate", source = "birthdate")
    @Mapping(target = "medications", source = "medications")
    @Mapping(target = "allergies", source = "allergies")
    MedicalRecord toMedicalRecord(final MedicalRecordDto medicalRecordDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "birthdate", source = "birthdate")
    void partialUpdate(final String birthdate, @MappingTarget final MedicalRecord existingMedicalRecord);
}
