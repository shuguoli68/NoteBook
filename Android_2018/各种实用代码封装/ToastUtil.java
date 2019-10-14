package cn.sharelink.leddemo.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static boolean isShow=true;
    private static Toast mToast=null;


    private ToastUtil(){
        throw  new UnsupportedOperationException("不能被实例化");
    }


    //全局控制是否显示Toast
    public static void controlShow(boolean ishowToat){
        isShow=ishowToat;
    }

    //取消Toast显示
    public void cancelToast(){

        if(isShow&&mToast!=null){
            mToast.cancel();
        }
    }


    //短时间显示Toast
    public static void showShort(Context context,CharSequence message){

        if(isShow){
            if(mToast==null){
                mToast=Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_SHORT);
              
            }else {
                mToast.setText(message);
            }
			mToast.show();

        }
    }

    //短时间显示Toast,传入资源ID
    public static void showShort(Context context,int resId){

        if(isShow){

            if(mToast==null){
                mToast=Toast.makeText(context.getApplicationContext(),resId,Toast.LENGTH_SHORT);

            }else {
                mToast.setText(resId);
            }
			mToast.show();
        }

    }

    //长时显示Toast
    public static void showLong(Context context,CharSequence message){
        if(isShow){
            if(mToast==null){
                mToast=Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_LONG);

            }else {
                mToast.setText(message);
            }
			mToast.show();
        }

    }

    //长时间显示Toast
    public static void showLong(Context context,int resId){

        if(isShow){

            if(mToast==null){
                mToast=Toast.makeText(context.getApplicationContext(),resId,Toast.LENGTH_LONG);
            }else {

                mToast.setText(resId);
            }
			mToast.show();

        }

    }


    //自定义显示的Toast时间
    public static void  show(Context context,CharSequence message,int duration){
        if(isShow){

            if(mToast==null){
                mToast=Toast.makeText(context.getApplicationContext(),message,duration);

            }else {
                mToast.setText(message);
            }
			mToast.show();

        }

    }

    //自定义显示时间
    public static void show(Context context, int resId, int duration){

        if(isShow){
            if(mToast==null){
                mToast=Toast.makeText(context.getApplicationContext(),resId,duration);
            }else {
                mToast.setText(resId);
            }
			mToast.show();
        }

    }

}
