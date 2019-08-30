package com.hjq.http.net.download;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
private static final String FILE_NAME = "i999.apk";
private final static String APK_URL = "http://www.imooc.com/mobile/mukewang.apk";
private final static String FILE_PATH = Environment.getExternalStorageDirectory()
        + File.separator + FILE_NAME;
*/

public class DownloadHelper {
    public static void Download(String url, String path, onDownloadListener listener){
        DownloadAsync task = new DownloadAsync(url, path, listener);
        task.execute();
    }

    public static class DownloadAsync extends AsyncTask<String, Integer, Boolean> {
        String mUrl;
        String mPath;
        onDownloadListener mOnDownloadListener;
        static int contentLength;

        public DownloadAsync(String url, String path, onDownloadListener listener) {
            mUrl = url;
            mPath = path;
            mOnDownloadListener = listener;
        }

        /**
         * 通过主线程调用execute(String ..)执行
         * （子线程）
         */
        @Override
        protected Boolean doInBackground(String... params) {

            try {
                /**
                 * 执行下载任务
                 */
                URL url = new URL(mUrl);
                URLConnection con = url.openConnection();
                InputStream is = con.getInputStream();
                //下载总长度
                contentLength = con.getContentLength();

                File apkFile = new File(mPath);
                if(apkFile.exists()){
                    if(!apkFile.delete()){
                        if(mOnDownloadListener != null){
                            mOnDownloadListener.onFail(-2, apkFile, "文件删除失败");
                        }
                    }
                }
                // 已下载长度
                int downloadLength = 0;
                byte[] bytes = new byte[1024];
                int len;
                OutputStream os = new FileOutputStream(mPath);
                while ((len = is.read(bytes)) >-1 ){
                    os.write(bytes, 0, len);
                    downloadLength += len;
                    //传入两个参数到onProgressUpdate(Integer... values)
                    publishProgress(downloadLength * 100 / contentLength
                            , downloadLength);
                }
                is.close();
                os.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                if(mOnDownloadListener != null){
                    mOnDownloadListener.onFail(-2, new File(mPath), e.getMessage());
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                if(mOnDownloadListener != null){
                    mOnDownloadListener.onFail(-3, new File(mPath), e.getMessage());
                }
                return false;
            }

            return true;
        }

        /**
         * 请求之前的操作调用了接口
         * 的onStart方法(UI线程)
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mOnDownloadListener != null){
                mOnDownloadListener.onStart();
            }
        }

        /**
         * 请求之后的操作，判断了doInBackground返回值Boolean类型调用了接口
         * 的onSuccess（UI线程）
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                if(mOnDownloadListener != null){
                    mOnDownloadListener.onSuccess(0, new File(mPath));
                }
            }else{

            }
        }

        /**
         * 在doInBackground中调用的publishProgress会将其参数传入
         * @Integer类型可变参数，用于更新进度条及下载进度（UI线程）
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values == null || values.length == 0) {
                return ;
            }
            if(mOnDownloadListener != null){
                mOnDownloadListener.onProgress(values[0], values[1]);
            }
        }
    }
    //自定义接口用于实现请求成功，失败，进度，开始的处理
    public interface onDownloadListener {
        void onSuccess(int code, File file);
        void onFail(int code, File file, String msg);
        void onProgress(int progress, int downloadLength);
        void onStart();
    }
}
