package com.dating.jd_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.dating.jd_common.TestParcelable;

@Route(path = "/test/Test2Activity")
public class Test2Activity extends AppCompatActivity {

    @Autowired()
    String name ;

    @Autowired(name = "age")
    int age ;

    @Autowired
    TestParcelable test;

//    @BindView(R.id.tv_text2)
    TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        ARouter.getInstance().inject(this);

        tvTest = findViewById(R.id.tv_text2);
        tvTest.setText("name:" + name +",age:" + age  + ",test:"+test);

    }

}