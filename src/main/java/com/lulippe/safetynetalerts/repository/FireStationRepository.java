package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
@AllArgsConstructor
public class FireStationRepository implements CrudRepository<FireStation> {
    private final DataManager dataManager;

    protected List<FireStation> getDataList() {
        return dataManager.getDataContainer().getFireStations();
    }

    @Override
    public void save(FireStation entity) {
        findByAddress(entity.getAddress())
                .ifPresentOrElse(existingEntity -> {
                    int index = getDataList().indexOf(existingEntity);
                    getDataList().set(index, entity);
                }, () -> getDataList().add(entity));
    }

    @Override
    public void delete(FireStation entity) {
        getDataList().remove(entity);
    }

    public Optional<FireStation> findByAddress(String address) {
        return getDataList().stream().filter(fireStation -> fireStation.getAddress().equals(address)).findFirst();
    }

    public Optional<FireStation> findByAddressAndStation(String address, String station) {
        return getDataList().stream()
                .filter(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station))
                .findFirst();
    }

    public Optional<FireStation> findByStation(String station) {
        return getDataList().stream().filter(fireStation -> fireStation.getStation().equals(station)).findFirst();
    }

    public OptionalInt getMaxStationNumber(){
        return getDataList().stream()
                .map(FireStation::getStation)
                .mapToInt(Integer::parseInt)
                .max();
    }
}
