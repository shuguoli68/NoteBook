https://github.com/niniloveyou/StateButton



���ã������޸ı�����������ɫ����״��

<com.example.util.StateButton
            android:id="@+id/radius_test"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_margin="10dp"
            android:padding="10dp"
            android:text="Բ��"
            android:textColor="@android:color/white"
            app:normalBackgroundColor="@color/colorPrimary"
            app:pressedBackgroundColor="@color/colorPrimaryDark"
            app:radius="10dp"
            app:unableBackgroundColor="@android:color/holo_red_light"
app:normalTextColor="@android:color/white"
app:pressedTextColor="@android:color/white"
app:unableTextColor="@android:color/white"
/>

        <com.example.util.StateButton
            android:id="@+id/round_test"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_margin="10dp"
            android:padding="10dp"
            android:text="��Բ"
            android:textColor="@android:color/white"
            app:normalBackgroundColor="@color/colorPrimary"
            app:pressedBackgroundColor="@color/colorPrimaryDark"
            app:round="true"
            app:radius="2dp"
            app:unableBackgroundColor="@android:color/holo_red_light"
/>

        <com.example.util.StateButton
            android:id="@+id/different_radius_test"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_margin="10dp"
            android:padding="10dp"
            android:text="����"
            android:textColor="@android:color/white"
            app:normalBackgroundColor="@color/colorPrimary"
            app:pressedBackgroundColor="@color/colorPrimaryDark"
            app:unableBackgroundColor="@android:color/holo_red_light"/>

����Բ�ǵ����õ���¼���
 case R.id.radius_test:
                radiusTest.setRadius(new float[]{40, 40, 40, 40, 40, 40, 40, 40});
                break;