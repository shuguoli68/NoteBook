     �������Ҳ�������ˣ��磺�����ť�������쳣�����򲻻����κη�Ӧ�����������ť�����Ͳ��ܵ��



�ڼ̳�application�����У�implements Cockroach.ExceptionHandler

��onCreate�У�
  //�쳣����
   Cockroach.install(this);


��д������

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
                    //�ϴ��쳣��־��������
                }
            }
        }).start();
//toast��ʾ��������
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Cockroach", thread + "\n" + throwable.toString());
                            throwable.printStackTrace();
                            Toast.makeText(instance, "���ԣ����������쳣����\n" + thread + "\n"
                                    + throwable.toString(), Toast.LENGTH_LONG).show();
                        } catch (Throwable e) {

                        }
                    }
                });
    }


//�汾��Ϣ
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
                "\n�豸����    : " + Build.MANUFACTURER +
                "\n�豸�ͺ�    : " + Build.MODEL +
                "\nϵͳ�汾    : " + Build.VERSION.RELEASE +
                "\nSDK�汾     : " + Build.VERSION.SDK_INT +
                "\n�汾����    : " + versionName +
                "\n�汾��      : " + versionCode +
                "\n************* Crash Log Head ****************\n\n";
    }


