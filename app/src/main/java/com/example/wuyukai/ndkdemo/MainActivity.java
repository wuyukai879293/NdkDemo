package com.example.wuyukai.ndkdemo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wuyukai.ndkdemo.fragment.HealthFragment;
import com.example.wuyukai.ndkdemo.fragment.MsgFragment;
import com.example.wuyukai.ndkdemo.fragment.UsercenterFragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Fragment[] fragments;
    private RelativeLayout layout_home;
    private RelativeLayout layout_health;
    private RelativeLayout layout_msg;
    private RelativeLayout layout_usercenter;

    private ImageView img_home;
    private ImageView img_health;
    private ImageView img_msg;
    private ImageView img_usercenter;

    private TextView tv_home;
    private TextView tv_health;
    private TextView tv_msg;
    private TextView tv_usercenter;

    private int selectColor;
    private int unSelectColor;
    private int index;
    private int currentIndex;

    // 调试
//    private static final String TAG = "BluetoothChat";
    private static boolean D = true;
    private static final String info = "junge";
    // 类型的消息发送从bluetoothchatservice处理程序
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_GES_TYPE = 6;
    public static final String BluetoothData = "fullscreen";
    public String filename = ""; // 用来保存存储的文件名
    private String newCode = "";
    private String newCode2 = "";
    private String fmsg = ""; // 保存用数据缓存
    // 键名字从收到的bluetoothchatservice处理程序
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // 独特的是这个应用程序
    private int type = 0;
    volatile boolean dataArrive = false;
    boolean masterStop = false;
//    public TypeBluetooth mType = TypeBluetooth.Client;
    public static modeSC mSC = modeSC.CLIENT;
    public static modeSD mSD = modeSD.SINGLE;
    public static modeUSER mUSER = modeUSER.GENERAL;
    private int msgFromSlave = 0;

    // Intent需要 编码
    public static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private TextView mTextView;


    private static final String TAG = "AccelerometerSensorDemo";

    private Button startAc;
    private Button pickUser;
    private Switch swithMode;

    private String str;

    // 布局控件
    private EditText mInputEditText;
    private EditText mOutEditText;
    private ImageButton mSendButton;

    private TextView outInfo;
    private TextView output;


    private TextView textViewInfo;
    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;

    private MEMS mems = new MEMS();
    private byte[] msgBuffer = null;
    // 名字的连接装置
    private String mConnectedDeviceName = null;
    // 传出消息的字符串缓冲区
    // 当地的蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter = null;
    // 成员对象的聊天服务
    private BluetoothChatService mChatService = null;
    // 设置标识符，选择用户接受的数据格式
    private StringBuffer mOutStringBuffer;
    //第一次输入加入-->变量
    private int sum =1;
    private int UTF =1;


    String mmsg = "";
    String mmsg2 = "";

    public volatile boolean isStart = false;
    public volatile boolean isStop = false;
    private boolean dataFinish = false;
    private AccelerometerSensor accelerometerSensor;
    Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        init();
        accelerometerSensor = new AccelerometerSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE),mHandler,mems);
//        accelerometerSensor.setRunInMainAc();
        //默认启动时加载用户1的数据
//        accelerometerSensor.genTempTool(context,1);
        pickUser = (Button) findViewById(R.id.pickUser);
        mInputEditText = (EditText) findViewById(R.id.editText1);
        mInputEditText.setGravity(Gravity.TOP);
        mInputEditText.setSelection(mInputEditText.getText().length(), mInputEditText.getText().length());
        mInputEditText.clearFocus();
        mInputEditText.setFocusable(false);
        // 设置文本的标题
        outInfo = (TextView)findViewById(R.id.outInfo);
        output = (TextView)findViewById(R.id.output);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if(getWindow().getAttributes().softInputMode== WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)

        {
            //隐藏软键盘
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        // 初始化Socket
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "not_connect", Toast.LENGTH_LONG)
                    .show();
            finish();
            return;
        }
//        startAc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startAc.setText(mUSER+"");
//            }
//        });
        pickUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDialog();
            }
        });
