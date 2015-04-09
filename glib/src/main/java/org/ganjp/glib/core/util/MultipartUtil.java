/**
 * MultipartUtil.java
 * 
 * Created by Gan Jianping on 20/07/13.
 * Copyright (c) 2013 GANGJIANPING. All rights reserved.
 */
package org.ganjp.glib.core.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.ganjp.glib.core.entity.Response;

import android.util.Log;


/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server. 
 * @author www.codejava.net
 *
 */
public class MultipartUtil {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @param isIgnoreSSL
     * @throws IOException
     */
    public MultipartUtil(String requestURL, String charset, boolean isIgnoreSSL) throws IOException {
        this.charset = charset;
         
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
        Log.i("MultipartUtil", boundary);
        try {
	        URL url = new URL(requestURL);
	        httpConn = (HttpURLConnection) url.openConnection();
	        if (isIgnoreSSL) {
	        	TrustModifier.relaxHostChecking(httpConn);
	        }
	        httpConn.setDoOutput(true); // indicates POST method
	        httpConn.setDoInput(true);
	        
	        httpConn.setUseCaches(false); // Don't use a Cached Copy
	        httpConn.setRequestMethod("POST");
	        httpConn.setRequestProperty("Connection", "Keep-Alive");
	        httpConn.setRequestProperty("ENCTYPE", "multipart/form-data");
	        
	        httpConn.setChunkedStreamingMode(0);
	        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
	   
	        outputStream = new BufferedOutputStream(httpConn.getOutputStream());
	        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
	        
	        Log.d("MultipartUtil", "Create a new MultipartUtil Successful");
        } catch(Exception ex) {
        	Log.e("Creste MultipartUtil", ex.getMessage());
        }
    }
 
    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value);
        writer.flush();
        Log.d("DbsUtil", "Add " + name + "=" + value + "successfully");
    }
 
    /**
     * <p>Adds a upload file section to the request </p>
     * 
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded 
     * @throws java.io.IOException
     */
    public void addFilePart(String fieldName, File uploadFile) {
    	try {
    		Log.d("MultipartUtil", "Start add a file Part : " + uploadFile.getAbsolutePath());
	        String fileName = uploadFile.getName();
	        writer.append("--" + boundary).append(LINE_FEED);
	        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
	        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
	        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
	        writer.append(LINE_FEED);
	        writer.flush();
 
	        FileInputStream inputStream = new FileInputStream(uploadFile);
	        byte[] buffer = new byte[4096];//4096
	        int bytesRead = -1;
	        int i=1;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            outputStream.write(buffer, 0, bytesRead);
	            Log.d("MultipartUtil", "write " + String.valueOf(i++));
	        }
	        outputStream.flush();
	        inputStream.close();
        
	        writer.append(LINE_FEED);
	        writer.flush();
	        Log.d("DbsUtil", "Add a file successfully");
    	} catch(Exception ex) {
    		Log.e("MultipartUtil", "Add a file error : " + ex.getMessage());
    	}
    }
 
    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws java.io.IOException
     */
    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();
 
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
 
        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
 
        return response;
    }
    
    /**
     * <p>Completes the request and receives response from the server.</p>
     * 
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws java.io.IOException
     */
    public Response getResponse() throws IOException {
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
 
        // checks server's status code first
        int status;
        try {
        	status = httpConn.getResponseCode();
        } catch (Exception ex) {
        	status = httpConn.getResponseCode();
        }
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				if (StringUtil.isNotEmpty(line)) {
					builder.append(line);
				}
			}
            reader.close();
            httpConn.disconnect();
            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(builder.toString(), Response.class);
            Log.d("MultipartUtil", "Get Response : " + response.toString());
            return response;
        } else {
            return null;
        }
    }
    
    public static Response submitFileAndGetResponse(String requestURL, File uploadFile, String fileType, String uuid, boolean isIgnoreSSL) {
		try {
			requestURL += "/" + fileType;
	        MultipartUtil multipart = new MultipartUtil(requestURL, "UTF-8", isIgnoreSSL);
	        multipart.addFormField("uuid", uuid);
	        multipart.addFilePart("file", uploadFile);
	        return multipart.getResponse();
	    } catch (IOException ex) {
	        System.out.println("ERROR: " + ex.getMessage());
	        ex.printStackTrace();
	    }
		return null;
	}
    
    public static Response submitPersonalInfoAndGetResponse(String requestURL, String description, boolean isIgnoreSSL) {
 		try {
 			MultipartUtil multipart = new MultipartUtil(requestURL, "UTF-8", isIgnoreSSL);
 	        multipart.addFormField("description", description);
 	        return multipart.getResponse();
 	    } catch (IOException ex) {
 	        System.out.println("ERROR: " + ex.getMessage());
 	        ex.printStackTrace();
 	    }
 		return null;
 	}
    
	public static Response submitFile(String requestURL, File uploadFile, String fileType, String uuid, boolean isIgnoreSSL) {
		try {
			requestURL += "/" + fileType;
	        MultipartUtil multipart = new MultipartUtil(requestURL, "UTF-8",isIgnoreSSL);
	        multipart.addFormField("uuid", uuid);
	        multipart.addFilePart("file", uploadFile);
	        return multipart.getResponse();
	    } catch (IOException ex) {
	        System.out.println("ERROR: " + ex.getMessage());
	        ex.printStackTrace();
	    }
		return null;
	}
	
	/**
	 * <p>Upload Files</p>
	 * 
	 * @param fileFullPath
	 * @param serverUrl
	 * @return
	 */
	public static Response uploadFile(String serverUrl, String fileFullPath, String fieldName, String value, boolean isIgnoreSSL) {
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		int serverResponseCode = 0;
		
		File sourceFile = new File(fileFullPath); 
		if (!sourceFile.isFile()) { 
			Log.d("MultipartUtil", "Upload the File not exist :" + fileFullPath);
		} else {
			try { 
				Log.d("MultipartUtil", "Start upload the file :" + fileFullPath + " to " + serverUrl);
				String lineEnd = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";
				int bytesRead, bytesAvailable, bufferSize;
				byte[] buffer;
				int maxBufferSize = 1 * 32 * 1024;
				
				// open a URL connection to the Servlet
				fis = new FileInputStream(sourceFile);
				URL url = new URL(serverUrl);
				
				// Open a HTTP  connection to  the URL
				conn = (HttpURLConnection) url.openConnection(); 
				if (isIgnoreSSL) {
					TrustModifier.relaxHostChecking(conn);
				}
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");//Enable POST method
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				
				// Create output Stream to send data to server
				dos = new DataOutputStream(conn.getOutputStream());
				
				// Add a field
				dos.writeBytes(twoHyphens + boundary + lineEnd); 
	            dos.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\" "+ lineEnd + lineEnd);
	            dos.writeBytes(value + lineEnd);
	            dos.writeBytes(twoHyphens + boundary + lineEnd); 
	            Log.d("MultipartUtil", "add " + fieldName + "=" + value + " successfully.");
	            
				dos.writeBytes(twoHyphens + boundary + lineEnd); 
				dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\"" + sourceFile.getName()  +"\"" + lineEnd);
				dos.writeBytes(lineEnd);
				// create a buffer of  maximum size
				bytesAvailable = fis.available(); 
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				// read file and write it into form...
				Log.d("uploadFile", "Read file and write it into form.");
				bytesRead = fis.read(buffer, 0, bufferSize);
				int i=1;
				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fis.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fis.read(buffer, 0, bufferSize);
					Log.d("uploadFile", "Wite file :" + String.valueOf(i++)); 
				}
				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				Log.d("MultipartUtil", "write the file successfully.");
				
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();
				Log.d("MultipartUtil", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
				if(serverResponseCode == 200){
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		            StringBuilder builder = new StringBuilder();
					for (String line = null; (line = reader.readLine()) != null;) {
						if (StringUtil.isNotEmpty(line)) {
							builder.append(line);
						}
					}
		            reader.close();
		            conn.disconnect();
		            ObjectMapper mapper = new ObjectMapper();
		            Response response = mapper.readValue(builder.toString(), Response.class);
		            Log.d("MultipartUtil", "Response : " + response);
		            return response;
				}
				//close the streams
				fis.close();
				dos.flush();
				dos.close();
			} catch (MalformedURLException ex) {
				Log.e("MultipartUtil", "Upload file to server error : " + ex.getMessage(), ex);
			} catch (Exception e) {
				Log.e("MultipartUtil", "Upload file to server Exception : " + e.getMessage(), e);
			} finally {
				try {
					if (fis!=null) {
						fis.close();
					}
					if (dos!=null) {
						dos.close();
					}
				} catch (Exception ex) {
					Log.e("Close the streams", "error: " + ex.getMessage(), ex);
				}
			}
		}
		return null;
	}
	
	public static final String FILE_TYPE_NAME_CARD_IMAGE = "NameCardImage";
	public static final String FILE_TYPE_IDENTITY_CARD_IMAGE = "IndentityCardImage";
	public static final String FILE_TYPE_BILLING_IMAGE = "BillingImage";
	
	public static final String SUBMIT_FILE_URL = "http://42.61.85.141:8081/dbs/services/uploadFile";
	public static final String SUBMIT_RESULT_URL = "http://42.61.85.141:8081/dbs/services/uploadFilesAndInfo";
	public static final String SUBMIT_PERSONAL_INFO_URL = "http://42.61.85.141:8081/dbs/services/submitPersonalInfo";
	
	public static void main(String[] args) {
//        File uploadFile1 = new File("C:/Ganjp/Workspace/dbs/resources/abbyy/businesscard/englishNameCard.jpg");
//        File uploadFile2 = new File("C:/Ganjp/Workspace/dbs/resources/abbyy/passportidcardvisa/usaPassport.jpg");
//        File uploadFile3 = new File("C:/Ganjp/Workspace/dbs/resources/abbyy/document/chineseTraditionalText.png");
//        String uuid = "";
//        uuid = submitFileAndGetUuid(SUBMIT_FILE_URL, uploadFile1, FILE_TYPE_NAME_CARD_IMAGE, uuid);
//        uuid = submitFileAndGetUuid(SUBMIT_FILE_URL, uploadFile2, FILE_TYPE_IDENTITY_CARD_IMAGE, uuid);
//        uuid = submitFileAndGetUuid(SUBMIT_FILE_URL, uploadFile3, FILE_TYPE_BILLING_IMAGE, uuid);
		try {
			ObjectMapper mapper = new ObjectMapper();
	        String result = "{\"port\":60123,\"status\":\"success\",\"server\":\"a01bpwebapp1a\",\"message\":\"S100:Upload successful\",\"protocol\":\"HTTP1.1\"}";
	        Response response = mapper.readValue(result, Response.class);
		} catch (Exception ex) {
			
		}
    }
	
