package com.smarthome.model.service;

import com.smarthome.model.entity.Geraet;
import com.smarthome.model.entity.Raum;
import com.smarthome.model.repository.GeraetRepository;
import com.smarthome.model.repository.RaumRepository;
import com.smarthome.util.StatusLog;
import com.smarthome.util.customExceptions.NoGeraetProvidedException;

import java.sql.SQLException;
import java.util.*;

public final class RaumService {

    private static RaumService instance;
    private final RaumRepository raumRepository;
    private final GeraetRepository geraetRepository;

    private RaumService(final RaumRepository raumRepository, final GeraetRepository geraetRepository) {
        this.raumRepository = raumRepository;
        this.geraetRepository = geraetRepository;
    }

    public static RaumService setup(final RaumRepository raumRepository, final GeraetRepository geraetRepository) {
        instance = new RaumService(raumRepository, geraetRepository);
        return instance;
    }

    public static RaumService getInstance() {
        return instance;
    }

    public void addRaum(final String name) {
        if (name == null || name.isBlank()) {
            StatusLog.addError("Raumname darf nicht leer sein");
            return;
        }
        try {
            raumRepository.addRaum(UUID.randomUUID(), name);
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
    }

    public boolean updateRaum(final UUID id, final String name) {
        if (name == null || name.isBlank()) {
            StatusLog.addError("Raumname darf nicht leer sein");
            return false;
        }
        try {
            raumRepository.updateRaum(id, name);
            return true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
            return false;
        }
    }

    public void deleteRaum(final UUID id) {
        try {
            raumRepository.deleteRaum(id);
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
    }

    public List<Raum> getAlleRaeume() {
        try {
            Map<UUID, Raum> raumMap = new HashMap<>();
            Map<UUID, Geraet> geraetMap = new HashMap<>();
            raumRepository.mapAllRaeume(raumMap);
            geraetRepository.mapAllGeraete(raumMap, geraetMap);
            return new ArrayList<>(raumMap.values());
        } catch (SQLException | NoGeraetProvidedException e) {
            StatusLog.addError(e);
            return List.of();
        }
    }
}
