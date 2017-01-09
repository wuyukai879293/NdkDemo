package com.example.wuyukai.ndkdemo;

public class MEMS {
	float STOP_FLAG = 0;
	boolean isDataFinish = false;
	//float threshold[] = new float[200];
	float THRESHOLD = (float) 0.2;
	//int position = 0;
	int N1 = 500;
	int M = 3;
	int N = 5;
	int Size = 30;
	float a[][] = new float[N1][M];         //预留100个长度用来接收数据
	float tempData[][]=new float[N1][M];   //缓存数组
	float buffer[][] = new float[N][M];      //N行3列的缓冲数组，用于判别起始终止点
	float different[] = new float[N-1];        //存差分值
	float Resample_Data[][] = new float[Size][M];//重采样后的数组
	float Temp_x[]= new float[Size];        //二维转一维临时x数组
	float Temp_z[] = new float[Size];        //二维转一维临时y数组

	int flag_start=1;
	int flag_end=0;
	int count=0;
	int num=0;
	int num1=0;
	
	float value_x = 0,value_z = 0;
	
	public void Reg_Init(){
	    flag_start=1;
	    flag_end=0;
	    count=0;
	    num=0;
	    num1=0;
		isDataFinish = false;
	}

	public void Reg_Groud_Init(){
	    for (int i=0; i<N1; i++) {
	        for (int j=0; j<M; j++) {
	            a[i][j]=0;
	            tempData[i][j]=0;
	        }
	    }
	    for (int i=0; i<Size; i++) {
	        for (int j=0; j<M; j++) {
	            Resample_Data[i][j]=0;
	        }
	        Temp_x[i]=0;
	        Temp_z[i]=0;
	    }
	}

	public boolean isDataFinished(){
		return isDataFinish;
	}
	public void resetDataFinish(){
		isDataFinish = false;
	}
	public float max(float data[],int length){
		float ma=0;
	    for (int i=0;i<length ; i++) {
	        if (ma<data[i]) {
	            ma=data[i];
	        }
	    }
	    return ma;
	}
	
	public float min(float data[],int length){
		float mi=0;
	    for (int i=0;i<length ; i++) {
	        if (mi>data[i]) {
	            mi=data[i];
	        }
	    }
	    return mi;
	}
	
	public float Tranfrom(int index,int n){
	    return Resample_Data[index][n];
	}
	
	public float Weigth(){
		float s=0;
	    float s1=0;
	    float d_x=0;
	    float d_z=0;
	    float percent=0;
	    for (int i=1; a[i][0]!=STOP_FLAG; i++) {
	        d_x=Math.abs(a[i][0]-a[i-1][0]);//求X轴差分数据
	        d_z=Math.abs(a[i][2]-a[i-1][2]);//求Z轴差分数据
	        s=s+d_x;
	        s1=s1+d_z;
	    }
	    percent=s/(s+s1);//返回X轴占得百分比，则Z轴为1-percent
//	    NSLog(@"X轴累计差分：%f，Z轴的累计差分：%f",s,s1);
	    return percent;

	}
	
	public boolean Weigth_bigger(){
		float s=0;
	    float s1=0;
	    float d_x=0;
	    float d_z=0;
	    for (int i=1; a[i][0]!=STOP_FLAG; i++) {
	        d_x=Math.abs(a[i][0]-a[i-1][0]);//求X轴差分数据
	        d_z=Math.abs(a[i][2]-a[i-1][2]);//求Z轴差分数据
	        s=s+d_x;
	        s1=s1+d_z;
	    }
	    if (s>s1) {
	        return true;//保留x轴数据
	    }else{
	        return false;//保留z轴数据
	    }

	}
	
	public int Length(){
		int i;
		for (i=0; i<N1; i++) {
		    if (a[i][0]==STOP_FLAG) {
		            break;
		        }
		    }
		    return i;
	}
	
	public void LinearSmoothFilter(int Type,int length){

		for (int i=0; i<length; i++) {
		     if (i==0) {
		    tempData[i][Type]=(float) ((5*a[i][Type]+2*a[i+1][Type]-a[i+2][Type])/6.0);
		     }
		     else if (i==length-1){
		    tempData[i][Type]=(float) ((-a[i-2][Type]+2*a[i-1][Type]+5*a[i][Type])/6.0);
		       }
		     else
		    tempData[i][Type]=(float) ((a[i-1][Type]+a[i][Type]+a[i+1][Type])/3.0);
		   }

	}
	
