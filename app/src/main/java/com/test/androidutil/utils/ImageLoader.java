package com.test.androidutil.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.yifan.shufa.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author:Lynn M614778
 * @Data:2016-1-7下午11:36:44
 */
public class ImageLoader {
    private View mView;
    private ImageView mImageView = null;
	private String mUrl = null;
	// 创建Cache
	public static LruCache<String, Bitmap> mCaches;
	private Set<NewsAsyncTask> mTask;
	/**
	 * imageview控件大小
	 */
	private int taskValue=0;


	public ImageLoader(ImageView imageview, String mPath) {
		// this.imageview = imageview;
		new NewsAsyncTask1(mPath, imageview).execute("");

	}
	public ImageLoader(ImageView imageview, String mPath, int size) {
		// this.imageview = imageview;
		taskValue = size;
		new NewsAsyncTask1(mPath, imageview).execute("");

	}

	public ImageLoader(View view) {
		mView = view;
// 	public ImageLoader(RecyclerView recyclerView) {
//		mRecyclerView = recyclerView;
		mTask=new HashSet<>();
		// 获取最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 4;
		mCaches = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 在每次存入缓存的时候调用
				return value.getByteCount();
			}
		};
	}


	// 增加到缓存
	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (getBitmapFromCache(url) == null) {
			mCaches.put(url, bitmap);
		}
	}

	// 从缓存中获取数据
	public static Bitmap getBitmapFromCache(String url) {
		return mCaches.get(url);
	}

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mImageView.getTag().equals(mUrl)) {
				mImageView.setImageBitmap((Bitmap) msg.obj);
			}
		};
	};

	public void showImageByThread(ImageView imageView, final String url) {

		mImageView = imageView;
		mUrl = url;

		new Thread() {
			@Override
			public void run() {
				super.run();
				Bitmap bitmap = getBitmapFromURL(url);
				Message message = Message.obtain();
				message.obj = bitmap;
				mhandler.sendMessage(message);
			}
		}.start();
	}

	public Bitmap getBitmapFromURL(String urlString) {
		Bitmap bitmap;
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			is = new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			connection.disconnect();
			// Thread.sleep(1000);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is!=null){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void showImageByAsyncTask(ImageView imageView, String url) {
		// 从缓存中取出对应的图片
		Bitmap bitmap = getBitmapFromCache(url);
		// 如果缓存中没有，那么必须去下载
		if (bitmap == null) {
			imageView.setImageResource(R.mipmap.default_error);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}
    public Bitmap showImageByAsyncTask1(ImageView imageView, String url) {
        // 从缓存中取出对应的图片
        Bitmap bitmap = getBitmapFromCache(url);
        // 如果缓存中没有，那么必须去下载
//        if (bitmap == null) {
//            imageView.setImageResource(R.mipmap.default_error);
//        } else {
//            imageView.setImageBitmap(bitmap);
//        }
        return bitmap;
    }

	public void cancelAllTasks(){
		if(mTask!=null){
			for(NewsAsyncTask task:mTask){
				task.cancel(false);
			}
		}
	}
	//用来加载从start到end的所有图片
	public void loadImage(int start, int end , String URLS[], View view) {
		//	防止数组越界
		if(end> URLS.length){
			end = URLS.length;
		}
		for (int i = start; i < end; i++) {
			String url= URLS[i];
			// 从缓存中取出对应的图片
			Bitmap bitmap = getBitmapFromCache(url);
			// 如果缓存中没有，那么必须去下载
			if (bitmap == null) {
				NewsAsyncTask task=new NewsAsyncTask(url);
				task.execute(url);
				mTask.add(task);
			} else {
				ImageView imageView;
                try {
                    imageView=(ImageView) view.findViewWithTag(url);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		}
	}

	private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

		private String mUrl;
//		private ImageView mImageView;

		public NewsAsyncTask(String url) {
//			mImageView = imageView;
			mUrl = url;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];
			// 从网络获取图片
			Bitmap bitmap = getBitmapFromURL(params[0]);
			if (bitmap != null) {
				// 将不在缓存的图片加入缓存
				addBitmapToCache(url, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			ImageView imageView;
            imageView=(ImageView) mView.findViewWithTag(mUrl);
			if(imageView!=null&&bitmap!=null){
				imageView.setImageBitmap(bitmap);
			}
			mTask.remove(this);
		}
	}
	public Bitmap getBitmapFromURL1(String urlString) {
		Bitmap bitmap = null;
		BufferedInputStream is = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			is = new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			connection.disconnect();
			// Thread.sleep(1000);

			// // 如果图片大于200kB则进行压缩成100kB左右
			// if (bitmap.getByteCount() / 1024 > 100) {
			// bitmap = scalebitmap(bitmap);
			// }
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	private class NewsAsyncTask1 extends AsyncTask<String, Void, Bitmap> {

		private String mUrl;
		private ImageView mImageView;

		public NewsAsyncTask1(String url, ImageView imageview) {
			// mImageView = imageView;
			mUrl = url;
			mImageView = imageview;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// String url = params[0];
			// 从网络获取图片
			Bitmap bitmap = getBitmapFromURL1(mUrl);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			if (mImageView != null && bitmap != null) {
				//  练习生字调用，需要跳转图片大小，防止图片超出ImageView背景米字格边框
				if(taskValue != 0){
					Bitmap bitmapend =createBitmapThumbnail(bitmap);
					if (bitmapend!=null)
					mImageView.setImageBitmap(bitmapend);
				}else {
					mImageView.setImageBitmap(bitmap);
				}
			}
			// mTask.remove(this);
		}
	}

	/**
	 * 压缩图片防止空心字超出ImageView控件大小
	 * @param bitMap
	 * @return
     */

	public Bitmap createBitmapThumbnail(Bitmap bitMap) {
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		int max;
		if (width==0||height==0 )
			return null;
		max = Math.max(width,height);

		Bitmap bitmap = createNewBitmap(bitMap, width, height, (float)(taskValue*7)/(max*9));
		return createNewBitmap(bitMap, width, height, (float)(taskValue*7)/(max*9));
	}

	private Bitmap createNewBitmap(Bitmap bitMap, int width, int height, float scale) {
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// 得到新的图片
		return Bitmap.createBitmap(bitMap, 0, 0, width, height,
            matrix, true);
	}

}
