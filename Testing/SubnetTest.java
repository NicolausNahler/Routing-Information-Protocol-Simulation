package RIP.Testing;

import org.junit.jupiter.api.Test;
import RIP.IPAddress;
import RIP.Subnet;

import static org.junit.jupiter.api.Assertions.*;

class SubnetTest {

    IPAddress ipAddress = new IPAddress(192, 168, 1, 0);
    Subnet subnet = new Subnet(ipAddress, 24);

    @Test
    void getNet() {
        assertEquals(new IPAddress("192.168.1.0"), subnet.getNet());
    }

    @Test
    void getMask() {
        assertEquals(new IPAddress("255.255.255.0"), subnet.getMask());
    }

    @Test
    void getBroadcast() {
        assertEquals(new IPAddress("192.168.1.255"), subnet.getBroadcast());
    }

    @Test
    void contains() {
        assertTrue(subnet.contains(new IPAddress(192, 168, 1, 69)));
    }

    @Test
    void createMask() {
        assertEquals(new Subnet(new IPAddress("192.168.1.0"), 24), new Subnet("192.168.1.0/24"));
    }

    @Test
    void testToString() {
        assertEquals("Subnet [net=IPAddress [192.168.1.0], mask=IPAddress [255.255.255.0]]", subnet.toString());
    }

    @Test
    void testWrongSubnet() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("192.168.1.0/33"));
        assertThrows(IllegalArgumentException.class, () -> new Subnet("192.168.1.0-33"));
    }

    @Test
    void testWrongNetwork() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet(192, 168, 1, 0, 33));
    }

    @org.junit.jupiter.api.Test
    void testCompareTo() {
        assertEquals(1, new Subnet("192.168.1.0/24").compareTo(new Subnet("192.168.1.0/25")));
        assertEquals(-1, new Subnet("192.168.1.0/26").compareTo(new Subnet("192.168.1.0/25")));
        assertEquals(1, new Subnet("192.168.0.0/25").compareTo(new Subnet("192.168.1.0/25")));
        assertEquals(0, new Subnet("192.168.1.0/24").compareTo(new Subnet("192.168.1.0/24")));
    }
}