package com.example.wuyukai.ndkdemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class GesCollDataActivity extends AppCompatActivity {
    private Button collectData1;
    private Button collectData2;
    private Button collectData3;
    private Button collectData4;
    private Button collectData5;
    private Button saveAndBack;
    private Button select;
    private int selectItem = 0;
    private int userId;
    private String userName;
    private int gesId;
    private String gesName;
    private AccelerometerSensor accelerometerSensor;

    private MEMS mems2 = new MEMS();
    private int[] recvArrayX;
    private int[] recvArrayZ;
    Context context = GesCollDataActivity.this;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            ArrayList<int []> recvList;
            recvList = (ArrayList<int[]>) msg.obj;
            recvArrayX = recvList.get(0);
            recvArrayZ = recvList.get(1);
            String  gesArrayX = ArrToStrUtils.implodeArray(recvArrayX,",");
            String  gesArrayZ = ArrToStrUtils.implodeArray(recvArrayZ,",");
            //调用SQLiteTool工具类进行数据操作
            SQLiteDatabase db = new SQLiteTool(GesCollDataActivity.this, "sunny.db").getWritableDatabase();
            //创建数据存储对象
            ContentValues values = new ContentValues();
            //往数据存储对象添加需要写入到数据的数据
            //特别注意:这里的字符串KEY是在数据库TABLE中的COLUMN的值
            values.put("user_id", userId);
            values.put("user_name", userName);
            values.put("ges_id", gesId);
            values.put("ges_name", gesName);
            values.put("ges_arrayX", gesArrayX);
            values.put("ges_arrayZ", gesArrayZ);
            //执行写入数据库方法
            long insert = db.insert("user2", null, values);
            //判断是否写入成功
            if(insert>0){
//                Toast.makeText(GesCollDataActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                switch (selectItem){
                    case 1:
                        collectData1.setText(R.string.data_finish);
                        collectData1.setTextColor(Color.WHITE);
                        break;
                    case 2:
                        collectData2.setText(R.string.data_finish);
                        collectData2.setTextColor(Color.WHITE);
                        break;
                    case 3:
                        collectData3.setText(R.string.data_finish);
                        collectData3.setTextColor(Color.WHITE);
                        break;
                    case 4:
                        collectData4.setText(R.string.data_finish);
                        collectData4.setTextColor(Color.WHITE);
                        break;
                    case 5:
                        collectData5.setText(R.string.data_finish);
                        collectData5.setTextColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
            }else{
                Toast.makeText(GesCollDataActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
            }
            //关闭数据库连接释放资源
            db.close();
            accelerometerSensor.sensorOff();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ges_coll_data);

        collectData1  = (Button)findViewById(R.id.collectData1);
        collectData2  = (Button)findViewById(R.id.collectData2);
        collectData3  = (Button)findViewById(R.id.collectData3);
        collectData4  = (Button)findViewById(R.id.collectData4);
        collectData5  = (Button)findViewById(R.id.collectData5);
        select = (Button)findViewById(R.id.select);

        accelerometerSensor = new AccelerometerSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE),handler,mems2);
        Intent intent = getIntent();
        //gesId = intent.getIntExtra(GesDataActivity.EXTRA_MESSAGE,0);
        userId = intent.getIntExtra("userId",0);
        userName = intent.getStringExtra("userName");
        gesId = intent.getIntExtra("gesId",0);
        gesName = intent.getStringExtra("gesName");

        collectData1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometerSensor.setRunInGesAc();
                selectItem++;
                collectData1.setBackgroundColor(getResources().getColor(R.color.colorAftClick));
                collectData1.setEnabled(false);

            }
        });
        collectData2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometerSensor.setRunInGesAc();
                selectItem++;
                collectData2.setBackgroundColor(getResources().getColor(R.color.colorAftClick));
                collectData2.setEnabled(false);

            }
        });

        collectData3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometerSensor.setRunInGesAc();
                selectItem++;
                collectData3.setBackgroundColor(getResources().getColor(R.color.colorAftClick));
                collectData3.setEnabled(false);
            }
        });

        collectData4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometerSensor.setRunInGesAc();
                selectItem++;
                collectData4.setBackgroundColor(getResources().getColor(R.color.colorAftClick));
                collectData4.setEnabled(false);

            }
        });

        collectData5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometerSensor.setRunInGesAc();
                selectItem++;
                collectData5.setBackgroundColor(getResources().getColor(R.color.colorAftClick));
                collectData5.setEnabled(false);

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用SQLiteTool工具类进行数据操作
                SQLiteDatabase db = new SQLiteTool(GesCollDataActivity.this, "sunny.db").getReadableDatabase();
                //从数据库获取数据出来
                Cursor c = db.rawQuery("select ges_arrayX,ges_arrayZ from user2 order by _id desc",new String[] {});
                List<Map<String, String>> list = new ArrayList<>();
                if(c!=null){
                    while(c.moveToNext()){
                        Map<String, String> userMap = new HashMap<>();
//                        userMap.put("id", c.getString(c.getColumnIndex("_id")));
//                        userMap.put("userId", c.getString(c.getColumnIndex("user_id")));
//                        userMap.put("userName", c.getString(c.getColumnIndex("user_name")));
//                        userMap.put("gesId", c.getString(c.getColumnIndex("ges_id")));
//                        userMap.put("gesName", c.getString(c.getColumnIndex("ges_name")));
                        userMap.put("gesArrayX", c.getString(c.getColumnIndex("ges_arrayX")));
                        userMap.put("gesArrayZ", c.getString(c.getColumnIndex("ges_arrayZ")));
                        list.add(userMap);
                    }
                }
                //实例化SimpleAdapter对象
	    /*
	     * 参数1：上下文Context
	     * 参数2：存放数据的list数组，里面是map对象
	     * 参数3：需要遍历的list模版也就是list的xml文件
	     * 参数4：list里面map的key值
	     * 参数5：list模版xml文件对应的id
	     */
                SimpleAdapter adapter = new SimpleAdapter(GesCollDataActivity.this, list, R.layout.list,
                        new String[] {"id","userId","userName","gesId","gesName","gesArrayX","gesArrayZ"},
                        new int[]{R.id.id,R.id.user_id,R.id.user_name,R.id.ges_id,R.id.ges_name,R.id.ges_arrayX,R.id.ges_arrayZ});
                //通过listview把数据库获取出来的数据进行遍历
                ListView lv = (ListView) findViewById(R.id.lvSQL);
                lv.setAdapter(adapter);
                c.close();
                //关闭数据库连接释放资源
                db.close();
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(0<selectItem && selectItem < 5){
                new AlertDialog.Builder(GesCollDataActivity.this)
                    .setMessage("未完成数据采集,请勿退出!")
                    .setPositiveButton("继续采集", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("仍然退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //此处数据不应该保存,后期再做调整
                            accelerometerSensor.genTempTool(context,userId);
                            accelerometerSensor.sensorOff();
                            finish();
                        }
                    })
                    .create()
                    .show();
            }else if(selectItem==0){
                accelerometerSensor.sensorOff();
                finish();
            }else {
                accelerometerSensor.genTempTool(context,userId);
                accelerometerSensor.sensorOff();
                Toast.makeText(GesCollDataActivity.this,"数据已保存",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return false;
    }
}
