package at.ac.tuwien.infosys.dslab.common.observer;

public interface Observer<T> {
    public void update(Observable observable, T t);
}
