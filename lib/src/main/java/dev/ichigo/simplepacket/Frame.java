package dev.ichigo.simplepacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Frame {
    private final List<Packet> packets;

    public Frame(List<Packet> packets) {
        this.packets = new ArrayList<>(packets);
    }

    public List<Packet> getPackets() {
        return packets;
    }

    /**
     * Decodes a frame from the given byte array.
     * A frame is a sequence of Packet objects terminated by an EOF packet (type 0 with an empty payload).
     * @param data the byte array representing the encoded frame.
     * @return the decoded Frame object.
     * @throws FrameDecoderException if the frame is invalid.
     * @throws PacketDecoderException if there is an error decoding a packet.
     */
    public static Frame decode(byte[] data) throws FrameDecoderException, PacketDecoderException {
        if (data.length < 3) {
            throw new FrameDecoderException("Invalid frame: data length is less than 2 bytes");
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        List<Packet> packets = new ArrayList<>();
        while (buffer.hasRemaining()) {
            Packet packet = Packet.decode(buffer);
            // Stop processing when an EOF packet is encountered.
            if (packet.equals(Packet.EOF())) {
                break;
            }
            packets.add(packet);
        }
        return new Frame(packets);
    }

    /**
     * Encodes the frame into a byte array.
     * It encodes all packets in the frame and appends an EOF packet at the end.
     * @return the encoded byte array of the frame.
     */
    public byte[] encode() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            for (Packet packet : packets) {
                output.write(packet.encode());
            }
            // Append an EOF packet to mark the end of the frame.
            output.write(Packet.EOF().encode());
        } catch (Exception e) {
            // In production, proper exception handling should be applied.
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    @Override
    public boolean equals(Object obj) {
        // Check packets array equality.
        if (obj instanceof Frame) {
            Frame other = (Frame) obj;
            return packets.equals(other.packets);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return packets.hashCode();
    }
}
