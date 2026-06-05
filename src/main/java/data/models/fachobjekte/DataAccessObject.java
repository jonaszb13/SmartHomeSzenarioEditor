package data.models.fachobjekte;

import java.util.UUID;

public class DataAccessObject {
    private final UUID id;
    private String name;

    public DataAccessObject(final UUID id, final String name) {
        this.id = id;
        this.name = name;
    }


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