//        swithMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    mSD = modeSD.DOUBLE;
//                }else {
//                    mSD = modeSD.SINGLE;
//                }
//            }
//        });
//        mTextView = (TextView)findViewById(R.id.text);
//        mTextView.setText(Command.getStringFromC());
    }

    public void init(){
        initViews();
        initEvent();
        initData();
    }

    public void initViews(){
//        layout_home  = (RelativeLayout) findViewById(R.id.layout_home);
        layout_health  = (RelativeLayout) findViewById(R.id.layout_health);
        layout_msg  = (RelativeLayout) findViewById(R.id.layout_msg);
        layout_usercenter  = (RelativeLayout) findViewById(R.id.layout_usercenter);

//        img_home = (ImageView) findViewById(R.id.img_home);
        img_health = (ImageView) findViewById(R.id.img_health);
        img_msg = (ImageView) findViewById(R.id.img_msg);
        img_usercenter = (ImageView) findViewById(R.id.img_usercenter);

//        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_health = (TextView) findViewById(R.id.tv_health);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_usercenter = (TextView) findViewById(R.id.tv_usercenter);

    }

    public void initEvent(){
//        layout_home.setOnClickListener(this);
        layout_health.setOnClickListener(this);
        layout_msg.setOnClickListener(this);
        layout_usercenter.setOnClickListener(this);
    }

    public void initData(){
        selectColor = getResources().getColor(R.color.bottom_text_color_pressed);
        unSelectColor = getResources().getColor(R.color.bottom_text_color_normal);
        fragments = new Fragment[3];
//        fragments[0] = new HomeFragment();
        fragments[0] = new HealthFragment();
        fragments[1] = new MsgFragment();
        fragments[2] = new UsercenterFragment();

        getFragmentManager().beginTransaction().add(R.id.main_container,fragments[0]).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.layout_home:
//                index = 0;
//                setTabs(index);
//                break;
            case R.id.layout_health:
                index = 0;
                mSD = modeSD.SINGLE;
                setTabs(index);
                break;
            case R.id.layout_msg:
                mSD = modeSD.DOUBLE;
                index = 1;
                setTabs(index);
                break;
            case R.id.layout_usercenter:
                index = 2;
                setTabs(index);
                break;
        }
        if (index ==2){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(fragments[currentIndex]);
            ft.commit();
            accelerometerSensor.sensorOff();
            Intent intent = new Intent(MainActivity.this,UserListActivity.class);
            startActivity(intent);
        }else if(currentIndex != index){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(fragments[currentIndex]);
            if(!fragments[index].isAdded()){
                ft.add(R.id.main_container,fragments[index]);
            }
            ft.show(fragments[index]).commit();
        }
        currentIndex = index;

    }

    public void setTabs(int pos){
        resetColor();
        switch (pos){
//            case 0:
//                img_home.setImageResource(R.mipmap.icon_home_pressed);
//                tv_home.setTextColor(selectColor);
//                break;
            case 0:
                img_health.setImageResource(R.mipmap.icon_health_pressed);
                tv_health.setTextColor(selectColor);
                break;
            case 1:
                img_msg.setImageResource(R.mipmap.icon_msg_pressed);
                tv_msg.setTextColor(selectColor);
                break;
            case 2:
                img_usercenter.setImageResource(R.mipmap.icon_usercenter_pressed);
                tv_usercenter.setTextColor(selectColor);
                break;
        }

    }

    public void resetColor(){
//        img_home.setImageResource(R.mipmap.icon_home_normal);
        img_health.setImageResource(R.mipmap.icon_health_normal);
        img_msg.setImageResource(R.mipmap.icon_msg_normal);
        img_usercenter.setImageResource(R.mipmap.icon_usercenter_normal);

//        tv_home.setTextColor(unSelectColor);
        tv_health.setTextColor(unSelectColor);
        tv_msg.setTextColor(unSelectColor);
        tv_usercenter.setTextColor(unSelectColor);
    }

    public void showUserDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("用户列表");
        SQLiteDatabase db = new SQLiteTool(MainActivity.this,"sunny.db").getReadableDatabase();
        Cursor c = db.rawQuery("select distinct user_name from user2 order by user_id",new String[] {});
        List<String> list = new ArrayList<>();
        if(c!=null){
            while (c.moveToNext()){
                list.add(c.getString(c.getColumnIndex("user_name")));
            }
        }
        final String[] userList = list.toArray(new String[0]);
        builder.setItems(userList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = userList[which];
                pickUser.setText(str);
                //索引从0开始,用户ID从1开始,所以+1
                int user_id = which + 1;
                accelerometerSensor.genTempTool(context,user_id);
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }


