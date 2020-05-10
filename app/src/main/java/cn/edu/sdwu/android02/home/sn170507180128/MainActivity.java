package cn.edu.sdwu.android02.home.sn170507180128;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_homework);

        //获取按钮
        Button button=(Button) findViewById(R.id.homework_btn1);
        Button button2=(Button) findViewById(R.id.homework_btn2);
        Button button3=(Button) findViewById(R.id.homework_btn3);



    }


    //作业1 跳转
    public void startmain(View view){
        //界面跳转
        Intent intent=new Intent(this,homeworkActivity1.class);
        startActivity(intent);
    }
    //作业2 跳转
    public void startmain2(View view){
        //界面跳转
        Intent intent=new Intent(this,homeworkActivity2.class);
        startActivity(intent);
    }

}
