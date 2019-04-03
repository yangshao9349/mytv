package reco.frame.demo.entity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.R.string;
import android.util.Log;

public class HttpClient {
	 private LinkedList params=null;


	 public  String doGet(String url){  

	        HttpParams httpParams = new BasicHttpParams();  
	        HttpConnectionParams.setConnectionTimeout(httpParams,30000);    
	        HttpConnectionParams.setSoTimeout(httpParams, 30000);    
	        String content="";
	        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);    
	        // GET  
	        HttpGet httpGet = new HttpGet(url);  
	        try {  
	            HttpResponse response = httpClient.execute(httpGet);  
	            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){  
	            	
	                Log.i("GET", "Bad Request!");  
	            }else{
	            	
	            	content= EntityUtils.toString(response.getEntity()); 
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            System.out.println("������");
	        }  
	        return content;
	    }  

	public static   String doPost(String urlpath,LinkedList params) {
		//��GET��ʽһ���Ƚ��������List
		String content="";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpPost postMethod = new HttpPost(urlpath);
		    postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8")); //����������POST Entity��
	
		    HttpResponse response = httpClient.execute(postMethod); //ִ��POST����
		    content= EntityUtils.toString(response.getEntity(), "utf-8"); //��ȡ��Ӧ����
						
		} catch (UnsupportedEncodingException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return content;
    }

	public String uploadFile(String actionUrl,File uploadFile,String newName)
    {
      String end ="\r\n";
      String twoHyphens ="--";
      String boundary ="*****";
      String returnstr="";
      try
      {
        URL url =new URL(actionUrl);
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        /* ����Input��Output����ʹ��Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        /* ���ô��͵�method=POST */
        con.setRequestMethod("POST");
        /* setRequestProperty */
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type",
                           "multipart/form-data;boundary="+boundary);
        /* ����DataOutputStream */
        DataOutputStream ds =
          new DataOutputStream(con.getOutputStream());
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; "+
                      "name=\"file1\";filename=\""+
                      newName +"\""+ end);
        ds.writeBytes(end);  
        /* ȡ���ļ���FileInputStream */
        FileInputStream fStream =new FileInputStream(uploadFile);
        /* ����ÿ��д��1024bytes */
        int bufferSize =1024;
        byte[] buffer =new byte[bufferSize];
        int length =-1;
        /* ���ļ���ȡ����������� */
        while((length = fStream.read(buffer)) !=-1)
        {
          /* ������д��DataOutputStream�� */
          ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        /* close streams */
        fStream.close();
        ds.flush();
        /* ȡ��Response���� */
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b =new StringBuffer();
        while( ( ch = is.read() ) !=-1 )
        {
          b.append( (char)ch );
        }
        /* ��Response��ʾ��Dialog */
        returnstr =b.toString().trim();
        /* �ر�DataOutputStream */
        ds.close();
      }
      catch(Exception e)
      {
        System.out.println("�ϴ�ʧ��"+e);
      }
      return returnstr;
    }
}
