package utils.events;

import model.Comanda;

public class ChangeEvent implements Event {
    private EventType type;

    public ChangeEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

}
