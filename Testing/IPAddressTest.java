package RIP.Testing;

import RIP.IPAddress;

import static org.junit.jupiter.api.Assertions.*;

class IPAddressTest {

    IPAddress ipAddress = new IPAddress("192.168.1.0");

    @org.junit.jupiter.api.Test
    void createNetmask() {
        assertEquals(new IPAddress("255.255.255.0"), IPAddress.createNetmask(24));
    }

    @org.junit.jupiter.api.Test
    void getIP() {
        assertEquals(new IPAddress(192, 168, 1, 0).getIP(), 0xC0A80100);
        assertEquals(new IPAddress("192.168.1.0").getIP(), 0xC0A80100);
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        assertEquals("IPAddress [192.168.1.0]", ipAddress.toString());
    }

    @org.junit.jupiter.api.Test
    void testWrongIPAddress() {
        assertThrows(IllegalArgumentException.class, () -> new IPAddress("1.1.1.1.1"));
        assertThrows(IllegalArgumentException.class, () -> new IPAddress("1.1.1"));
    }

    @org.junit.jupiter.api.Test
    void testCompareTo() {
        assertEquals(1, new IPAddress("192.168.1.1").compareTo(new IPAddress("192.168.1.2")));
        assertEquals(-1, new IPAddress("192.168.1.3").compareTo(new IPAddress("192.168.1.2")));
        assertEquals(0, new IPAddress("192.168.1.1").compareTo(new IPAddress("192.168.1.1")));
    }
}