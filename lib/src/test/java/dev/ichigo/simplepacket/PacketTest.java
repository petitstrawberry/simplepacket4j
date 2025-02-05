package dev.ichigo.simplepacket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTest {

    @Test
    public void testPacketEncoding() {
        byte type = 1;
        byte[] payload = {10, 20, 30, 40};
        Packet packet = new Packet(type, payload);

        byte[] encoded = packet.encode();
        ByteBuffer buffer = ByteBuffer.wrap(encoded);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        assertEquals(type, buffer.get());
        assertEquals(payload.length, Short.toUnsignedInt(buffer.getShort()));
        byte[] decodedPayload = new byte[payload.length];
        buffer.get(decodedPayload);
        assertArrayEquals(payload, decodedPayload);
    }

    @Test
    public void testPacketDecoding() throws PacketDecoderException {
        byte type = 1;
        byte[] payload = { 10, 20, 30, 40 };
        ByteBuffer buffer = ByteBuffer.allocate(1 + 2 + payload.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(type);
        buffer.putShort((short) payload.length);
        buffer.put(payload);
        buffer.flip();

        Packet packet = Packet.decode(buffer);

        assertEquals(type, packet.getType());
        assertEquals(payload.length, packet.getLength());
        assertArrayEquals(payload, packet.getPayload());
    }

    @Test
    public void testPacketEncodingDecoding() throws PacketDecoderException {
        byte type = 1;
        byte[] payload = { 10, 20, 30, 40 };
        Packet packet = new Packet(type, payload);

        byte[] encoded = packet.encode();
        ByteBuffer buffer = ByteBuffer.wrap(encoded);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        Packet decodedPacket = Packet.decode(buffer);

        assertEquals(packet, decodedPacket);
    }

    @Test
    public void testEOFPacket() {
        Packet eofPacket = Packet.EOF();

        assertEquals(0, eofPacket.getType());
        assertEquals(0, eofPacket.getLength());
        assertArrayEquals(new byte[0], eofPacket.getPayload());
    }
}