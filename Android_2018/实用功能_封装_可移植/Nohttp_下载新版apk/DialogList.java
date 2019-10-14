package cn.sharelink.leddemo.contract;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.accloud.service.ACDeviceBind;

import java.util.List;

import cn.sharelink.leddemo.R;
import cn.sharelink.leddemo.util.LogUtils;
import cn.sharelink.leddemo.util.ToastUtil;

public class DialogList {

    private static int position = 0;

    public static void showList(Context mContext, List<ACDeviceBind> acDeviceBinds, final MyCallBack back){
        int size = acDeviceBinds.size();
        if (size == 0){
            ToastUtil.showShort(mContext,mContext.getString(R.string.bind_empty));
            return;
        }
        final String[] values = new String[size];
        for (int i=0;i<size;++i){
            values[i] = acDeviceBinds.get(i).getPhysicalDeviceId();
        }
        AlertDialog alertDialog
                = new AlertDialog.Builder(mContext, R.style.my_dialog)
                .setTitle(mContext.getString(R.string.bind_one))
                .setSingleChoiceItems(values, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LogUtils.i("选择："+i);
                        position = i;
                    }
                })
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        back.click(position);
                    }})
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public static void showImg(Context mContext, Bitmap bitmap){
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bitmap);
        AlertDialog alertDialog
                = new AlertDialog.Builder(mContext, R.style.my_dialog)
                .setView(imageView)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private static ProgressDialog pd;
    public static ProgressDialog showProgress(Context mContext, String title, String msg, final MyCancelCall cancelCall){
        pd = new ProgressDialog(mContext,R.style.my_dialog_bg);
        pd.setTitle(title);
        pd.setMessage(msg);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                cancelCall.cancelClick();
            }
        });
        if (!pd.isShowing()){
            pd.show();
        }
        return pd;
    }

    //有数字进度的对话框
    private static ProgressDialog mDialog;
    public static ProgressDialog numProgress(Context mContext, String title, String msg,int max, final MyCancelCall cancelCall){
        mDialog = new ProgressDialog(mContext,R.style.my_dialog_bg);
        mDialog.setTitle(title);
        mDialog.setMessage(msg);
        mDialog.setMax(max);
        mDialog.setProgress(0);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mDialog.isShowing()){
                    mDialog.dismiss();
                }
                cancelCall.cancelClick();
            }
        });
        if (!mDialog.isShowing()){
            mDialog.show();
        }
        return mDialog;
    }

    public interface MyCallBack{
        void click(int i);
    }

    public interface MyCancelCall{
        void cancelClick();
    }

    public static Dialog getDialog(final Context mContext, String title, String msg, final MyDialogCall dialogCall ){
        final AlertDialog alert = new AlertDialog.Builder(mContext,R.style.my_dialog_bg).setTitle(title)
                .setMessage(msg)
                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogCall.cancel();
                    }
                })
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogCall.ok();
                    }
                })
                .create();             //创建AlertDialog对象
        if (alert!=null && !alert.isShowing()) {
            alert.show();
        }
        return alert;
    }

    //有数字进度的对话框
    public static ProgressDialog numProgress(Context mContext, String title, String msg){
        ProgressDialog mDialog = new ProgressDialog(mContext,R.style.my_dialog_bg);
        mDialog.setTitle(title);
        mDialog.setMessage(msg);
        mDialog.setMax(100);
        mDialog.setProgress(0);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        if (!mDialog.isShowing()){
            mDialog.show();
        }
        return mDialog;
    }

    public interface MyDialogCall{
        void cancel();
        void ok();
    }
}
