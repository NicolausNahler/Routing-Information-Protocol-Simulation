package RIP;

import java.util.TreeMap;

/**
 * represents a router
 *
 * @author Nicolaus Nahler
 */
public class Router {

    /**
     * represents the name of a router
     */
    private final String name;

    /**
     * represents the routing table of a router
     */
    private final RoutingTable routingTable = new RoutingTable();

    /**
     * constructor for a router
     *
     * @param name name
     */
    public Router(String name) {
        this.name = name;
    }

    /**
     * getter for the routing table
     *
     * @return RoutingTable
     */
    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    /**
     * adds a local net into the routing table
     *
     * @param subnet local subnet
     */
    public void addLocalNet(String subnet) {
        routingTable.addEntry(subnet, "127.0.0.1", 0);
    }

    /**
     * adds a default route into the routing table
     *
     * @param nextHop next hop
     */
    public void addDefaultRoute(String nextHop) {
        routingTable.addEntry("0.0.0.0/0", nextHop, 0);
    }

    /**
     * adds a route into the routing table
     *
     * @param subnet   subnet
     * @param nextHop  next hop
     * @param hopCount hop count
     */
    public void addRoute(String subnet, String nextHop, int hopCount) {
        routingTable.addEntry(subnet, nextHop, hopCount);
    }

    /**
     * sends an update to an ip address
     *
     * @param targetIPAddress ip address
     * @return a TreeMap<Subnet, Integer> with represents the update packet being sent
     */
    public TreeMap<Subnet, Integer> sendUpdate(String targetIPAddress) {
        return routingTable.sendUpdate(new IPAddress(targetIPAddress));
    }

    /**
     * receives an update from an ip address
     *
     * @param packet       update packet
     * @param srcIPAddress ip address
     */
    public void receiveUpdate(TreeMap<Subnet, Integer> packet, String srcIPAddress) {
        packet.keySet().forEach(subnet -> routingTable.addEntry(subnet.format(), srcIPAddress, packet.get(subnet) + 1));
    }

    /**
     * poisons every route to a subnet
     *
     * @param subnet subnet
     */
    public void poisonRoute(String subnet) {
        routingTable.getRoutingTable().get(new Subnet(subnet)).setHopCount(16);
    }

    /**
     * poisons every route learned from a specific router
     *
     * @param address the ip address of the router
     */
    public void poisonRoutesFromRouter(String address) {
        routingTable.getRoutingTable().entrySet().stream().filter(entry -> entry.getValue().getNextHop().equals(new IPAddress(address))).forEach(entry -> entry.getValue().setHopCount(16));
    }

    @Override
    public String toString() {
        return "Router [" + name + "]";
    }
}