//  httpclient, httpmine, httpcore	
//	public Response uploadFileAndProductType(String fileFullPath, String serverUrl, String productId) {
//    	HttpClient httpClient = getNewHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        HttpPost httpPost = new HttpPost(serverUrl);
//    	 try {
//    		 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//    		 httpPost.setEntity(entity);
//    		 entity.addPart("files", new FileBody(new File (fileFullPath)));
//    		 entity.addPart("producttype", new StringBody(productId));
//    		 Log.i("upload file", "Start uploading");
//    	     HttpResponse httpResponse = httpClient.execute(httpPost, localContext);
//    	     Log.i("upload file", "Finish upload : " + httpResponse);
//    	     BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
//    	     StringBuilder builder1 = new StringBuilder();
//			 for (String line = null; (line = reader.readLine()) != null;) {
//					if (StringUtil.isNotEmpty(line)) {
//						builder1.append(line);
//					}
//			 }
//	         reader.close();
//	          
//	         ObjectMapper mapper = new ObjectMapper();
//	         Response response = mapper.readValue(builder1.toString(), Response.class);
//	         Log.i("uploadFile", "Response : " + response);
//	         return response;
//         } catch (Exception e) {
//             // handle exception here
//             Log.e(e.getClass().getName(), e.getMessage());
//         }
//    	 return null;
//    }
//    
//    public HttpClient getNewHttpClient() {
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//
//            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            HttpParams params = new BasicHttpParams();
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            registry.register(new Scheme("https", sf, 443));
//
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//
//            return new DefaultHttpClient(ccm, params);
//        } catch (Exception e) {
//            return new DefaultHttpClient();
//        }
//    }
//    
//    public class MySSLSocketFactory extends SSLSocketFactory {
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
//            super(truststore);
//            TrustManager tm = new X509TrustManager() {
//                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                }
//                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                }
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            };
//            sslContext.init(null, new TrustManager[] { tm }, null);
//        }
//        @Override
//        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
//            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
//        }
//        @Override
//        public Socket createSocket() throws IOException {
//            return sslContext.getSocketFactory().createSocket();
//        }
//    }
}