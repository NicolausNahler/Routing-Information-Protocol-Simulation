package RIP.Testing;

import RIP.*;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class RIPTest {

    @org.junit.jupiter.api.Test
    void creatingRouter() {
        Router r1 = new Router("R1");
        assertEquals("Router [R1]", r1.toString());
    }

    @org.junit.jupiter.api.Test
    void addLocalNet() {
        Router r1 = new Router("R1");
        r1.addLocalNet("192.168.1.0/24");
        RoutingTable routingTable = new RoutingTable();
        routingTable.addEntry("192.168.1.0/24", "127.0.0.1", 0);
        assertEquals(r1.getRoutingTable(), routingTable);
        routingTable.addEntry("192.168.0.0/24", "127.0.0.1", 0);
        assertNotEquals(r1.getRoutingTable(), routingTable);
    }

    @org.junit.jupiter.api.Test
    void sortedRoutingTable() {
        Router r1 = new Router("R1");
        r1.addLocalNet("10.0.0.0/30");
        r1.addRoute("10.1.0.0/30", "192.168.1.1", 7);
        r1.addRoute("10.1.0.0/30", "192.168.1.1", 5);
        r1.addLocalNet("192.168.0.0/24");
        r1.addLocalNet("192.168.0.0/28");
        r1.addLocalNet("192.168.10.0/24");
        r1.addDefaultRoute("192.168.3.1");
        assertEquals("RoutingEntry [Subnet [net=IPAddress [192.168.10.0], mask=IPAddress [255.255.255.0]], Next-Hop [IPAddress [127.0.0.1]], Hop-Count [0]]\n" +
                "RoutingEntry [Subnet [net=IPAddress [192.168.0.0], mask=IPAddress [255.255.255.240]], Next-Hop [IPAddress [127.0.0.1]], Hop-Count [0]]\n" +
                "RoutingEntry [Subnet [net=IPAddress [192.168.0.0], mask=IPAddress [255.255.255.0]], Next-Hop [IPAddress [127.0.0.1]], Hop-Count [0]]\n" +
                "RoutingEntry [Subnet [net=IPAddress [10.1.0.0], mask=IPAddress [255.255.255.252]], Next-Hop [IPAddress [192.168.1.1]], Hop-Count [5]]\n" +
                "RoutingEntry [Subnet [net=IPAddress [10.0.0.0], mask=IPAddress [255.255.255.252]], Next-Hop [IPAddress [127.0.0.1]], Hop-Count [0]]\n" +
                "RoutingEntry [Subnet [net=IPAddress [0.0.0.0], mask=IPAddress [0.0.0.0]], Next-Hop [IPAddress [192.168.3.1]], Hop-Count [0]]", r1.getRoutingTable().toString());
    }

    @org.junit.jupiter.api.Test
    void addEntry() {
        RoutingTable rt = new RoutingTable();
        rt.addEntry("10.0.0.0/24", "10.0.0.2", 0);
        Subnet subnet1 = new Subnet("10.0.0.0/24");
        RoutingEntry routingEntry1 = new RoutingEntry("10.0.0.2", 0);
        TreeMap<Subnet, RoutingEntry> tm = new TreeMap<>();
        tm.put(subnet1, routingEntry1);
        assertEquals(tm, rt.getRoutingTable());
        Subnet subnet2 = new Subnet("10.1.0.0/24");
        RoutingEntry routingEntry2 = new RoutingEntry("10.1.0.2", 1);
        tm.put(subnet2, routingEntry2);
        assertNotEquals(tm, rt.getRoutingTable());
        rt.addEntry("10.1.0.0/24", "10.2.0.2", 1);
    }

    @org.junit.jupiter.api.Test
    void addBetterEntry() {
        RoutingTable rt1 = new RoutingTable();
        rt1.addEntry("10.0.0.0/24", "10.0.0.1", 3);
        RoutingTable rt2 = new RoutingTable();
        rt2.addEntry("10.0.0.0/24", "10.0.0.1", 3);
        assertEquals(rt1, rt2);

        rt2.addEntry("10.0.0.0/24", "10.0.0.1", 2);
        RoutingTable rt3 = new RoutingTable();
        rt3.addEntry("10.0.0.0/24", "10.0.0.1", 2);
        assertEquals(rt2, rt3);
    }

    @org.junit.jupiter.api.Test
    void addWorseEntry() {
        RoutingTable rt1 = new RoutingTable();
        rt1.addEntry("10.0.0.0/24", "10.0.0.1", 3);
        RoutingTable rt2 = new RoutingTable();
        rt2.addEntry("10.0.0.0/24", "10.0.0.1", 3);
        assertEquals(rt1, rt2);

        rt2.addEntry("10.0.0.0/24", "10.0.0.4", 4);
        assertEquals(rt1, rt2);
    }

    @org.junit.jupiter.api.Test
    void toStringRoutingTable() {
        Router r1 = new Router("R1");
        r1.addLocalNet("192.168.0.0/30");
        assertEquals("RoutingEntry [Subnet [net=IPAddress [192.168.0.0], mask=IPAddress [255.255.255.252]], " +
                "Next-Hop [IPAddress [127.0.0.1]], Hop-Count [0]]", r1.getRoutingTable().toString());
    }

    @org.junit.jupiter.api.Test
    void addWorseRouteFromSameRouter() {
        RoutingTable rt1 = new RoutingTable();
        rt1.addEntry("10.0.0.0/24", "10.0.0.1", 3);
        RoutingTable rt2 = new RoutingTable();
        rt2.addEntry("10.0.0.0/24", "10.0.0.1", 3);
        assertEquals(rt1, rt2);

        rt2.addEntry("10.0.0.0/24", "10.0.0.1", 4);
        RoutingTable rt3 = new RoutingTable();
        rt3.addEntry("10.0.0.0/24", "10.0.0.1", 4);
        assertEquals(rt2, rt3);
    }

    @org.junit.jupiter.api.Test
    void sendUpdate() {
        //initialize network
        Router r1 = new Router("R1");
        Router r2 = new Router("R2");
        r1.addLocalNet("10.0.0.0/24");
        r2.addLocalNet("10.0.0.0/24");
        r1.addLocalNet("192.168.0.0/24");
        r2.addLocalNet("192.168.10.0/24");

        //tests sending
        TreeMap<Subnet, Integer> update1 = r1.sendUpdate("10.0.0.2");
        TreeMap<Subnet, Integer> test1 = new TreeMap<>();
        test1.put(new Subnet("10.0.0.0/24"), 0);
        test1.put(new Subnet("192.168.0.0/24"), 0);
        assertEquals(update1, test1);

        //tests split horizon
        r1.addRoute("192.168.10.0/24", "10.0.0.2", 4);
        TreeMap<Subnet, Integer> update2 = r1.sendUpdate("10.0.0.2");
        assertEquals(update2, test1);
    }

    @org.junit.jupiter.api.Test
    void receiveUpdate() {
        //initialize network
        Router r1 = new Router("R1");
        Router r2 = new Router("R2");
        r1.addLocalNet("10.0.0.0/24");
        r2.addLocalNet("10.0.0.0/24");
        r1.addLocalNet("192.168.0.0/24");
        r2.addLocalNet("192.168.10.0/24");

        TreeMap<Subnet, Integer> update = r1.sendUpdate("10.0.0.2");
        r2.receiveUpdate(update, "10.0.0.1");

        RoutingTable rt = new RoutingTable();
        rt.addEntry("10.0.0.0/24", "127.0.0.1", 0);
        rt.addEntry("192.168.10.0/24", "127.0.0.1", 0);
        rt.addEntry("192.168.0.0/24", "10.0.0.1", 1);
        assertEquals(rt, r2.getRoutingTable());
    }

    @org.junit.jupiter.api.Test
    void testPoisonRoutesFromRouters() {
        //initialize network
        Router r1 = new Router("R1");
        Router r2 = new Router("R2");
        r1.addLocalNet("10.0.0.0/24");
        r1.addLocalNet("192.168.0.0/24");
        r2.addLocalNet("10.0.0.0/24");
        r2.addLocalNet("192.168.10.0/24");

        r1.receiveUpdate(r2.sendUpdate("10.0.0.1"), "10.0.0.2");
        r2.receiveUpdate(r1.sendUpdate("10.0.0.2"), "10.0.0.1");

        //tests if equal
        RoutingTable rt1 = new RoutingTable();
        rt1.addEntry("10.0.0.0/24", "127.0.0.1", 0);
        rt1.addEntry("192.168.0.0/24", "127.0.0.1", 0);
        rt1.addEntry("192.168.10.0/24", "10.0.0.2", 1);
        assertEquals(rt1, r1.getRoutingTable());
        RoutingTable rt2 = new RoutingTable();
        rt2.addEntry("10.0.0.0/24", "127.0.0.1", 0);
        rt2.addEntry("192.168.10.0/24", "127.0.0.1", 0);
        rt2.addEntry("192.168.0.0/24", "10.0.0.1", 1);
        assertEquals(rt2, r2.getRoutingTable());
        r1.poisonRoutesFromRouter("10.0.0.2");
        r2.poisonRoutesFromRouter("10.0.0.1");

        //tests poison
        RoutingTable rt3 = new RoutingTable();
        rt3.addEntry("10.0.0.0/24", "127.0.0.1", 0);
        rt3.addEntry("192.168.0.0/24", "127.0.0.1", 0);
        rt3.addEntry("192.168.10.0/24", "10.0.0.2", 16);
        assertEquals(rt3, r1.getRoutingTable());
        RoutingTable rt4 = new RoutingTable();
        rt4.addEntry("10.0.0.0/24", "127.0.0.1", 0);
        rt4.addEntry("192.168.10.0/24", "127.0.0.1", 0);
        rt4.addEntry("192.168.0.0/24", "10.0.0.1", 16);
        assertEquals(rt4, r2.getRoutingTable());
    }

    @org.junit.jupiter.api.Test
    void testPoisonRoute() {
        //initialize network
        Router r1 = new Router("R1");
        Router r2 = new Router("R2");
        r1.addLocalNet("10.0.0.0/24");
        r1.addLocalNet("192.168.0.0/24");
        r2.addLocalNet("10.0.0.0/24");
        r2.addLocalNet("192.168.10.0/24");

        r1.receiveUpdate(r2.sendUpdate("10.0.0.1"), "10.0.0.2");
        r2.receiveUpdate(r1.sendUpdate("10.0.0.2"), "10.0.0.1");

        r1.poisonRoute("192.168.0.0/24");
        r2.receiveUpdate(r1.sendUpdate("10.0.0.2"), "10.0.0.1");

        RoutingTable rt1 = new RoutingTable();
        rt1.addEntry("10.0.0.0/24", "127.0.0.1", 0);
        rt1.addEntry("192.168.0.0/24", "127.0.0.1", 16);
        rt1.addEntry("192.168.10.0/24", "10.0.0.2", 1);
        assertEquals(rt1, r1.getRoutingTable());
        RoutingTable rt2 = new RoutingTable();
        rt2.addEntry("10.0.0.0/24", "127.0.0.1", 0);
        rt2.addEntry("192.168.10.0/24", "127.0.0.1", 0);
        rt2.addEntry("192.168.0.0/24", "10.0.0.1", 16);
        assertEquals(rt2, r2.getRoutingTable());
    }

    @org.junit.jupiter.api.Test
    void testFourRouterNetwork() {
        //initialize routers
        Router r1 = new Router("R1");
        Router r2 = new Router("R2");
        Router r3 = new Router("R3");
        Router r4 = new Router("R4");

        //initialize local nets from router 1
        r1.addLocalNet("10.0.1.0/24");
        r1.addLocalNet("10.0.4.0/24");
        r1.addLocalNet("192.168.1.0/24");

        //initialize local nets from router 2
        r2.addLocalNet("10.0.1.0/24");
        r2.addLocalNet("10.0.2.0/24");
        r2.addLocalNet("192.168.2.0/24");

        //initialize local nets from router 3
        r3.addLocalNet("10.0.2.0/24");
        r3.addLocalNet("10.0.3.0/24");
        r3.addLocalNet("192.168.3.0/24");

        //initialize local nets from router 4
        r4.addLocalNet("10.0.3.0/24");
        r4.addLocalNet("10.0.4.0/24");
        r4.addLocalNet("192.168.4.0/24");

        //learn routes
        r1.receiveUpdate(r2.sendUpdate("10.0.1.1"), "10.0.1.2");
        r2.receiveUpdate(r1.sendUpdate("10.0.1.2"), "10.0.1.1");

        r2.receiveUpdate(r3.sendUpdate("10.0.2.1"), "10.0.2.2");
        r3.receiveUpdate(r2.sendUpdate("10.0.2.2"), "10.0.2.1");

        r3.receiveUpdate(r4.sendUpdate("10.0.3.1"), "10.0.3.2");
        r4.receiveUpdate(r3.sendUpdate("10.0.3.2"), "10.0.3.1");

        r4.receiveUpdate(r1.sendUpdate("10.0.4.1"), "10.0.4.2");
        r1.receiveUpdate(r4.sendUpdate("10.0.4.2"), "10.0.4.1");

        r1.receiveUpdate(r2.sendUpdate("10.0.1.1"), "10.0.1.2");
        r2.receiveUpdate(r1.sendUpdate("10.0.1.2"), "10.0.1.1");

        r2.receiveUpdate(r3.sendUpdate("10.0.2.1"), "10.0.2.2");
        r3.receiveUpdate(r2.sendUpdate("10.0.2.2"), "10.0.2.1");

        r3.receiveUpdate(r4.sendUpdate("10.0.3.1"), "10.0.3.2");
        r4.receiveUpdate(r3.sendUpdate("10.0.3.2"), "10.0.3.1");

        r4.receiveUpdate(r1.sendUpdate("10.0.4.1"), "10.0.4.2");
        r1.receiveUpdate(r4.sendUpdate("10.0.4.2"), "10.0.4.1");

        //test tables from router 1
        RoutingTable rt1 = new RoutingTable();
        rt1.addEntry("192.168.1.0/24", "127.0.0.1", 0);
        rt1.addEntry("192.168.2.0/24", "10.0.1.2", 1);
        rt1.addEntry("192.168.3.0/24", "10.0.4.1", 2);
        rt1.addEntry("192.168.4.0/24", "10.0.4.1", 1);
        rt1.addEntry("10.0.1.0/24", "127.0.0.1", 0);
        rt1.addEntry("10.0.2.0/24", "10.0.1.2", 1);
        rt1.addEntry("10.0.3.0/24", "10.0.4.1", 1);
        rt1.addEntry("10.0.4.0/24", "127.0.0.1", 0);
        assertEquals(rt1, r1.getRoutingTable());

        //test tables from router 2
        RoutingTable rt2 = new RoutingTable();
        rt2.addEntry("192.168.1.0/24", "10.0.1.1", 1);
        rt2.addEntry("192.168.2.0/24", "127.0.0.1", 0);
        rt2.addEntry("192.168.3.0/24", "10.0.2.2", 1);
        rt2.addEntry("192.168.4.0/24", "10.0.1.1", 2);
        rt2.addEntry("10.0.1.0/24", "127.0.0.1", 0);
        rt2.addEntry("10.0.2.0/24", "127.0.0.1", 0);
        rt2.addEntry("10.0.3.0/24", "10.0.2.2", 1);
        rt2.addEntry("10.0.4.0/24", "10.0.1.1", 1);
        assertEquals(rt2, r2.getRoutingTable());

        //test tables from router 3
        RoutingTable rt3 = new RoutingTable();
        rt3.addEntry("192.168.1.0/24", "10.0.2.1", 2);
        rt3.addEntry("192.168.2.0/24", "10.0.2.1", 1);
        rt3.addEntry("192.168.3.0/24", "127.0.0.1", 0);
        rt3.addEntry("192.168.4.0/24", "10.0.3.2", 1);
        rt3.addEntry("10.0.1.0/24", "10.0.2.1", 1);
        rt3.addEntry("10.0.2.0/24", "127.0.0.1", 0);
        rt3.addEntry("10.0.3.0/24", "127.0.0.1", 0);
        rt3.addEntry("10.0.4.0/24", "10.0.3.2", 1);
        assertEquals(rt3, r3.getRoutingTable());

        //test tables from router 4
        RoutingTable rt4 = new RoutingTable();
        rt4.addEntry("192.168.1.0/24", "10.0.4.2", 1);
        rt4.addEntry("192.168.2.0/24", "10.0.3.1", 2);
        rt4.addEntry("192.168.3.0/24", "10.0.3.1", 1);
        rt4.addEntry("192.168.4.0/24", "127.0.0.1", 0);
        rt4.addEntry("10.0.1.0/24", "10.0.4.2", 1);
        rt4.addEntry("10.0.2.0/24", "10.0.3.1", 1);
        rt4.addEntry("10.0.3.0/24", "127.0.0.1", 0);
        rt4.addEntry("10.0.4.0/24", "127.0.0.1", 0);
        assertEquals(rt4, r4.getRoutingTable());
    }
}