package com.example.wuyukai.ndkdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GesListActivity extends AppCompatActivity {
    private int userId;
    private String userName;
    private SimpleAdapter arrayAdapter;
//    public final static String EXTRA_MESSAGE_POSITION = "GesDataActivity.messgae";
//    public final static String EXTRA_MESSSAGE_GESNAME = "GesDataActivity.messgae";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ges_list);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",0);
        userName = intent.getStringExtra("userName");

        //调用SQLiteTool工具类进行数据操作
        SQLiteDatabase db = new SQLiteTool(GesListActivity.this, "sunny.db").getReadableDatabase();
        //从数据库获取数据出来
        Cursor c = db.rawQuery("select distinct ges_id,ges_name from user2 where user_id="+userId+" order by _id asc",new String[] {});
        final List<Map<String, String>> ges_list = new ArrayList<Map<String,String>>();
        if(c!=null){
            while(c.moveToNext()){
                Map<String, String> userMap = new HashMap<String, String>();
                userMap.put("gesId",c.getString(c.getColumnIndex("ges_id")));
                userMap.put("gesName", c.getString(c.getColumnIndex("ges_name")));
                ges_list.add(userMap);
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
        arrayAdapter = new SimpleAdapter(GesListActivity.this, ges_list, R.layout.simple_list_item_2,
                new String[] {"gesId","gesName"},
                new int[]{R.id.gesId,R.id.gesName});
        //通过listview把数据库获取出来的数据进行遍历
        ListView gesDataLv = (ListView) findViewById(R.id.gesDataLv);
        final Button addItemBtn = (Button) findViewById(R.id.addItemBtn);
        gesDataLv.setAdapter(arrayAdapter);
        c.close();
        //关闭数据库连接释放资源
        db.close();

        // Get reference of widgets from XML layout
        //final ListView gesDataLv = (ListView) findViewById(R.id.gesDataLv);
//        final Button addItemBtn = (Button) findViewById(R.id.addItemBtn);

        // Initializing a new String Array
//        String[] gestures = new String[] {
//                "Cape Gooseberry",
//                "Capuli cherry"
//        };

        // Create a List from String Array elements
//        final List<String> ges_list = new ArrayList<>(Arrays.asList(gestures));

        // Create an ArrayAdapter from List
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
//                (this, android.R.layout.simple_list_item_1, ges_list);

        // DataBind ListView with items from ArrayAdapter
//        gesDataLv.setAdapter(arrayAdapter);
        gesDataLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView mTextView = (TextView)view.findViewById(R.id.gesName);
                String gesName = mTextView.getText().toString();
//                TextView tv = (TextView) view.findViewById(R.id.text);
                //Toast.makeText(GesDataActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GesListActivity.this,GesCollDataActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("userName",userName);
                intent.putExtra("gesId",position + 1);
                intent.putExtra("gesName",gesName);
                startActivity(intent);
            }
        });
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取自定义AlertDialog布局文件的view
                LinearLayout change_name = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.user_rename_dialog, null);
                //TextView tv_name_dialog = (TextView) change_name.findViewById(R.id.tv_name_dialog);
                //由于EditText要在内部类中对其进行操作，所以要加上final
                final EditText et_name_dialog = (EditText) change_name.findViewById(R.id.et_username_dialog);
                final TextView dialogLabel = (TextView) change_name.findViewById(R.id.dialogLabel);
                dialogLabel.setText("新手势名:");
                //设置AlertDialog中TextView和EditText显示Activity中TextView的内容
                //tv_name_dialog.setText(old_name.getText().toString());
                //et_name_dialog.setText(old_name.getText().toString());
                new AlertDialog.Builder(GesListActivity.this)
                        .setTitle("添加手势")
                        .setView(change_name)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //将Activity中的textview显示AlertDialog中EditText中的内容
                                //并且用Toast显示一下
                                // Add new Items to List
//                                ges_list.add(et_name_dialog.getText().toString());
                                Map<String, String> userMap = new HashMap<String, String>();
                                userMap.put("gesName", et_name_dialog.getText().toString());
                                ges_list.add(userMap);
                                /*
                                notifyDataSetChanged ()
                                Notifies the attached observers that the underlying
                                data has been changed and any View reflecting the
                                data set should refresh itself.
                                */
                                arrayAdapter.notifyDataSetChanged();
                                Toast.makeText(GesListActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        //由于“取消”的button我们没有设置点击效果，直接设为null就可以了
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            }
        });
    }

}
