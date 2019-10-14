package com.xinlian.xladver.okhttp;

import android.util.Log;

import com.xinlian.xladver.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

public class ResponseBodyToFile {
    //Log标记
    private static final String TAG = "eeeee";
    //APK文件类型
    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";
    //PNG文件类型
    private static String PNG_CONTENTTYPE = "image/png";
    //JPG文件类型
    private static String JPG_CONTENTTYPE = "image/jpg";
    //文件后缀名
    private static String fileSuffix="";
    private static int gress = -1;

    /**
     * 写入文件到本地
     * @param file
     * @param body
     * @return
     */
    public static boolean  writeResponseBodyToDisk(File file,String name, ResponseBody body, MyProgress my) {
        gress = -1;
        if (!file.exists()){
            boolean b = file.mkdirs();
            if (!b)
                LogUtil.i("文件夹创建失败！");
            else
                LogUtil.i("文件夹创建成功！");
        }
        File newFile = new File(file , name);
        if (newFile.exists()){
            newFile.delete();
        }
        Log.d(TAG, "contentType:>>>>" + body.contentType().toString());
        //下载文件类型判断，并对fileSuffix赋值
        String type = body.contentType().toString();

        if (type.equals(APK_CONTENTTYPE)) {
            fileSuffix = ".apk";
        } else if (type.equals(PNG_CONTENTTYPE)) {
            fileSuffix = ".png";
        }

        // 其他类型同上 需要的判断自己加入.....

        //下面就是一顿写入，文件写入的位置是通过参数file来传递的
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;

        try {
            is = body.byteStream();
            long total = body.contentLength();

            fos = new FileOutputStream(newFile);
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
//                LogUtils.i("进度："+progress+",具体值："+sum*1.0f/total*1.0f);
                if (gress != progress) {
                    gress = progress;
                    my.getProress(progress);
                }
            }
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }
    }

    public interface MyProgress{
        void getProress(int progress);
    }
}
