package com.example.wuyukai.ndkdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EDTW {
	List<String> gesNameList;
	int N_eachType = 5;
	private int[][] file_X;
	private int[][] file_Z;
	private int gesIdMax;
	public int edtwProcessPro(int data_x[],int data_z[],MEMS mems){
		int index = 0;
		float s=0;
		int N_size = 30;
		float distance = 0;
		float weigth = mems.Weigth();
		float DistDTW[][] = new float[N_size][N_size];
		float d;
		float DTWaverage=0;
		float DTW_min=1000000;//随意设置超大的值

		for (int k=0; k<gesIdMax; k++) {//k代表手势种类
			for (int p=0; p<N_eachType; p++) {//p代表本类手势的第几个样本

				for (int i=0; i<N_size; i++) {//i代表每个模板里第几个元素
					for (int j=0; j<N_size; j++) {//j代表测试数据里第几个元素
						distance=(float) Math.sqrt(weigth*Math.pow((data_x[j]-file_X[p+k*N_eachType][i]),2)+(1-weigth)*Math.pow((data_z[j]-file_Z[p+k*N_eachType][i]),2));//计算欧式距离，涉及两个轴权重问题
						if (j==0&&i==0) {
							DistDTW[i][j]=distance;//如果DistDTW[0][0],直接用欧式距离
						}else if (j>0)
						{
							DistDTW[i][j]=DistDTW[i][j-1]+distance;//考虑其他累计距离
						}
						if (i>0) {
							if (j==0) {
								DistDTW[i][j]=distance+DistDTW[i-1][j];//当i>0时第一列的值只能是欧式距离加上累计距离
							}else{
								d=Math.min(DistDTW[i][j-1], Math.min(DistDTW[i-1][j], DistDTW[i-1][j-1]));
								DistDTW[i][j]=d+distance;
							}
						}
						s=s+DistDTW[i][j];
					}
				}

				DTWaverage=(float) (s/Math.pow(N_size, 2));//计算平均阈值
				s=0;
				if (DTW_min>DTWaverage) {    //求最小匹配距离
					DTW_min=DTWaverage;
					index=k+1;
				}
			}
		}
		return index;
	}
	public boolean genTemplate(Context context,int USER_ID){
		//调用SQLiteTool工具类进行数据操作
		SQLiteDatabase db = new SQLiteTool(context, "sunny.db").getReadableDatabase();
		//从数据库获取数据出来
		Cursor c = db.rawQuery("select ges_arrayX,ges_arrayZ from user2 where user_id="+USER_ID+" order by ges_id asc",new String[] {});
		Cursor c2 = db.rawQuery("select distinct ges_id,ges_name from user2 where user_id="+USER_ID+" order by ges_id asc",new String[] {});
		if (c2.getCount() != 0){
			gesNameList = new ArrayList<>();
			while (c2.moveToNext()){
				gesNameList.add(c2.getString(c2.getColumnIndex("ges_name")));
			}
			c2.moveToLast();
			Log.i("gesIdMax",gesIdMax+"");
			gesIdMax = c2.getInt(c2.getColumnIndex("ges_id"));
			file_X = new int[gesIdMax*N_eachType][30];
			file_Z = new int[gesIdMax*N_eachType][30];
			int i = 0;
			while(c.moveToNext()){
					file_X[i] = ArrToStrUtils.explodedArray(c.getString(c.getColumnIndex("ges_arrayX")));
					file_Z[i] = ArrToStrUtils.explodedArray(c.getString(c.getColumnIndex("ges_arrayZ")));
					i++;
				}
			c.close();
			//关闭数据库连接释放资源
			db.close();
			return true;
		}else {
			c.close();
			//关闭数据库连接释放资源
			db.close();
			return false;
		}
	}
	public String getGesName(int index){
		String s;
		//索引和ID位置不同-1
		s= gesNameList.get(index-1);
		return s;
	}
}
