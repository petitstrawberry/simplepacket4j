package dev.ichigo.simplepacket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class FrameTest {

    @Test
    public void testDecode() throws FrameDecoderException, PacketDecoderException {
        byte[] data = {0x01, 0x03, 0x00, 0x01, 0x02, 0x03, 0x02, 0x02, 0x00, 0x01, 0x02, 0x00, 0x00, 0x00};
        Frame frame = Frame.decode(data);

        List<Packet> packets = frame.getPackets();
        assertEquals(2, packets.size());
        assertEquals(0x01, packets.get(0).getType());
        assertEquals(0x03, packets.get(0).getLength());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03}, packets.get(0).getPayload());
        assertEquals(0x02, packets.get(1).getType());
        assertEquals(0x02, packets.get(1).getLength());
        assertArrayEquals(new byte[]{0x01, 0x02}, packets.get(1).getPayload());
    }

    @Test
    public void testEncode() throws PacketDecoderException {
        List<Packet> packets = Arrays.asList(
            new Packet((byte) 0x01, new byte[]{0x01, 0x02, 0x03}),
            new Packet((byte) 0x02, new byte[]{0x01, 0x02})
        );
        Frame frame = new Frame(packets);
        byte[] data = frame.encode();

        byte[] expectedData = {0x01, 0x03, 0x00, 0x01, 0x02, 0x03, 0x02, 0x02, 0x00, 0x01, 0x02, 0x00, 0x00, 0x00};
        assertArrayEquals(expectedData, data);
    }

    @Test
    public void testEncodeDecode() throws FrameDecoderException, PacketDecoderException {
        List<Packet> packets = Arrays.asList(
                new Packet((byte) 0x01, new byte[] { 0x01, 0x02, 0x03 }),
                new Packet((byte) 0x02, new byte[] { 0x01, 0x02 }));
        Frame frame = new Frame(packets);
        byte[] data = frame.encode();
        Frame decodedFrame = Frame.decode(data);
        assertEquals(frame, decodedFrame);
    }

    @Test
    public void testInvalidFrame() {
        byte[] data = {0x01, 0x03};
        assertThrows(FrameDecoderException.class, () -> {
            Frame.decode(data);
        });
    }

    @Test
    public void testInvalidPacket() {
        byte[] data = {0x01, 0x03, 0x00, 0x01, 0x02, 0x02, 0x02, 0x00, 0x01, 0x02};
        assertThrows(PacketDecoderException.class, () -> {
            Frame.decode(data);
        });
    }

    @Test
    public void testInvalidPayload() {
        byte[] data = {0x01, 0x03, 0x00, 0x01, 0x02, 0x03, 0x02, 0x02, 0x00, 0x01};
        assertThrows(PacketDecoderException.class, () -> {
            Frame.decode(data);
        });
    }
}
