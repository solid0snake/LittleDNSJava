import java.io.*;
import java.net.*;
import java.util.*;

//  Author:   Pedro Garate
//  Date  :   1/28/13
//  Objective: Receives ipAddress or hostname from user/client
//             if hostname is received ipaddresses are returned
//             if ipaddress is received hostname is returned
//***************************************************************

//***********LittleDNS class
public class LittleDNS
{
	//************************* main ****************************
	public static void main (String args[])
	{
		final int port = 16184;
		try (ServerSocket ss = new ServerSocket(port))
		{
			while(true)
			{
				Socket s = ss.accept();
				Runnable r = new DNSHandler(s);
				new Thread(r).start();
			}
		}catch(IOException e) {e.printStackTrace();}
	}
}

//***********DNSHandler class
class DNSHandler implements Runnable
{
	//************** transform************************************
	public void transform(String s) throws UnknownHostException
	{
		if(s.trim().equalsIgnoreCase("EXIT")) return;
		
		String [] tokens = s.split("\\.");
		
		if (tokens.length == 4)
				getName(s);
		else getIp(s);
	}
	
	//******************* getIP **********************************
	public static void getIp(String s) throws UnknownHostException
	{
		InetAddress [] aia = InetAddress.getAllByName(s);
		for (int i = 0; i < aia.length; i++)
		{
			System.out.print(aia[i].getHostAddress()+"\n");
		}
	}
	
	//******************** getName *********************************
	public static void getName(String s) throws UnknownHostException
	{
		InetAddress ai = InetAddress.getByName(s);
		
		System.out.print(ai.getHostName()+"\n");
		
	}
	
	private Socket s;
	
	public DNSHandler(Socket s1) {s=s1;}
	
	//*********************** run ********************************
	public void run()
	{
		try
		{
			try
			{
				InputStream is = s.getInputStream();
				OutputStream os = s.getOutputStream();
				Scanner sc = new Scanner(is);
				PrintWriter pw = new PrintWriter(os, true);
				pw.println("Enter EXIT to exit.");
				boolean done = false;
				while(!done && sc.hasNextLine())
				{
					String line = sc.nextLine();
					pw.print(" > " );
					transform(line);
					if(line.trim().equalsIgnoreCase("EXIT")) done = true;
				}
				sc.close();
			}finally {s.close();}
		}catch(IOException e) {e.printStackTrace();}
	}
}
