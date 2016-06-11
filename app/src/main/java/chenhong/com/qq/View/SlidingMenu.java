package chenhong.com.qq.View;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

import chenhong.com.qq.Utils.ColorUtil;

/**
 * Created by Administrator on 2016/6/10.
 */
public class SlidingMenu extends FrameLayout {

    private View menuView;
    private View mainView;
    // ViewDragHelper安卓系统自带的处理拖拽的帮助类
    private ViewDragHelper draghelper;
    private int width;
    private float dragrange;
    //利用计算器辅助计算
    private FloatEvaluator floatEvaluator;
    private IntEvaluator intEvaluator;

    public SlidingMenu(Context context) {
        super(context);
        initview();
    }

    private void initview() {
        draghelper = ViewDragHelper.create(this, callback);//回调
        floatEvaluator=new FloatEvaluator();
        intEvaluator=new IntEvaluator();
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview();
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview();
    }
   //这个方法在xml解析完后执行可用于得到子view
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("Slling menu only have two children");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让ViewDragHelper帮我们判断是否拦截
        return draghelper.shouldInterceptTouchEvent(ev);

    }

    //让ViewDragHelper帮我们解析触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        draghelper.processTouchEvent(event);
        return true;
    }
    /**
     * 该方法在onMeasure执行完之后执行，那么可以在该方法中初始化自己和子View的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragrange = width*0.6f;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==menuView||child==mainView;
        }
        @Override
          public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child==mainView){
                if(left<0){
                    left=0;
                }else if(left>dragrange){
                    left= (int) dragrange;
                }
            }else if(child==menuView){

            }
            return left;
        }
        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面;
         * 最好不要返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragrange;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView==menuView){
                //固定住菜单view
               menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getMeasuredHeight());
                int newLeft=mainView.getLeft()+dx;
                if(newLeft>=0&&newLeft<=dragrange){
                    mainView.layout(mainView.getLeft()+dx,mainView.getTop()+dy,mainView.getRight()+dx,mainView.getBottom()+dy);
                }
            }
            //计算滑动百分比
            float fraction=mainView.getLeft()/dragrange;
            executeAnim(fraction);
            //更改状态回调方法
            if(fraction==0&&currentstate!=DragState.Close){
                currentstate=DragState.Close;
                if(draglistener!=null) draglistener.onClose();
            }else  if(fraction==1f&&currentstate!=DragState.Open){
                currentstate=DragState.Open;
                if(draglistener!=null) draglistener.onOpen();
            }
            if(draglistener!=null){
                draglistener.onDraging(fraction);
            }


        }
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(mainView.getLeft()<=dragrange/2){
                close();
            }else{
                open();
            }

            //处理用户的微滑
            if(xvel>200&&currentstate!=DragState.Open){
                open();
            }else if(xvel<-200&&currentstate!=DragState.Close){
                close();
            }
        }
    };

    public void open() {
        draghelper.smoothSlideViewTo(mainView, (int) dragrange, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
    }

    public void close() {
        draghelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
    }

    //刷新滚动
    public void computeScroll() {
        if (draghelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
        }
    };
    private void executeAnim(float fraction) {
       //0-1
        //缩小MainView放大MenuView
        //计算器类 根据变化的百分比来计算进度  1f最后的 0.9f起始的
        float scalevalue=floatEvaluator.evaluate(fraction,1f,0.8f);;//1-0.8
        ViewHelper.setScaleX(mainView, scalevalue);
        ViewHelper.setScaleY(mainView, scalevalue);
        ViewHelper.setTranslationX(menuView,intEvaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
        //放大
        ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5f,1.0f));
        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1.0f));
        ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1.0f));
        //给slidngmenu的背景盖上遮罩
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    //定义接口返回状态给外界让外界根据状态进行变化
    private OnDragStateChangeListener draglistener;
    public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
        this.draglistener=listener;
    }
    public  interface OnDragStateChangeListener{
        void onOpen();
        void onClose();
        void onDraging(float fraction);
    }
    public DragState getCurrentstate() {
        return currentstate;
    }
    private DragState currentstate=DragState.Close;
    enum DragState{//枚举两个状态
        Open,Close;
    }
}
