package chenhong.com.qq.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

import chenhong.com.qq.Utils.ColorUtil;

/**
 * 实验用布局
 * Created by Administrator on 2016/6/9.
 */
public class DragLayout extends FrameLayout {
    private View redview;
    private ViewDragHelper draghelper;
    private View blueview;

    public DragLayout(Context context) {
        super(context);
        initview();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview();
    }

    private void initview() {
        draghelper = ViewDragHelper.create(this, callback);
    }

  private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
      @Override
      //用于判断是否捕获当前child的触摸事件 return true捕获 false不处理
      public boolean tryCaptureView(View child, int pointerId) {
          return child==redview||child==blueview;
      }
      //当view被开始捕获和解析的回调方法
      @Override
      public void onViewCaptured(View capturedChild, int activePointerId) {
          super.onViewCaptured(capturedChild, activePointerId);
      }
      //当手指抬起时执行该方法
      //releasedChild：当前抬起的view xvel，yvel  x和y方向的移动速度 正-右 负-右
      @Override
      public void onViewReleased(View releasedChild, float xvel, float yvel) {
          super.onViewReleased(releasedChild, xvel, yvel);
          int centerLeft=getMeasuredWidth()/2-releasedChild.getMeasuredWidth()/2;
          //draghelper内部封装了scroller滚动
          if(releasedChild.getLeft()<centerLeft){
              //向左滚动
           draghelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
              ViewCompat.postInvalidateOnAnimation(DragLayout.this);//滚动之后刷新布局
          }else {
              //向右滚动
              draghelper.smoothSlideViewTo(releasedChild,getMeasuredWidth()-releasedChild.getMeasuredWidth(),releasedChild.getTop());
              ViewCompat.postInvalidateOnAnimation(DragLayout.this);//滚动之后刷新布局
          }
      }

      //当child的位置改变的时候执行，一般做其他子view的跟随移动
      //changedView：位置改变的child
      //left，top最新的
      //dx，dy本次水平垂直移动的范围
      @Override
      public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
          super.onViewPositionChanged(changedView, left, top, dx, dy);
         if(changedView==redview){
             //让blueview跟随
             blueview.layout(blueview.getLeft()+dx,blueview.getTop()+dy,blueview.getRight()+dx,blueview.getBottom()+dy);
         }else if(changedView==blueview){
             redview.layout(redview.getLeft() + dx, redview.getTop() + dy, redview.getRight() + dx, redview.getBottom() + dy);
         }
          //计算view移动的百分比 转化为小数
          float fraction=changedView.getLeft()*1f/(getMeasuredWidth()-changedView.getMeasuredWidth());
          executeAnim(fraction);

      }
      //控制child在水平方向上的移动
      //left表示这个drag类认为你想当前child的left改变的值 left=child.getLeft+dx
      //dx:本次child水平方向移动的距离
      //return你想变的left的值
      @Override
      public int clampViewPositionHorizontal(View child, int left, int dx) {
          if(left<0){
              left=0;
          }
          int maxWidth=getMeasuredWidth()-child.getMeasuredWidth();
          if(left>maxWidth){
              left=maxWidth;
          }
          return left;
      }
      //控制child在垂直方向上的移动
      @Override
      public int clampViewPositionVertical(View child, int top, int dy) {
          int maxHeight=getMeasuredHeight()-child.getMeasuredHeight();
          if(top<0){
              top=0;
          }else if(top>maxHeight){
              top=maxHeight;
          }
          return top;
      }
      //获取view水平方向拖拽的范围,但是不限制边界，最好不要返回0
      @Override
      public int getViewHorizontalDragRange(View child) {
          return getMeasuredWidth()-child.getMeasuredWidth();
      }
      //获取view垂直方向拖拽的范围
      @Override
      public int getViewVerticalDragRange(View child) {
          return getMeasuredHeight()-child.getMeasuredHeight();
      }
  };

    private void executeAnim(float fraction) {
        //缩放
//		ViewHelper.setScaleX(redView, 1+0.5f*fraction);
//		ViewHelper.setScaleY(redView, 1+0.5f*fraction);
        //旋转
 //        ViewHelper.setRotation(redview,720*fraction);//围绕垂直屏幕的Z轴
        ViewHelper.setRotationX(redview,360*fraction);//围绕x轴转
//		ViewHelper.setRotationY(redView,360*fraction);//围绕y轴转
        //平移
//		ViewHelper.setTranslationX(redView,80*fraction);
        //透明
//		ViewHelper.setAlpha(redView, 1-fraction);

//设置过度颜色的渐变
        redview.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));
//		setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,Color.RED,Color.GREEN));
    }

    //刷新滚动
    public void computeScroll() {
        if (draghelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    };




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

    //系统解析xml的标签时结束后调用的方法，即可得自己的子view数量
   //一般在此方法中初始化子view的引用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redview = getChildAt(0);
        blueview=getChildAt(1);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //测量自己的子veiw
//        //打造MeasureSpec 模式：精确的
////        int measureSpec=MeasureSpec.makeMeasureSpec(view.getLayoutParams().width,MeasureSpec.EXACTLY);
////        view.measure(measureSpec,measureSpec);
//        //没有特殊的对子view测量需求即可用下列方法
//        measureChild(view,widthMeasureSpec,heightMeasureSpec);
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left=getPaddingLeft()+getMeasuredWidth()/2-redview.getMeasuredWidth()/2;
        int top=getPaddingTop();
        redview.layout(left,top,left+redview.getMeasuredWidth(),top+redview.getMeasuredHeight());
        blueview.layout(left, redview.getBottom(),
                left + blueview.getMeasuredWidth(), redview.getBottom()
                        + blueview.getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
