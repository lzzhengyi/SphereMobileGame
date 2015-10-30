package SphereGameDemo;

import java.io.Serializable;
import java.util.Random;

public class DDice implements Serializable {

	public final static int MAX_D_LEVEL = 25;
	
	public int number;
	public int value;
	public int constant;
	private static Random rand;
	
	//number of dice, value per dice, constant added to rolled value, rng
	public DDice (int n, int v, int c, Random r){
		number = n;
		value = v;
		constant = c;
		if (rand ==null){
			rand = r;
		}
	}
	public DDice (int n, int v, int c){
		number = n;
		value = v;
		constant = c;
		if (rand ==null){
			rand = new Random();
		}
	}
	public DDice (int ref, Random r){
		if (rand ==null){
			rand = r;
		}
		number=1;
		int dn = rand.nextInt(10000);
		if (dn-ref/2<6000){
			number++;
		}
		if (dn-ref/2<1020){
			number++;
		}
		if (dn-ref/2<120){
			number++;
		}
		if (dn-ref/2<=-1000){
			number++;
		}
		if (dn-ref/2<=-10000){
			number++;
		}
		int tval = 0;
		value = 2;
		int bonus = 0;
		if (ref>10000 && rand.nextBoolean()){
			bonus = 10;
		}
		for (int i=0;i<4;i++){
			tval = Math.max(2,rand.nextInt(Math.min(MAX_D_LEVEL+bonus, Math.max(ref/20, 6))));
			if (tval>value){
				value = tval;
			}
		}
//		value =Math.max(2,rand.nextInt(Math.min(MAX_D_LEVEL, Math.max(ref/20, 6))));
		constant = rand.nextInt(Math.min((MAX_D_LEVEL+bonus)/2, Math.max(ref/20, 1)));
	}
	public String toString () {
		if (constant>0){
			return number+"d"+value+" + "+constant;
		}
		return number+"d"+value;
	}
	public int roll (){
		int sum=constant;
		for (int i=0;i<number;i++){
			sum = sum + rand.nextInt(value)+1;
		}
		return sum;
	}
	public int calculateSPValue () {
		return number*value+constant;
	}
	public int calculateRollCost () {
		return number*(value/2)+constant;
	}
}
