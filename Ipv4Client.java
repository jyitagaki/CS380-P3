//Joshua Itagaki
//CS 380

import java.io.*;
import java.net.Socket;

public class Ipv4Client {
	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket("codebank.xyz", 38003)){
			OutputStream out = socket.getOutputStream();
			BufferedReader buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			for(int i = 1; i <= 12; i++){
				int dataL = (int)(Math.pow(2, i));
				int length = (int) (Math.pow(2, i) + 20);
				byte[] packet = new byte[length];
				packet[0] = (byte)0x45;	//Version & HLen
				packet[1] = 0;	//TOS not implemented
				packet[2] = (byte)(length >> 8);	//length first byte
				packet[3] = (byte)(length);	//length second byte
				packet[4] = 0;	//ident not implemented
				packet[5] = 0;	//ident not implemented
				packet[6] = (byte)0x40;	//Flags
				packet[7] = 0;	//offset
				packet[8] = 50;	//TTL assume every packet is 50
				packet[9] = 6; //protocol assume every packet TCP
				
				for(int j = 12; i <= 15; i++){	//packets 12-15
					packet[j] = 0;	//source address is all 0's
				}
				
				byte[] destination = socket.getInetAddress().getAddress();
				
				for(int j = 0; j <= 3; j++){	//packets 16-19
					packet[j + 16] = destination[j];	//destination address of server
				}
				short csValue = checksum(packet, length);	//calculate checksum
				packet[10] = (byte)(csValue >> 8);	//first byte of checksum
				packet[11] = (byte)csValue;	//second byte of checksum
				System.out.println("Data length: " + dataL);
				out.write(packet);
				String message = buff.readLine();
				System.out.println(message);
			}
		}
	}
	public static short checksum(byte[] array, int count){
		short sumU = 0;
		int index = 0;
		while(count-- != 0){
			sumU += array[index++];
			if((sumU & 0xFFFF0000) != 0){
				sumU &= 0xFFFF;
				sumU++;
			}
		}
		return (short) (sumU & 0xFFFF);
	}
}
