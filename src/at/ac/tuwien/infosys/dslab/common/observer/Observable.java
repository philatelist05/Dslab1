package at.ac.tuwien.infosys.dslab.common.observer;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class Observable<T> {

    private final List<Observer<T>> observers;

    public Observable() {
        this.observers = new LinkedList<Observer<T>>();
    }

    protected final void notifyObservers(T t) {
        for (Observer<T> observer : this.observers) {
            observer.update(this, t);
        }
    }

    public final void addObserver(Observer<T> observer) {
        this.observers.add(observer);
    }
}
