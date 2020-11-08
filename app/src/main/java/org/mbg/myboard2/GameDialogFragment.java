package org.mbg.myboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GameDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG_EVENT_DIALOG="dialog_event";
    static int i=-1;
    String str_game_name="";

    public GameDialogFragment() {}
    public static GameDialogFragment getInstance(int cafe_i){
        GameDialogFragment e= new GameDialogFragment();
        i=cafe_i;
        return e;
    }

    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        View v= inflater.inflate(R.layout.dialog_game, container);
        EditText game_name= (EditText) v.findViewById(R.id.gameName);
        Button input_button= (Button)v.findViewById(R.id.inputButton);
        input_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_game_name= game_name.getText().toString();
                input_game_name();
                dismiss();
            }
        });





        //game_name.setNav

        return v;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    public void input_game_name(){
        //게임 수
        if(!str_game_name.equals("")) {
            if(!str_game_name.trim().equals("")) {
                MapView.cafe_map.get(i).input_game_name=str_game_name;
            }
        }


    }
}