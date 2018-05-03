package union_program;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import lejos.utility.Stopwatch;
import lejos.utility.TextMenu;

public class UNION_program {

	
	static RegulatedMotor testMotor  = Motor.B;
	static RegulatedMotor testMotor2  = Motor.C;
	
	static EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(SensorPort.S4);
	static RegulatedMotor leftMotor  = Motor.C;
    static RegulatedMotor rightMotor = Motor.B;
    
    static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
    
	public static void main(String[] args) {
		
		String ports[] = {"go straight","line trace"};
        TextMenu portMenu = new TextMenu(ports, 1, "SELECT");
   
        int No = portMenu.select();
        if (No < 0) return;
        
        int j =0;
        
        LCD.clear();
        
            while (!Button.ESCAPE.isDown())
            {
               if (No == 0){
               	go_straight(); //一定距離直進を行う
               	j++;
               }
               
               if (No == 1){
            	   linetrace(); //ライントレースを行う
            	   j++;
               }               
     
               if(j == 1){
                   break;  //go_straightか、linetarce のどちらかを一回行ったら終了。             
              }
            }
          
	}

	
	
	
	
	
public static void go_straight(){
		
		
		
		Stopwatch stopwatch = new Stopwatch(); 
		
		
		 stopwatch.reset(); 
		 
		 
		 
		 String ports[] = {"0.0m", "0.5m", "1.0m", "1.5m","2.0m", "2.5m","3.0m", "3.5m","4.0m","4.5m","5.0m"};
		 TextMenu portMenu = new TextMenu(ports, 1, "SELECT");
		 
		 int distance = portMenu.select();
         if (distance < 0) return;
         
         LCD.clear();
		 
        testMotor.setSpeed(300);
		testMotor2.setSpeed(300);
		
		testMotor.resetTachoCount();
		testMotor.rotateTo(0);
		testMotor2.resetTachoCount();
		testMotor2.rotateTo(0);
		
		testMotor.rotate(1035*distance, true);
		testMotor2.rotate(1035*distance, false);
		//1035°回転を　distance回、行う
	
		
		double value1; //タコカウントの値
		double value2; //ストップウォッチの値(ミリ秒)
		double value3; //平均速度のの値(㎝/秒)
		double value5 = 0; //走行距離の値(ミリ)
		double value_s =0; //ストップウォッチの値(秒)

		value1 = testMotor.getTachoCount();
		value2 = stopwatch.elapsed();
		value3 =((value1*174/360)/10)/(value2/1000/*秒に変換*/);
		//速度（㎝/秒）
		value_s = value2/1000;
		value5 = (value1/360)*174/10;
		
		String value1_0 = String.format("%.1f", value1); //小数点の調整
		String value_s_0 = String.format("%.1f", value_s); //小数点の調整
		String value3_0 = String.format("%.1f", value3); //小数点の調整
		String value5_0 = String.format("%.1f", value5); //小数点の調整
		
		LCD.drawString("tacho  " + value1_0 + "°",0,0);	
		LCD.drawString("time  " + value_s_0 + "s",0,2);
		
		
		LCD.drawString("ave_speed " + value3_0 + "cm/s",0,4);
		
		LCD.drawString("distance  " + value5_0 + "cm",0,7);
		
		
		Delay.msDelay(60000);
		
	}
	

public static void linetrace(){
	Stopwatch stopwatch = new Stopwatch(); 
	Stopwatch stopwatch2 = new Stopwatch(); 
		
		 stopwatch.reset(); 
		 stopwatch2.reset();
		 
		int i =0;
		int j =0;
		
		    double value1;
			double value2;
			double value3;
			double value5 = 0;
			double value_s =0;
			double value_t = 0;
			double value_ms = 0;
			double value_mt = 0;
			double value_a = 0;
		
	SensorMode color = colorSensor.getMode(1);
	float value_c[] = new float[color.sampleSize()];
	
	SensorMode sonic = sonicSensor.getMode(0);
	float value_d[] = new float[sonic.sampleSize()];
	//value2:障害物との距離
	
	value1 = leftMotor.getTachoCount();
	value2 = stopwatch.elapsed(); /*ミリ秒*/
	
	motor_init();
	
    while (!Button.ESCAPE.isDown()) {
    	value_mt = stopwatch2.elapsed(); /*ミリ秒*/
    	value_t = leftMotor.getTachoCount();
    
    	LCD.drawString("max_speed  " + value_a,0,6);
    
    	j++;
    	
    	if(j == 50 ){
        	
    		
        	
        	
        	value_ms =((value_t*174/360)/10)/(value_mt/1000/*秒に変換*/);
        	//速度（㎝/秒）
        	
        	if(value_a <= value_ms){
    			value_a = value_ms; //前の速度より、大きければ代入する。
    		}
    
        	
    		stopwatch2.reset();
    		leftMotor.resetTachoCount();
    		j=0;
    	}
    	
    	
    	
    	
    	if(i > 1000 ){
    		Sound.buzz();
    		break;
    	}
    	
    	
    	
    	

    
    	
    	
    	color.fetchSample(value_c, 0);
		for ( int k = 0; k < color.sampleSize(); k++ ) {
			LCD.drawString("val[" + k + "] : " + value_c[k] , 1, k+1);
		}
		
		LCD.drawString(sonic.getName(), 1, 0);
		sonic.fetchSample(value_d, 0);
		for ( int k = 0; k < sonic.sampleSize(); k++ ) {
			
			LCD.drawString("val[" + k + "] : " + value_d[k] + " m", 1, k+1);
		
		 if(  value_d[0]  < 0.10 ){
			    
            	motor_set(0,0);
            	
            	Sound.buzz();
            	
            	
            	motor_set(200,200);
            	
        		leftMotor.rotate(180, true);
        		rightMotor.rotate(-180, false);
        		//右に90°回転
        		
        		leftMotor.rotate(540, true);
        		rightMotor.rotate(540, false);
        		//前進(540)
        		
        		leftMotor.rotate(-180, true);
        		rightMotor.rotate(180, false);
        		//左に90°回転
        		leftMotor.rotate(720, true);
        		rightMotor.rotate(720, false);
        		//前進(720)
        		
        		leftMotor.rotate(-180, true);
        		rightMotor.rotate(180, false);
        		//左に90°回転
        		
        		leftMotor.rotate(540, true);
        		rightMotor.rotate(540, false);
        		//前進(540)
        		
        		leftMotor.rotate(180, true);
        		rightMotor.rotate(-180, false);
        		//右に90°回転        		
        
		 }
		if ( value_c[0] >= 0.0 && value_c[0] <= 0.1 ) {
			//黒検知、右回転
            motor_set(50,-50);
            i = 0;
        } else if( value_c[0] > 0.1 && value_c[0] <= 0.23 ){
            //黒大、白少：右スピン前進
        	motor_set(100,30);
        	i = 0;
        }else if( value_c[0] > 0.23 && value_c[0] <= 0.33 ){
        	//黒白の中間検知：前進
        	motor_set(100,100);
        	i = 0;
        	 }else if( value_c[0] > 0.33 && value_c[0] <= 0.58 ){
        	//黒少、白大：左スピン前進
        	motor_set(30,100);
        	i = 0;
        }else if( value_c[0] > 0.58 && value_c[0] <= 1.0 ){
        	//白検知、左回転
        	motor_set(-50,50);
        	i++;
        }
		
		

		
		
		}
    }
    LCD.clear();
    motor_set(0,0);
    
    value1 = rightMotor.getTachoCount();
	value2 = stopwatch.elapsed(); /*ミリ秒*/
	value5 = (value1/360)*174/10;
	value3 = value5 /(value2/1000/*秒に変換*/);
	//速度（㎝/秒）
	value_s = value2/1000;
	
	String value1_1 = String.format("%.1f", value1); //小数点の調整
	String value_s_1 = String.format("%.1f", value_s); //小数点の調整
	String value3_1 = String.format("%.1f", value3); //小数点の調整
	String value_a_1 = String.format("%.1f", value_a); //小数点の調整
	String value5_1 = String.format("%.1f", value5); //小数点の調整
	
	LCD.drawString("tacho  " + value1_1 + "°",0,0);	
	LCD.drawString("time  " + value_s_1 + "s",0,2);
	
	
	LCD.drawString("ave_speed " + value3_1 + "cm/s",0,4);
	LCD.drawString("max_speed " + value_a_1 + "cm/s",0,5);
	

	
	LCD.drawString("distance  " + value5_1 + "cm",0,7);
	
	
	Delay.msDelay(60000);
	
    
    
	}

private static void motor_init() {
    leftMotor.resetTachoCount();
    rightMotor.resetTachoCount();
    leftMotor.rotateTo(0);
    rightMotor.rotateTo(0);
}

private static void motor_set(int l_motor_pow, int r_motor_pow) {
    leftMotor.setSpeed(l_motor_pow);    
    rightMotor.setSpeed(r_motor_pow);
    
    if ( l_motor_pow > 0 ) {
        leftMotor.forward();
    } else if ( l_motor_pow < 0 ) {
        leftMotor.backward();
    } else {
        leftMotor.stop();
    }
    if ( r_motor_pow > 0 ) {
        rightMotor.forward();
    } else if ( r_motor_pow < 0 ) {
        rightMotor.backward();
    } else {
        rightMotor.stop();
    }
}



	
}