	public void Resample(int Orignal_Length,int Resample_Length){
//		[MEMS LinearSmoothFilter:2 andwith:[MEMS Length]];//数据平滑函数得到的数据保存在tempData[N1][M]数组中
//	    [MEMS LinearSmoothFilter:0 andwith:[MEMS Length]];//数据平滑函数得到的数据
		LinearSmoothFilter(2,Length());
		LinearSmoothFilter(0,Length());
	    int Step_1;//采样步长1
	    int Step_2;//采样步长1
	    int Step_n,Step_m;//
	    int start,end;
	    int temp=0;
	    int temp1=0;
	    int temp2=0;
	    //判断原始数据与当前数据的长度，大于则进行下采样，小于进行插值
	    if (Orignal_Length>Resample_Length) {//采样
	        Step_1=Orignal_Length/Resample_Length;
	        Step_2=Orignal_Length/Resample_Length+1;
	        Step_m=Orignal_Length-Resample_Length*Step_1;//步长2的点数
	        Step_n=Resample_Length-Step_m;               //步长1的点数
	        if (Step_m<=Step_n) {                       //取数量少的点作为始末点把间距大的点留在两侧
	            start=Step_m/2;
	            end=Step_m-start;
	            for (int i=0; i<Resample_Length; i++) {//Step_m小，对应的是Step_2
	                if (i<start) {
	                    temp=i*Step_2;
	                    Resample_Data[i][0]=tempData[temp][0];//起始段采用Step_2步长
	                    Resample_Data[i][2]=tempData[temp][2];
	                }
	                else if (i>=start&&i<(start+Step_n)){
	                    temp1=temp+(i-start+1)*Step_1;
	                    Resample_Data[i][0]=tempData[temp1][0];//中间段采用Step_1步长
	                    Resample_Data[i][2]=tempData[temp1][2];
	                }
	                else{
	                    temp2=temp1+(i-start-Step_n+1)*Step_2;
	                    Resample_Data[i][0]=tempData[temp2][0];//结束段采用Step_2步长
	                    Resample_Data[i][2]=tempData[temp2][2];
	                }
	            }
	        }
	        else{
	            start=Step_n/2;
	            end=Step_n-start;
	            for (int i=0; i<Resample_Length; i++) {//Step_n小，对应的是Step_1
	                if (i<start) {
	                    temp=i*Step_2;
	                    Resample_Data[i][0]=tempData[temp][0];//起始段采用Step_1步长
	                    Resample_Data[i][2]=tempData[temp][2];
	                }
	                else if (i>=start&&i<(start+Step_m)){
	                    temp1=temp+(i-start+1)*Step_1;
	                    Resample_Data[i][0]=tempData[temp1][0];//中间段采用Step_2步长
	                    Resample_Data[i][2]=tempData[temp1][2];
	                }
	                else{
	                    temp2=temp1+(i-start-Step_m+1)*Step_2;
	                    Resample_Data[i][0]=tempData[temp2][0];//结束段采用Step_2步长
	                    Resample_Data[i][2]=tempData[temp2][2];
	                }
	            }
	        }
	        Orignal_Length=Resample_Length;
	    }
	    else if (Resample_Length>Orignal_Length){                               //插值
	        int d;
	        d=Resample_Length-Orignal_Length;
	        d=d/2;                           //前后进行插值
	        for (int i=0; i<Resample_Length; i++) {
	            if(i<d||i>(Orignal_Length+d)) {
	            Resample_Data[i][0] = (float) 0.00;
	            Resample_Data[i][2]= (float) 0.00;
	            }else{
	            Resample_Data[i][0]=tempData[i-d][0];
	            Resample_Data[i][2]=tempData[i-d][2];
	            }
	        }
	        Orignal_Length=Resample_Length;
	    }
	    else{
	        for (int i=0; i<Resample_Length; i++) {
	            Resample_Data[i][0]=tempData[i][0];
	            Resample_Data[i][2]=tempData[i][2];
	        }
	    }

	}
	
