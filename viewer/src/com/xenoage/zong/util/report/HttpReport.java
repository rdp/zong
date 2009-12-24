package com.xenoage.zong.util.report;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.xenoage.util.StreamTools;
import com.xenoage.util.lang.Tuple2;


/**
 * This class sends an report by HTTP POST
 * to a given address. The report consists of a list
 * of files (filename and data block) that have to
 * be registered before.
 * 
 * When everything went ok, this class expects the
 * server to answer with "OK" (must be the last two
 * characters of the response). Otherwise, an error
 * has occurred.
 * 
 * The user gets notified with an {@link IOException}
 * when sending the report has failed.
 * 
 * @author Andreas Wenger
 */
public class HttpReport
{
	
	private static final String SERVER_HOST = "www.xenoage.com";
	private static final int SERVER_PORT = 80;
	private static final String SERVER_PAGE = "/zong/report/report.php";
	
	private ArrayList<Tuple2<String, byte[]>> files = new ArrayList<Tuple2<String, byte[]>>();
	
	
	/**
	 * Registers another data block to be sent.
	 */
	public void registerData(String filename, String data)
	{
		if (filename.length() == 0)
			throw new IllegalArgumentException("filename may not be empty");
		files.add(new Tuple2<String, byte[]>(filename, data.getBytes()));
	}
	
	
	/**
	 * Registers another data block to be sent.
	 */
	public void registerData(String filename, InputStream in)
		throws IOException
	{
		if (filename.length() == 0)
			throw new IllegalArgumentException("filename may not be empty");
		byte[] data = StreamTools.readInputStream(in);
		files.add(new Tuple2<String, byte[]>(filename, data));
	}
	
	
	/**
	 * Sends the report.
	 */
	public void send()
		throws IOException
	{
		if (files.size() == 0)
		{
			throw new IOException("register at least one data block before sending");
		}
		
		//total length of data
		int totalDataLength = 0;
		for (Tuple2<String, byte[]> b : files)
		{
			totalDataLength += b.get2().length;
		}
		
		//commands for filenames
		ArrayList<String> dataCommands = new ArrayList<String>();
		int totalDataCommandLength = 0;
		int fileIndex = 0;
		for (Tuple2<String, byte[]> b : files)
		{
			String dataCommand = getDataCommand(b.get1(), fileIndex++);
			dataCommands.add(dataCommand);
			totalDataCommandLength += dataCommand.length() + "\r\n".getBytes().length;
		}
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), 5000); //5 sec timeout

		DataOutputStream raw = new DataOutputStream(socket.getOutputStream());
		OutputStreamWriter wr = new OutputStreamWriter(raw);

		String trail = "--delimiter--\r\n";

		String header =
		 "POST " + SERVER_PAGE + " HTTP/1.0\r\n"
		 + "Accept: */*\r\n"
		 + "Referer: http://localhost\r\n"
		 + "Accept-Language: de\r\n"
		 + "Content-Type: multipart/form-data; boundary=delimiter\r\n"
		 + "User_Agent: TESTAGENT\r\n"
		 + "Host: www.zong-music.com\r\n"
		 + "Content-Length: " + (totalDataLength + totalDataCommandLength + trail.length()) + "\r\n"
		 //+ "Connection: Keep-Alive\r\n"
		 + "Pragma: no-cache\r\n"
		 + "\r\n";
		
		wr.write(header);
		for (int i = 0; i < files.size(); i++)
		{
			wr.write(dataCommands.get(i));
			wr.flush();
			raw.write(files.get(i).get2());
			raw.flush();
			wr.write("\r\n");
			wr.flush();
		}
		wr.write(trail);
		wr.flush();
    
    BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  	String text = "", line;
  	while ((line = rd.readLine()) != null)
  	{
  		text += line; // + "\n";
  	}
  	wr.close();
  	raw.close();

  	socket.close();
		
  	if (!text.endsWith("OK"))
  	{
  		throw new IOException("Failed. Server answers: " + text);
  	}
	}
	
	
	private String getDataCommand(String filename, int index)
	{
    return "--delimiter\r\n"
	    + "Content-Disposition: form-data; name=\"file" + index + "\"; filename=\""
	    + filename + "\"\r\n"
	    + "\r\n";
	}
	
}