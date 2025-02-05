package dev.ichigo.simplepacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Packet {
    private final byte type;
    private final int length; // Corresponds to Swift's UInt16 (range 0-65535)
    private final byte[] payload;

    public Packet(byte type, byte[] payload) {
        this.type = type;
        this.payload = payload;
        this.length = payload.length;
    }

    public byte getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public byte[] getPayload() {
        return payload;
    }

    /**
     * Encodes the packet into a byte array.
     * The structure is:
     * - 1 byte for the type,
     * - 2 bytes for the length (little-endian),
     * - N bytes for the payload.
     * @return the encoded byte array.
     */
    public byte[] encode() {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 2 + payload.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(type);
        buffer.putShort((short) payload.length);
        buffer.put(payload);
        return buffer.array();
    }

    /**
     * Decodes a packet from the provided ByteBuffer.
     * The ByteBuffer should have its order set to little-endian.
     * @param buffer the ByteBuffer from which to decode the packet.
     * @return the decoded Packet object.
     * @throws PacketDecoderException if the packet header or payload is invalid.
     */
    public static Packet decode(ByteBuffer buffer) throws PacketDecoderException {
        if (buffer.remaining() < 3) {
            throw new PacketDecoderException("Invalid packet: not enough bytes for header");
        }
        byte type = buffer.get();
        int length = Short.toUnsignedInt(buffer.getShort());
        if (buffer.remaining() < length) {
            throw new PacketDecoderException("Invalid payload: not enough bytes for payload");
        }
        byte[] payload = new byte[length];
        buffer.get(payload);
        return new Packet(type, payload);
    }

    /**
     * Returns an EOF packet, defined as a packet with type 0 and an empty payload.
     * @return an EOF Packet.
     */
    public static Packet EOF() {
        return new Packet((byte) 0, new byte[0]);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Packet other = (Packet) obj;
        return type == other.type && length == other.length && Arrays.equals(payload, other.payload);
    }

    @Override
    public int hashCode() {
        int result = Byte.hashCode(type);
        result = 31 * result + Integer.hashCode(length);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
