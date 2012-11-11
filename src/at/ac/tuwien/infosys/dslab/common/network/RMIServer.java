package at.ac.tuwien.infosys.dslab.common.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class RMIServer {

    private final Registry registry;
    private String bindingName;
    private Remote stub;
    private Remote toBeStubbed;// used for Strong References (not GCs)

    public RMIServer(int port) {
        try {
            this.registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void bind(String name, Remote stub) {
        try {
            this.toBeStubbed = stub;
            this.stub = UnicastRemoteObject.exportObject(this.toBeStubbed, 0);
            this.bindingName = name;
            this.registry.rebind(name, this.stub);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        try {
            if (this.bindingName != null)
                this.registry.unbind(this.bindingName);
            if (this.toBeStubbed != null)
                UnicastRemoteObject.unexportObject(toBeStubbed, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}