	public void Normalize(int max,int min,int length){
		float max_x,max_z;
	    float min_x,min_z;
	//二维转一维
	    for (int i=0; i<length; i++) {
	        Temp_x[i]=Resample_Data[i][0];
	        Temp_z[i]=Resample_Data[i][2];
	    }
	//判断最大值
	    max_x=max(Temp_x,length);
	    max_z=max(Temp_z,length);
	//判断最小值
	    min_x=min(Temp_x,length);
	    min_z=min(Temp_z,length);
	//归一化
	    for (int i=0; i<length; i++) {
	    Resample_Data[i][0]=(max-min)*(Resample_Data[i][0]-min_x)/(max_x-min_x)+min;
	    Resample_Data[i][2]=(max-min)*(Resample_Data[i][2]-min_z)/(max_z-min_z)+min;
	    }

	}
	public void Gesture_select(float x,float z,float Th1){
		//起点判别
	    if(flag_start==1)                  //flag_start==1表示正在起点判断
	    {    buffer[num][0]=x;
//	    	 buffer[num][0]=a[num][0];
	        //a[i][1]=accelerometerData.acceleration.y;//用不到y轴
	        buffer[num][2]=(float) (z+0.98);
//	    	 buffer[num][2]=(float) (a[num][2]+0.98);
	        if(num>0){
	            different[num-1]=Math.abs(buffer[num][0]-buffer[num-1][0])+Math.abs(buffer[num][2]-buffer[num-1][2]);
	            // NSLog(@"%f",different[num-1]);
	            //threshold[position] = different[num-1];
	            //position++;
	            if (different[num-1]>Th1) {//大于一个阈值，记录一次+0.015
	                count++;
	            }
	        }
	    num++;
	    }
	//终点判别
	    if (flag_end==1) {                //flag_end==1开始终点判断
	        buffer[num][0]=x;
//	    	buffer[num][0]=a[num][0];
	        //a[i][1]=accelerometerData.acceleration.y;//用不到y轴
	        buffer[num][2]=(float) (z+0.98);
//	    	buffer[num][2]=(float) (a[num][2]+0.98);
	        if(num>0){
	            different[num-1]=Math.abs(buffer[num][0]-buffer[num-1][0])+Math.abs(buffer[num][2]-buffer[num-1][2]);
	            // NSLog(@"%f",different[num-1]);
	            if (different[num-1]<Th1-0.05) {//小于一个阈值，记录一次
	                count++;
	            }
	        }
	        num++;
	    }

	}
	public void MEMS_Data(float x,float z){
		//Reg_Init();
		this.value_x = x;
		this.value_z = z;
		
		Gesture_select(value_x,value_z, THRESHOLD);
		if((num==N)&&(count==N-1)&&(flag_start ==1)){//循环N次后,且满足条件后开始接收数据
	        num=0;
	        count=0;
	        flag_start =0;               //停止起点判断
	        flag_end=1;                  //开始终点判断
	        for (int i=0; i<N; i++) {
	            a[i][0]=buffer[i][0];
	            a[i][2]=(float) (buffer[i][2]+0.98);   //保存之前缓存内的数据
	        }
	    }
	    else if((num==N)&&(count==N-1)&&(flag_end ==1)){//循环N次后,且满足条件后结束接收数据
	            num=0;
	            count=0;
	            flag_start =1;               //开始起点判断
	            flag_end=0;                  //结束终点判断
	        a[N+num1][0]=STOP_FLAG;
	        a[N+num1][2]=STOP_FLAG;//结束当前手势数据采集
			isDataFinish = true;
//	        NSLog(@"_____手势结束______");
//	        sender.enabled=YES;//启用按钮
//	        [self.MotionManager stopAccelerometerUpdates];//停止更新数据
	        
	        }

	    else if ((num==N)&&(count!=N-1)){//循环N次后,且不满足条件故清空计数
	        num=0;
	        count=0;
	    }
	   if((flag_start==0)&&(flag_end==1)) {//代表现在状态是在记录数据
	            
	       a[N+num1][0]=x;
	       a[N+num1][2]=(float) (z+0.98);
	       num1++;//往全局数组里保存数据
	        }
	   

	}
	
}