@Override
public void onStart() {
    super.onStart();
    accelerometerSensor.setRunInMainAc();
//    accelerometerSensor.genTempTool(context,1);
    if (D)
        Log.e(TAG, "++ ON START ++");


    if (!mBluetoothAdapter.isEnabled()) {
        Intent enableIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // 否则，设置聊天会话
    } else {
        if (mChatService == null)
            setupChat();
        //sendSensorData();
//            accelerometerSensor.setRunInMainAc(context);
//            accelerometerSensor.sensorOn();

    }
}


    @Override
    public synchronized void onResume() {
        super.onResume();
        accelerometerSensor.setRunInMainAc();
        //accelerometerSensor.genTempTool(context,1);
        if (D)
            Log.e(TAG, "+ ON RESUME +");

        // 执行此检查onresume()涵盖的案件中，英国电信
        // 不可在onstart()，所以我们停下来让它⋯
        // onresume()将被调用时，action_request_enable活动返回。
        if (mChatService != null) {
            // 只有国家是state_none，我们知道，我们还没有开始
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // 启动蓝牙聊天服务
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        // 初始化撰写与听众的返回键
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        // 初始化发送按钮，单击事件侦听器
        mSendButton = (ImageButton) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 发送消息使用内容的文本编辑控件
                //TextView view = (TextView) findViewById(R.id.edit_text_out);
//                String message = "x:" + x + "   " + "y:" + y + "    " + "z:" + z + "    ";

//                String message = "send a message";
                String message = mOutEditText.getText().toString();
                    sendData(message);
            }
        });

        // 初始化bluetoothchatservice执行蓝牙连接
        mChatService = new BluetoothChatService(this, mHandler);
        // 缓冲区初始化传出消息
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 蓝牙聊天服务站
        if (mChatService != null)
            mChatService.stop();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D)
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public  void sendData(String message) {
        // 检查我们实际上在任何连接
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "not_connect", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // 检查实际上有东西寄到
        if (message.length() > 0) {
            // 得到消息字节和告诉bluetoothchatservice写
            byte[] send = message.getBytes();
            mChatService.write(send);

        }

    }

    // 处理程序，获取信息的bluetoothchatservice回来
    private final Handler mHandler = new Handler() {
        String sendToPC;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_GES_TYPE:
//                    type = (int)msg.obj;
                    Bundle bundle = (Bundle) msg.obj;
                    String str = bundle.getString("ges_name");
                    int i = bundle.getInt("ges_id");
                    type = i;
                    str = str+"---------->"+getType();
                    output.setText(str);
                    sendToPC = mSwitch(getType());
                    masterStop = true;
                    if (mSC == modeSC.CLIENT){
                        sendData(sendToPC);// 双手模式下为发送到主机
                    }else {
                        if(dataArrive){
//                            if ((getType()*10+msgFromSlave)==34 || (getType()*10+msgFromSlave)==43){
//                                sendToPC = "05";
//                            }
                            switch (getType()*10+msgFromSlave){
                                case 11:
                                    sendToPC = "06";
                                    break;
                                case 22:
                                    sendToPC = "05";
                                    break;
                                case 33://getType()=9,msgFromSlave=10
                                    sendToPC = "07";
                                    break;
                                case 44:
                                    sendToPC = "25";
                                    break;
                                default:
                                    sendToPC = "00";
                            }
//                            sendData(getType() + "+" + msgFromSlave);
                            sendData(sendToPC);
                            msgFromSlave = 0;
                            dataArrive = false;
                            masterStop = false;
                        }
                    }

                    break;
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                            mTitle.setText("title_connected_to");
//                            mTitle.append(mConnectedDeviceName);
                            mInputEditText.setText("");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            mTitle.setText("title_connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                            mTitle.setText("title_not_connected");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // 构建一个字符串缓冲区
                    String writeMessage = new String(writeBuf);
                    sum=1;
                    UTF=1;
                    mmsg += writeMessage;
                    mInputEditText.getText().append("\n<--"+writeMessage+"\n");
                    fmsg+="\n<--"+writeMessage+"\n";
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    dataArrive = true;
                    // 构建一个字符串从有效字节的缓冲区
                    if (sum==1) {
                        mInputEditText.getText().append(Html.fromHtml("<font color=\"blue\">"+"\n-->\n"+"</font>"));
                        fmsg+="\n-->\n";
                        sum++;
                    }else {
                        sum++;
                    }
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    try{
                        msgFromSlave = Integer.parseInt(readMessage);
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }

                        mInputEditText.getText().append(Html.fromHtml("<font color=\"blue\">"+readMessage+"</font>"));
                        fmsg+=Html.fromHtml("<font color=\"blue\">"+readMessage+"</font>");
                    if(mSC == modeSC.SERVER)
                    {
                        if(masterStop)//means that master has finish the ges
                        {
                            switch (getType()*10+msgFromSlave){
                                case 11:
                                    sendToPC = "06";
                                    break;
                                case 22:
                                    sendToPC = "05";
                                    break;
                                case 33://getType()=9,msgFromSlave=10
                                    sendToPC = "07";
                                    break;
                                case 44:
                                    sendToPC = "25";
                                    break;
                                default:
                                    sendToPC = "00";
                            }

                            sendData(sendToPC);
                            msgFromSlave = 0;
                            masterStop = false;
                            dataArrive = false;
                        }
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // 保存该连接装置的名字
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "已连接 " + mConnectedDeviceName, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    public String changeCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            // 用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            // 用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }

    /**
     * 将字符编码转换成UTF-8码
     */
    public String toUTF_8(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, "UTF_8");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // 当devicelistactivity返回连接装置
                if (resultCode == Activity.RESULT_OK) {
                    // 获得设备地址
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // 把蓝牙设备对象
                    BluetoothDevice device = mBluetoothAdapter
                            .getRemoteDevice(address);
                    // 试图连接到装置
                    mChatService.connect(device);

                }
                break;
            case REQUEST_ENABLE_BT:
                // 当请求启用蓝牙返回
                if (resultCode == Activity.RESULT_OK) {
                    // 蓝牙已启用，所以建立一个聊天会话
                    setupChat();
                    //sendSensorData();
                    accelerometerSensor.setRunInMainAc();
//                    accelerometerSensor.genTempTool(context,1);
//                    accelerometerSensor.sensorOn();

                } else {
                    // 用户未启用蓝牙或发生错误
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "bt_not_enabled_leaving",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                mSC = modeSC.SERVER;
                Toast.makeText(MainActivity.this, "当前为主机模式", Toast.LENGTH_SHORT).show();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Intent serverIntent = new Intent(MainActivity.this,DeviceListActivity.class);
//                startActivity(intent);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStop(){
        super.onStop();
        accelerometerSensor.sensorOff();
    }

    public enum modeSC{
        CLIENT,
        SERVER
    }
    public enum modeSD{
        SINGLE,
        DOUBLE
    }
    public enum modeUSER{
        GENERAL,
        PICTURE,
        PPT
    }


    public int getType(){
         return type;
     }

    public String mSwitch(int gesId){
        String s = "00";
            if (mSD == modeSD.SINGLE){
                 switch (mUSER){
                     case GENERAL:
                         switch (gesId){
                             case 1:
                                 s = "01";
                                 break;
                             case 2:
                                 s = "02";
                                 break;
                             case 3:
                                 s = "03";
                                 break;
                             case 4:
                                 s = "04";
                                 break;
                             case 5:
                                 s = "08";
                                 break;
                             case 6:
                                 s = "09";
                                 break;
//                             case 7:
//                                 s = "07";
//                                 break;
//                             case 8:
//                                 s = "08";
//                                 break;
                             default:
                                 break;
                         }
                         break;
                     case PPT:
                         switch (gesId){
                             case 3:
                                 s = "11";
                                 break;
                             case 4:
                                 s = "12";
                                 break;
                             case 5:
                                 s = "13";
                                 break;
                             case 6:
                                 s = "14";
                                 break;
                             default:
                                 break;
                         }
                         break;
                     case PICTURE:
                         switch (gesId){
                             case 3:
                                 s = "21";
                                 break;
                             case 4:
                                 s = "22";
                                 break;
                             case 5:
                                 s = "23";
                                 break;
                             case 6:
                                 s = "24";
                                 break;
                             case 7:
                                 s = "25";
                                 break;
                             default:
                                 break;
                         }
                         break;
                     default:
                         break;
                 }
            }else {
                s = gesId + "";
            }
        return s;
    }


}
