package chenhong.com.qq.Activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import chenhong.com.qq.Global.Constant;
import chenhong.com.qq.R;
import chenhong.com.qq.View.MyLinearLayout;
import chenhong.com.qq.View.SlidingMenu;

public class MainActivity extends AppCompatActivity implements Constant {

    private ListView lv_menu;
    private ListView lv_main;
    private SlidingMenu slidingMenu;
    private ImageView iv_head;
    private MyLinearLayout  linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportActionBar().hide();//隐藏掉整个ActionBar，包括下面的Tabs
        initview();
        lv_menu.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @Override
            //重写方法将系统返回的view转化为自己的view并return
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        lv_main.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.NAMES){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=(convertView==null?super.getView(position, convertView, parent):convertView);
                //ViewHelper是jar包里的类帮助处理view
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);
                //以属性动画变大 ViewPropertyAnimator是jar包中的处理属性动画的类
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });
        slidingMenu.setOnDragStateChangeListener(new SlidingMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
            }
            @Override
            public void onClose() {
                ViewPropertyAnimator.animate(iv_head).translationXBy(15).setInterpolator(new CycleInterpolator(4)).setDuration(500).start();
            }
            @Override
            public void onDraging(float fraction) {
                ViewHelper.setAlpha(iv_head, 1 - fraction);
            }
        });
        linear.setSlidingMenu(slidingMenu);
    }




    private void initview() {
        linear = (MyLinearLayout) findViewById(R.id.my_layout);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        slidingMenu = (SlidingMenu) findViewById(R.id.slideMenu);
        lv_menu = (ListView) findViewById(R.id.menu_listview);
        lv_main= (ListView) findViewById(R.id.main_listview);
    }
}
