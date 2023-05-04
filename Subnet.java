package RIP;

import java.util.Objects;

/**
 * represents a subnet
 *
 * @author Nicolaus Nahler
 */
public class Subnet implements Comparable<Subnet> {

    /**
     * network address
     */
    private IPAddress net;

    /**
     * network mask
     */
    private IPAddress mask;

    /**
     * cidr
     */
    private int cidr;

    /**
     * create netmask from network ip and number of bits
     *
     * @param net  network address
     * @param cidr number of bits
     */
    public Subnet(IPAddress net, int cidr) {
        createMask(net, cidr);
    }

    /**
     * creates a mask
     *
     * @param net  ip address
     * @param cidr mask in cidr form
     */
    private void createMask(IPAddress net, int cidr) {
        this.net = net;
        this.mask = IPAddress.createNetmask(cidr);
        this.cidr = cidr;

        if ((this.net.getIP() & mask.getIP()) != this.net.getIP() || cidr > 32) {
            throw new IllegalArgumentException("bad network");
        }
    }

    /**
     * create netmask from ip (four number) and number of bits
     *
     * @param a3   first octet
     * @param a2   second octet
     * @param a1   third octet
     * @param a0   fourth octet
     * @param cidr the subnet mask
     */
    public Subnet(int a3, int a2, int a1, int a0, int cidr) {
        this(new IPAddress(a3, a2, a1, a0), cidr);
    }

    /**
     * constructor for a string
     *
     * @param mask mask
     */
    public Subnet(String mask) {
        String[] parts = mask.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("ill formed subnet");
        }
        createMask(new IPAddress(parts[0]), Integer.parseInt(parts[1]));
    }

    /**
     * gets the net address
     *
     * @return IPAddress
     */
    public IPAddress getNet() {
        return net;
    }

    /**
     * gets the net mask
     *
     * @return IPAddress
     */
    public IPAddress getMask() {
        return mask;
    }

    /**
     * gets the broadcast address
     *
     * @return IPAddress
     */
    public IPAddress getBroadcast() {
        return new IPAddress(net.getIP() + ~mask.getIP());
    }

    /**
     * is IP in this network
     *
     * @param ip the ip address
     * @return boolean
     */
    public boolean contains(IPAddress ip) {
        return (ip.getIP() & mask.getIP()) == net.getIP();
    }

    @Override
    public String toString() {
        return "Subnet [net=" + net + ", mask=" + mask + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subnet subnet = (Subnet) o;
        return Objects.equals(net, subnet.net) && Objects.equals(mask, subnet.mask);
    }

    @Override
    public int compareTo(Subnet subnet) {
        return net.compareTo(subnet.net) == 0 ? mask.compareTo(subnet.mask) : net.compareTo(subnet.net);
    }

    /**
     * formats a subnet in a different way
     *
     * @return String
     */
    public String format() {
        return net.format() + "/" + cidr;
    }
}
