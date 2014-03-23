package ServerSide;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class UserOutputStream extends DataOutputStream{
	private boolean[] numbers;
	public UserOutputStream(OutputStream out) {
		super(out);
		numbers=new boolean[255];
	}
	public int getNum(){
		for(int i=0;i<255;++i){
			if(!numbers[i]){
				numbers[i]=true;
				return i;		
			}
		}
		return -1;
	}
	public void freeNum(int n){
		numbers[n]=false;
	}

}
