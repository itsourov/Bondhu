package net.sourov.bondhu;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveState {
    Context context;
    SharedPreferences sharedPreferences;

    public SaveState(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences("preferance",Context.MODE_PRIVATE);
    }

    public void setState(int bvalue){
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putInt("bkey",bvalue);
        editor.apply();
    }
    public int getState(){
        return sharedPreferences.getInt("bkey",0);
    }
}
