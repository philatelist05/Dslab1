package at.ac.tuwien.infosys.dslab.common.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class RMIClient {

    private Registry registry;
    private Remote stub;

    public RMIClient(String host, int port) throws RemoteException {
        this.registry = LocateRegistry.getRegistry(host, port);
    }

    @SuppressWarnings (value="unchecked")
    public <T> T lookup(String name, Class<T> target) throws RemoteException {
        try {
            this.stub = this.registry.lookup(name);
            return (T) this.stub;
        } catch (Exception e) {
            throw new RemoteException(e.toString());
        }
    }

    public void stop() {
        this.registry = null;
        this.stub = null;
    }
}