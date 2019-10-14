package com.example.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SiwApp;
import com.example.fragment.UserFragment;
import com.example.models.bean.HeadPicBean;
import com.example.textslidingmenu.R;
import com.example.util.CustomDialog;
import com.example.util.MyConstants;
import com.example.util.RetrofitHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Admin on 2017/12/2.
 */
public class HeadPicActivity extends BaseActivity implements View.OnClickListener{

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final int PHOTO_RESOULT = 4;

    private Button album, camera;
    private ImageView display,cn_back,cn_ok;
    private TextView cn_title;
    private RelativeLayout head;

    private static final String TAG = "HeadPicActivity";

    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 4;
    private static final String NOW_HEAD_SAVE = "now_pic_save2";
    private static final String NOW_HEAD_NAME = "now_pic_name2";
    private static SharedPreferences sharedPreferences;
    String dirsPath;
    private Uri imageUri;
    CustomDialog customDialog;
    Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_img);

        permissionTest();
        init();
        initMess();
    }

    private void initMess() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (1 == msg.what){
                    display.setImageBitmap((Bitmap) msg.obj);
                }else if (2 == msg.what){
                    display.setImageBitmap((Bitmap) msg.obj);
                }else {
                    MyConstants.getUrlPic(HeadPicActivity.this,display);
                }
            }
        };
    }

    //权限
    private void permissionTest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "拒绝获取相机权限，将不能拍照！", Toast.LENGTH_SHORT).show();
                Log.i("TAG","拒绝获取相机权限，将不能拍照！");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top; //态栏的高度
        //设置TextView的margin-top值
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) head.getLayoutParams();
        lp.topMargin = statusBarHeight;
        head.setLayoutParams(lp);

    }
    private void init() {
        album = (Button) findViewById(R.id.head_gallery);
        camera = (Button) findViewById(R.id.head_camera);
        cn_back = (ImageView) findViewById(R.id.cn_back);
        cn_ok = (ImageView) findViewById(R.id.cn_ok);
        cn_title = (TextView) findViewById(R.id.cn_title);
        display = (ImageView) findViewById(R.id.head_img);
        head = (RelativeLayout) findViewById(R.id.head);
        cn_title.setText(R.string.head_img_title);
        cn_ok.setOnClickListener(this);
        cn_back.setOnClickListener(this);
        album.setOnClickListener(this);
        camera.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(NOW_HEAD_SAVE, MODE_MULTI_PROCESS);
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (isSDCardCanUser()) {
                    dirsPath = MyConstants.rootPath;
                    File dirFile = new File(dirsPath);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    } else {
                        Log.i("dirFile", "文件夹存在!");
                    }
                } else {
                    SiwApp.toast("SD卡不存在！");
                }
                //头像在本地存储
                if (!TextUtils.isEmpty(sharedPreferences.getString(NOW_HEAD_NAME, null)) && new File(dirsPath, sharedPreferences.getString(NOW_HEAD_NAME, null)).exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(dirsPath + "/" + sharedPreferences.getString(NOW_HEAD_NAME, null));
                    if (bitmap != null) {
                        Message message = new Message();
                        message.what =1;
                        message.obj = bitmap;
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(3);
                    }
                } else {
                    mHandler.sendEmptyMessage(3);
                }
            }
        }.start();
        customDialog = new CustomDialog(this,R.style.CustomDialog);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cn_back:
                finish();
                break;
            case R.id.cn_ok:
                //将修改后的头像上传到服务器
                uploadHeadImg();
                customDialog.show();
                break;
            case R.id.head_gallery:
                Log.i(TAG, "相册");
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent, ALBUM_REQUEST_CODE);
                break;
            case R.id.head_camera:
                Log.i(TAG, "相机");
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                        getExternalStorageDirectory(), "temp.jpg")));
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                break;
        }
    }

    private void isFinish(){
        finish();
    }
    /**
     * 开始裁剪
     *
     * @param uri
     */
    private void startCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /**
     * 判断sdcard卡是否可用
     *
     * @return 布尔类型 true 可用 false 不可用
     */
    private boolean isSDCardCanUser() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ALBUM_REQUEST_CODE:
                //相册，开始裁剪
                if (data == null) {
                    return;
                }
                startCrop(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                //相机, 开始裁剪
                File picture = new File(Environment.getExternalStorageDirectory()
                        + "/temp.jpg");
                startCrop(Uri.fromFile(picture));
                break;
            case CROP_REQUEST_CODE:
                //相册裁剪成功
                final Intent data2 = data;
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (data2 == null) {
                            // TODO 如果之前以后有设置过显示之前设置的图片 否则显示默认的图片
                            return;
                        }
                        String picName = "admin_" + MyConstants.FORMAT.format(new Date(System.currentTimeMillis())) + ".png";
                        File myIconFile = new File(dirsPath + "/" + picName);
                        sharedPreferences.edit().putString(NOW_HEAD_NAME, picName).commit();
                        SiwApp.setNowPic(SiwApp.NOW_PIC_NAME, picName);
                        Bundle extras = data2.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            try {
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myIconFile));
                                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);// (0-100)压缩文件
                                bos.flush();
                                bos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what =2;
                            message.obj = photo;
                            mHandler.sendMessage(message);
                        }
                    }
                }.start();
                break;
            default:
                break;
        }
    }

    public void uploadHeadImg(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                File file = new File(dirsPath,sharedPreferences.getString(NOW_HEAD_NAME,"head_img.png"));
                if (file!=null&&file.exists()){
                    RetrofitHelper.uploadHeadPic(file, new RetrofitHelper.OnRequestListener<HeadPicBean>() {
                        @Override
                        public void onError(Throwable throwable) {
                            Log.i("SiwApp","上传头像失败"+throwable.toString());
                        }

                        @Override
                        public void onFailed(int code) {
                            Log.i("SiwApp","上传头像失败，错误码："+code);
                        }

                        @Override
                        public void onSuccess(HeadPicBean bean) {
                            customDialog.cancel();
                            SiwApp.toast("上传成功");
                            Message message = new Message();
                            message.what = 2;
                            UserFragment.mHandler.sendMessage(message);
                            String childimg = bean.getData().get(0).getFileurl();
                            if (childimg!=null&&childimg.length()>0&&childimg.contains(",")) {
                                String[] strs = childimg.split(",");
                                childimg = strs[1];
                            }
                            SiwApp.setPicURL(SiwApp.URL_PIC_NAME,childimg);
                            isFinish();
                        }
                    });
                }
            }
        }.start();
    }
}
