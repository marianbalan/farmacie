package utils.observer;

import utils.events.Event;

public interface Observable {
    void addObserver(Observer e);
    void notifyObservers(Event t);
}
