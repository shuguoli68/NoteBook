     程序挂了也不会闪退，如：点击按钮，出现异常，程序不会有任何反应，就像这个按钮本来就不能点击



在继承application的类中：implements Cockroach.ExceptionHandler

在onCreate中：
  //异常捕获
   Cockroach.install(this);


重写方法：

@Override
    public void handlerException(final Thread thread, final  Throwable throwable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter pw = null;
                File file = null;
                try {
                    Date now = new Date(System.currentTimeMillis());
                    final Format FORMAT = new SimpleDateFormat("MM-dd HH-mm-ss", Locale.getDefault());
                    String fileName = "crash_"+FORMAT.format(now) + ".txt";
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                        File crashFolder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator, "crashLog");
                        if (!crashFolder.exists()){
                            crashFolder.mkdir();
                        }
                        file = new File(crashFolder,fileName);
                        pw = new PrintWriter(new FileWriter(file, false));
                        pw.write(getCrashHead());
                        throwable.printStackTrace(pw);
                        Throwable cause = throwable.getCause();
                        while (cause != null) {
                            cause.printStackTrace(pw);
                            cause = cause.getCause();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (pw != null) {
                        pw.close();
                    }
                }
                if (file!=null&&file.exists()){
                    //上传异常日志到服务器
                }
            }
        }).start();
//toast提示，调试用
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Cockroach", thread + "\n" + throwable.toString());
                            throwable.printStackTrace();
                            Toast.makeText(instance, "调试：程序发生了异常！！\n" + thread + "\n"
                                    + throwable.toString(), Toast.LENGTH_LONG).show();
                        } catch (Throwable e) {

                        }
                    }
                });
    }


//版本信息
public String getCrashHead() {
        int versionCode = -1;
        String versionName = "";
        try {
            PackageInfo pi = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "\n************* Crash Log Head ****************" +
                "\n设备厂商    : " + Build.MANUFACTURER +
                "\n设备型号    : " + Build.MODEL +
                "\n系统版本    : " + Build.VERSION.RELEASE +
                "\nSDK版本     : " + Build.VERSION.SDK_INT +
                "\n版本名称    : " + versionName +
                "\n版本号      : " + versionCode +
                "\n************* Crash Log Head ****************\n\n";
    }


