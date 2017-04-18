import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.zip.CRC32;
import java.nio.ByteBuffer;

public final class Ex2Client {

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("codebank.xyz", 38102)) {
			System.out.println("Connected to server.");
			Scanner kb = new Scanner(System.in);
			OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os, true, "UTF-8");
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			
			
			byte[] message = new byte[100];
			int byte1, byte2;
			System.out.print("Received Bytes:");
			for(int i = 0; i < 100; i++) {
				byte1 = br.read();
				byte1 *= 16;
				byte2 = br.read();
				byte1 = byte1 + byte2;
				message[i] = (byte)byte1;
				if(i%10 == 0) {
					System.out.print("\n   ");
				}
				if(byte1 < 16) {
					System.out.print(0);
				}
				System.out.print(Integer.toHexString(byte1));
			}
			CRC32 errorCheck = new CRC32();
			errorCheck.update(message);
			String errorCodeString = Long.toHexString(errorCheck.getValue());
			System.out.println();
			System.out.println("Generated CRC32: " + errorCodeString + ".");
			byte[] errorCode = ByteBuffer.allocate(4).putInt((int)errorCheck.getValue()).array();
			os.write(errorCode);
			int answer = br.read();
			if(answer == 1) {
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad.");
			}
        }
		System.out.println("Disconnected from server.");
    }
}