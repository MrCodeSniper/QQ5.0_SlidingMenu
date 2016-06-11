package chenhong.com.qq.View;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 自己的线性布局目的当菜单栏打开且不为空时拦截所有点击事件，并点击主界面迅速让菜单栏处于关闭状态
 * Created by Administrator on 2016/6/11.
 */
public class MyLinearLayout extends LinearLayout {

    public void setSlidingMenu(SlidingMenu slidingMenu) {
        this.slidingMenu = slidingMenu;
    }

    private SlidingMenu slidingMenu;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(slidingMenu!=null&&slidingMenu.getCurrentstate()== SlidingMenu.DragState.Open){
            return true;//拦截
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            slidingMenu.close();
        }
        if(slidingMenu!=null&&slidingMenu.getCurrentstate()== SlidingMenu.DragState.Open){
            return true;//拦截
        }
        return super.onTouchEvent(event);
    }
}
