package com.example.alex.guoanh5game;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Http_Client {
	/**
	 * httpClient Get璇锋眰
	 * 
	 * @param url
	 *            璇锋眰鍦板潃鍜屽弬鏁?
	 * @return 鏈嶅姟绔暟鎹繑鍥?
	 */
	private static int TIME = 5 * 1000;;
	private static String ENCODING = "UTF-8";


	public static String mapToHttpString(Map<String,String> map){
		List<String> keys = new ArrayList<String>(map.keySet());
		// key鎺掑簭
		Collections.sort(keys);

		StringBuilder authInfo = new StringBuilder();
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			String value = map.get(key);
			authInfo.append(buildKeyValue(key, value, true));
			authInfo.append("&");
		}

		String tailKey = keys.get(keys.size() - 1);
		String tailValue = map.get(tailKey);
		authInfo.append(buildKeyValue(tailKey, tailValue, true));

		return authInfo.toString();
	}
	/**
	 * 鎷兼帴閿?煎
	 * 
	 * @param key
	 * @param value
	 * @param isEncode
	 * @return
	 */
	private static String buildKeyValue(String key, String value, boolean isEncode) {
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("=");
		if (isEncode) {
			sb.append(requestEncodeStr(value));
		} else {
			sb.append(value);
		}
		return sb.toString();
	}


	/**
	 * 瀵硅姹傜殑瀛楃涓茶繘琛岀紪鐮?
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String requestEncodeStr(String requestStr)  {
		try {
			if (requestStr != null) {
				return URLEncoder.encode(requestStr, ENCODING);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return requestStr;
	}

	private static String readStream(InputStream inStream) throws Exception {
		String result;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		result = new String(outStream.toByteArray(), ENCODING);
		outStream.close();
		inStream.close();
		return result;
	}


	public static String httpClientGet(String strUrl) {
		String responseStr = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(TIME);
			conn.setReadTimeout(TIME);
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream	 input = conn.getInputStream();
				if (input != null) {
					//鎷垮埌娴佸悗澶勭悊
					responseStr = readStream(input);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return responseStr;
	}

	/**
	 * httpClient Post璇锋眰
	 * 
	 * @param url
	 *            璇锋眰鍦板潃
	 * @param params
	 *            璇锋眰鍙傛暟
	 * @return 鏈嶅姟绔暟鎹繑鍥?
	 */
	public static String httpClientPost(String strUrl,
			String body) {
		String responseStr = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIME);
			conn.setReadTimeout(TIME);
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
            // 鑾峰彇URLConnection瀵硅薄瀵瑰簲鐨勮緭鍑烘祦
            PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
            // 鍙戦?佽姹傚弬鏁?
            printWriter.write(body);//post鐨勫弬鏁? xx=xx&yy=yy
            // flush杈撳嚭娴佺殑缂撳啿
            printWriter.flush();
            
            //寮?濮嬭幏鍙栨暟鎹?
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while((len=bis.read(arr))!= -1){
                bos.write(arr,0,len);
                bos.flush();
            }
            bos.close();
            return bos.toString("utf-8");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return responseStr;
	}

}
