package RIP;

import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * represents the routing table of a router
 *
 * @author Nicolaus Nahler
 */
public class RoutingTable {

    /**
     * represents the routing table as a TreeMap
     */
    private final TreeMap<Subnet, RoutingEntry> routingTable = new TreeMap<>();

    /**
     * getter for routing table
     *
     * @return routingTable
     */
    public TreeMap<Subnet, RoutingEntry> getRoutingTable() {
        return routingTable;
    }

    /**
     * adds an entry into the routing table
     *
     * @param subnetStr subnetStr
     * @param nextHop   nextHop
     * @param hopCount  hopCount
     */
    public void addEntry(String subnetStr, String nextHop, int hopCount) {
        Subnet subnet = new Subnet(subnetStr);
        RoutingEntry routingEntry = new RoutingEntry(nextHop, hopCount);
        RoutingEntry existingEntry = routingTable.get(subnet);

        if (routingTable.containsKey(subnet)) {
            if (routingEntry.getNextHop().equals(existingEntry.getNextHop())) routingTable.put(subnet, routingEntry);
            if (hopCount < existingEntry.getHopCount()) routingTable.put(subnet, routingEntry);
        } else routingTable.put(subnet, routingEntry);
    }

    /**
     * sends an update
     *
     * @param ipAddress ipAddress
     * @return a TreeMap<Subnet, Integer> with represents the update packet being sent
     */
    public TreeMap<Subnet, Integer> sendUpdate(IPAddress ipAddress) {
        return routingTable.keySet().stream().filter(subnet -> !ipAddress.equals(routingTable.get(subnet).getNextHop()))
                .collect(Collectors.toMap(subnet -> subnet, subnet -> routingTable.get(subnet).getHopCount(), (a, b) -> b, TreeMap::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutingTable that = (RoutingTable) o;
        return Objects.equals(routingTable, that.routingTable);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        routingTable.forEach((key, value) -> str
                .append("RoutingEntry [")
                .append(key)
                .append(", ")
                .append(value)
                .append("]\n"));
        return str.toString().trim();
    }
}
