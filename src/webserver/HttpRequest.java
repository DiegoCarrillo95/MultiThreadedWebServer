package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class HttpRequest implements Runnable {

	final static String CRLF = "\r\n";
	Socket socket;

	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {

		// Get a reference to the socket's input and output streams.
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		// Set up input stream filters.
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// Get the request line of the HTTP request message.
		String requestLine = br.readLine();
		// Display the request line.
		System.out.println();
		System.out.println(requestLine);

		// Get and display the header lines.
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}

		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();
		// Prepend a "." so that file request is within the current directory.
		fileName = "." + fileName;

		returnFile(fileName, os);

		// Close streams and socket.
		System.out.println("Closing connection");
		os.close();
		br.close();
		socket.close();
	}

	private void returnFile(String fileName, DataOutputStream os) throws Exception {
		// Open the requested file.
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			 contentTypeLine = "Content-type: " +
			 contentType( fileName ) + CRLF;
		} else {
			statusLine = "HTTP/1.0 404 NOT FOUND" + CRLF;
			contentTypeLine = "Content-type: text/html" + CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
		}

		// Send the status line.
		os.writeBytes(statusLine);
		// Send the content type line.
		os.writeBytes(contentTypeLine);
		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);
		// Send the entity body.
		if (fileExists) {
			 sendBytes(fis, os);
			 fis.close();
		} else {
			os.writeBytes(entityBody);
		}

	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;
		// Copy requested file into the socket's output stream.
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	private static String contentType(String fileName) {
		// HTML
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		// CSS
		else if (fileName.endsWith(".css")) {
			return "text/css";
		}
		// GIF
		else if (fileName.endsWith(".gif")) {
			return "image/gif";
		}
		// JS
		else if (fileName.endsWith(".js")) {
			return "application/javascript";
		}
		// JPG/JPEG
		else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		// MID/MIDI
		else if (fileName.endsWith(".mid") || fileName.endsWith(".midi")) {
			return "audio/midi";
		}
		// MPEG
		else if (fileName.endsWith(".mpeg")) {
			return "video/mpeg";
		}
		// PNG
		else if (fileName.endsWith(".png")) {
			return "image/png";
		}
		// ICO
		else if (fileName.endsWith(".ico")) {
			return "image/x-icon";
		}
		// XML
		else if (fileName.endsWith(".xml")) {
			return "application/xml";
		}
		// JSON
		else if (fileName.endsWith(".json")) {
			return "application/json";
		}
		// ZIP
		else if (fileName.endsWith(".zip")) {
			return "application/zip";
		}
		// SVG
		else if (fileName.endsWith(".svg")) {
			return "image/svg+xml";
		}
		// FONTS (.otf, .ttf, .woff, .woff2, .eot)
		else if (fileName.endsWith(".otf")) {
			//return "font/otf";
			return "application/font-sfnt";
		}
		else if (fileName.endsWith(".ttf")) {
			//return "font/ttf";
			return "application/font-sfnt";
		}
		else if (fileName.endsWith(".woff")) {
			//return "font/woff";
			//return "application/font-woff";
			return "application/x-font-woff";
		}
		else if (fileName.endsWith(".woff2")) {
			//return "font/woff2";
			return "application/font-woff2";
		}
		else if (fileName.endsWith(".eot")) {
			return "application/vnd.ms-fontobject";
		}

		// Arquivo binário genérico
		return "application/octet-stream";
	}

}
