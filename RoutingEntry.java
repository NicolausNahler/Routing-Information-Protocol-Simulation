package RIP;

import java.util.Objects;

/**
 * represents a routing entry of a routing table
 *
 * @author Nicolaus Nahler
 */
public class RoutingEntry {

    /**
     * represents the next-hop of a route
     */
    private final IPAddress nextHop;

    /**
     * represents the hopCount of a route
     */
    private int hopCount;

    /**
     * constructor
     *
     * @param nextHop  nextHop
     * @param hopCount hopCount
     */
    public RoutingEntry(String nextHop, int hopCount) {
        this.nextHop = new IPAddress(nextHop);
        int MAX_HOP_COUNT = 16;
        this.hopCount = Math.min(hopCount, MAX_HOP_COUNT);
    }

    /**
     * getter for the next-hop
     *
     * @return IPAddress
     */
    public IPAddress getNextHop() {
        return nextHop;
    }

    /**
     * getter for the hop-count
     *
     * @return int
     */
    public int getHopCount() {
        return hopCount;
    }

    /**
     * setter for the hop-count
     *
     * @param hopCount hop-count
     */
    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutingEntry that = (RoutingEntry) o;
        return hopCount == that.hopCount && Objects.equals(nextHop, that.nextHop);
    }

    @Override
    public String toString() {
        return "Next-Hop [" + nextHop + "], Hop-Count [" + hopCount + "]";
    }
}
