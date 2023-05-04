package RIP;

/**
 * represents an ip address
 *
 * @author Nicolaus Nahler
 */
public class IPAddress implements Comparable<IPAddress> {

    /**
     * ip as integer
     */
    private int ip;

    /**
     * constructor for four integers
     *
     * @param a3 first octet
     * @param a2 second octet
     * @param a1 third octet
     * @param a0 fourth octet
     */
    public IPAddress(int a3, int a2, int a1, int a0) {
        createIP(a3, a2, a1, a0);
    }

    /**
     * creates an ip address from four integers
     *
     * @param a3 first octet
     * @param a2 second octet
     * @param a1 third octet
     * @param a0 fourth octet
     */
    private void createIP(int a3, int a2, int a1, int a0) {
        this.ip = (a3 << 24) + (a2 << 16) + (a1 << 8) + a0;
    }

    /**
     * create IP from given integer (internal use)
     */
    IPAddress(int ip) {
        this.ip = ip;
    }

    /**
     * constructor for a string
     *
     * @param ip ip address
     */
    public IPAddress(String ip) {
        String[] nums = ip.split("\\.");
        if (nums.length != 4) {
            throw new IllegalArgumentException("ill formed ip");
        }
        createIP(Integer.parseInt(nums[0]),
                Integer.parseInt(nums[1]),
                Integer.parseInt(nums[2]),
                Integer.parseInt(nums[3]));
    }

    /**
     * create IP/Netmask with given number of bits
     */
    public static IPAddress createNetmask(int cidr) {
        return new IPAddress((int) (0xffffffff00000000L >> (cidr)));
    }

    /**
     * @return ip address as integer
     */
    public int getIP() {
        return ip;
    }

    @Override
    public String toString() {
        int a0 = (ip) & 0xff;
        int a1 = (ip >>> 8) & 0xff;
        int a2 = (ip >>> 16) & 0xff;
        int a3 = (ip >>> 24) & 0xff;

        return "IPAddress [" + a3 + "." + a2 + "." + a1 + "." + a0 + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPAddress ipAddress = (IPAddress) o;
        return ip == ipAddress.ip;
    }

    @Override
    public int compareTo(IPAddress ipAddress) {
        return Integer.compareUnsigned(ipAddress.ip, ip);
    }

    /**
     * formats the String in a different way
     *
     * @return String
     */
    public String format() {
        int a0 = (ip) & 0xff;
        int a1 = (ip >>> 8) & 0xff;
        int a2 = (ip >>> 16) & 0xff;
        int a3 = (ip >>> 24) & 0xff;

        return a3 + "." + a2 + "." + a1 + "." + a0;
    }
}
