package cn.sharelink.fingerdemo;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;

public class FingerUtil {

    private Activity mContext;
    private static FingerUtil fingerUtil;
    private static final String TAG = "MyCallBack";
    private FingerprintManagerCompat manager;
    private KeyguardManager keyManager;
    private CancellationSignal mCancellationSignal = new CancellationSignal();
    private boolean isAble = false;
    private int num = 0;

    public FingerUtil(Activity mContext){
        this.mContext = mContext;
        manager = FingerprintManagerCompat.from(mContext);
        keyManager= (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        initFinger();
    }

    private void initFinger() {
        if (manager.isHardwareDetected()){
            if (manager.hasEnrolledFingerprints()){
                if(keyManager.isKeyguardSecure()){
                    Log.i(TAG, "initView: 指纹硬件存在并且功能正常,至少有一个指纹登记,已设置锁屏密码");
                    isAble = true;
                }else {
                    Log.i(TAG, "initView: 指纹硬件存在并且功能正常,至少有一个指纹登记,没有设置锁屏密码");
                    toast("请先设置锁屏密码！");
                }
            }else {
                Log.i(TAG, "initView: 指纹硬件存在并且功能正常,没有指纹登记");
                toast("请先进行指纹登记！");
            }
        }else {
            Log.i(TAG, "initView: 指纹硬件不存在或者功能不正常");
            toast("不支持指纹解锁！");
        }
    }

    public static FingerUtil getInstance(Activity mContext) {
        if (fingerUtil == null) {
            synchronized (FingerUtil.class) {
                if (fingerUtil == null) {
                    fingerUtil = new FingerUtil(mContext);
                }
            }
        }
        return fingerUtil;
    }

    public void start(){
        if (isAble) {
            if (mCancellationSignal.isCanceled()) {
                mCancellationSignal = new CancellationSignal();
            }
            manager.authenticate(null, 0, mCancellationSignal, new MyCallBack(), null);
            Log.i(TAG, "onClick: 开启成功");
            toast("指纹功能已开启！");
        }
    }

    public void stop(){
        if (isAble && !mCancellationSignal.isCanceled()) {
            mCancellationSignal.cancel();
            Log.i(TAG, "onClick: 关闭成功");
            toast("指纹功能已关闭！");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //onAuthenticationError或onAuthenticationSucceeded暂停30秒
            Log.i(TAG, "handleMessage: 重启指纹模块成功");
            manager.authenticate(null, 0, mCancellationSignal, new MyCallBack(), handler);
        }
    };

    private void toast(String str){
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    private class MyCallBack extends FingerprintManagerCompat.AuthenticationCallback {
        //开始验证后，什么时候停止由系统来确定，如果验证成功，那么系统会关系sensor，如果失败，则允许
        //多次尝试，如果依旧失败，则会拒绝一段时间，然后关闭sensor，过一段时候之后再重新允许尝试

        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Log.i(TAG, "onAuthenticationError: " + errString);
            toast("指纹验证错误！");
            handler.sendMessageDelayed(new Message(), 1000 * 30);
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            Log.i(TAG, "onAuthenticationFailed: " + "验证失败");
            num++;
            if(num==3){
                Log.i(TAG, "onAuthenticationFailed: 失败次数过多,请输入锁屏密码");
                Toast.makeText(mContext, "失败次数过多,请输入锁屏密码", Toast.LENGTH_SHORT).show();
                Intent intent=keyManager.createConfirmDeviceCredentialIntent("finger","开启锁屏密码");
                if(intent!=null){
                    mContext.startActivityForResult(intent, 1);
                }
                num=0;
            }
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Log.i(TAG, "onAuthenticationHelp: " + helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult
                                                      result) {
            Log.i(TAG, "onAuthenticationSucceeded: " + "验证成功");
            toast("指纹验证成功！");
            handler.sendMessageDelayed(new Message(), 1000 * 30);
        }
    }
}
