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

public class UserListActivity extends AppCompatActivity {
    private SimpleAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

//        // Get reference of widgets from XML layout
//        final ListView lv = (ListView) findViewById(R.id.lv);
//        final Button btn = (Button) findViewById(R.id.btn);
//
//        // Initializing a new String Array
//        String[] fruits = new String[] {
//                "Cape Gooseberry",
//                "Capuli cherry"
//        };
//
//        // Create a List from String Array elements
//        final List<String> fruits_list = new ArrayList<>(Arrays.asList(fruits));
//
//        // Create an ArrayAdapter from List
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
//                (this, android.R.layout.simple_list_item_1, fruits_list);
//
//        // DataBind ListView with items from ArrayAdapter
//        lv.setAdapter(arrayAdapter);
        //调用SQLiteTool工具类进行数据操作
        SQLiteDatabase db = new SQLiteTool(UserListActivity.this, "sunny.db").getReadableDatabase();
        //从数据库获取数据出来
        Cursor c = db.rawQuery("select distinct user_id,user_name from user2 order by _id asc",new String[] {});
        final List<Map<String, String>> user_list = new ArrayList<Map<String,String>>();
        if(c!=null){
            while(c.moveToNext()){
                Map<String, String> userMap = new HashMap<String, String>();
                userMap.put("userId",c.getString(c.getColumnIndex("user_id")));
                userMap.put("userName", c.getString(c.getColumnIndex("user_name")));
                user_list.add(userMap);
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
        arrayAdapter = new SimpleAdapter(UserListActivity.this, user_list, R.layout.simple_list_item_1,
                new String[] {"userId","userName"},
                new int[]{R.id.userId,R.id.userName});
        //通过listview把数据库获取出来的数据进行遍历
        ListView lv = (ListView) findViewById(R.id.lv);
        final Button btn = (Button) findViewById(R.id.btn);

//        final Button addItemBtn = (Button) findViewById(R.id.addItemBtn);

        lv.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        c.close();
        //关闭数据库连接释放资源
        db.close();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView mTextView = (TextView)view.findViewById(R.id.userName);
//                TextView mTextView = (TextView)view;
                String userName = mTextView.getText().toString();
//                TextView mTextView = (TextView)view;
//                TextView tv = (TextView) view.findViewById(R.id.text);
//                mTextView.setText("hello");
//                Toast.makeText(MainActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserListActivity.this,GesListActivity.class);
                intent.putExtra("userId",position + 1);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取自定义AlertDialog布局文件的view
                LinearLayout change_name = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.user_rename_dialog, null);
                //TextView tv_name_dialog = (TextView) change_name.findViewById(R.id.tv_name_dialog);
                //由于EditText要在内部类中对其进行操作，所以要加上final
                final EditText et_name_dialog = (EditText) change_name.findViewById(R.id.et_username_dialog);
                final TextView dialogLabel = (TextView) change_name.findViewById(R.id.dialogLabel);
                dialogLabel.setText("新用户名:");
                //设置AlertDialog中TextView和EditText显示Activity中TextView的内容
                //tv_name_dialog.setText(old_name.getText().toString());
                //et_name_dialog.setText(old_name.getText().toString());
                new AlertDialog.Builder(UserListActivity.this)
                        .setTitle("添加用户")
                        .setView(change_name)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //将Activity中的textview显示AlertDialog中EditText中的内容
                                //并且用Toast显示一下
                                // Add new Items to List
                                Map<String, String> userMap = new HashMap<>();
                                userMap.put("userName", et_name_dialog.getText().toString());
                                user_list.add(userMap);
//                                user_list.add(et_name_dialog.getText().toString());
                                /*
                                notifyDataSetChanged ()
                                Notifies the attached observers that the underlying
                                data has been changed and any View reflecting the
                                data set should refresh itself.
                                */
                                arrayAdapter.notifyDataSetChanged();
                                Toast.makeText(UserListActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();

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
