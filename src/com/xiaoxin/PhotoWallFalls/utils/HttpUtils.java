package com.xiaoxin.PhotoWallFalls.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

/**
 * ��װ�� ���� + �߳�
 */
public class HttpUtils {

	// ʹ���̳߳�������ͼƬ��ͬһʱ�̣������3���߳�������
	private static ExecutorService execuotrs = Executors.newFixedThreadPool(3);

	interface OnBitmapNetWorkResponse {
		public void ok(Bitmap bitmap);

		public void error(String error);
	}

	public static void RequestBitmapNetWork(final String path,
			final OnBitmapNetWorkResponse response) {
		
		final Handler handler = new Handler();

		execuotrs.execute(new Runnable() {
			@Override
			public void run() {
				boolean isNetWorkOK = false;
				try {
					URL url = new URL(path);
					HttpURLConnection openConnection = (HttpURLConnection) url
							.openConnection();
					openConnection.setConnectTimeout(5000);
					openConnection.connect();
					if (openConnection.getResponseCode() == 200) {
						InputStream inputStream = openConnection
								.getInputStream();
						final Bitmap bitmap = BitmapFactory
								.decodeStream(inputStream);

						handler.post(new Runnable() {

							@Override
							public void run() {
								response.ok(bitmap);
							}
						});

						inputStream.close();
						isNetWorkOK = true;
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (!isNetWorkOK) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								response.error("���������ڷ������ڣ�");
							}
						});

					}

				}

			}
		});
	}

	public interface OnNetWorkResponse {
		public void ok(String response);

		public void error(String error);
	}

	public static void RequestNetWork(final String path,
			final OnNetWorkResponse response) {
		//ʵ����handler 
		final Handler hanlder = new Handler();

		new Thread() {
			public void run() {
				//��־λ
				boolean isWorkOK = false;
				
				InputStream inputStream = null;
				ByteArrayOutputStream outStream = null;
				try {
					URL url = new URL(path);
					System.out.println("=======path========="+path);
					
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(5000);
					connection.setDoInput(true);
					connection.connect();

					if (connection.getResponseCode() == 200) {
						inputStream = connection.getInputStream();
						outStream = new ByteArrayOutputStream();

						byte[] b = new byte[1024];
						int len = 0;
						while ((len = inputStream.read(b)) != -1) {
							outStream.write(b, 0, len);
						}
						outStream.flush();
						final String result = new String(
								outStream.toByteArray());

						hanlder.post(new Runnable() {

							@Override
							public void run() {
								response.ok(result);
							}
						});

						isWorkOK = true;
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					// �������������
					if (!isWorkOK) {
						response.error("��������������");
					}

					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (outStream != null) {
						try {
							outStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			};
		}.start();

	}

}
