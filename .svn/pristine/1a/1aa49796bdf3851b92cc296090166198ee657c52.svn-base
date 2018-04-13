package net.iclassmate.bxyd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class IndexBar extends View {
	public interface OnIndexSelectedListener{
		public void indexChange(char index);
		public void indexSelected(char index);
	}
	public void setOnIndexSelectedListener(OnIndexSelectedListener l){
		osi=l;
	}
	private OnIndexSelectedListener osi;
	private char[] indexs = new char[] { '#', 'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	Paint paint=new Paint();
	public IndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setClickable(true);
	}
	int selected=-1;
	int sigleHeight;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight()-10;
		int width = getWidth();
		sigleHeight = height / indexs.length;
		for (int i = 0; i < indexs.length; i++) {
			paint.setColor(Color.GRAY);
			paint.setTextSize(20);
			paint.setAntiAlias(true);
			float xPos = width / 2 - paint.measureText(indexs[i]+"") / 2;
			float yPos = sigleHeight * i + sigleHeight;
			canvas.drawText(indexs[i]+"", xPos, yPos, paint);
			paint.reset();
		}
	}
	char currentIndex=' ';
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y=event.getY();
		int i=(int) Math.ceil(y/sigleHeight);
		if(i<1){
			i=1;
		}else if(i>indexs.length){
			i=indexs.length;
		}
		char index=indexs[i-1];
		if(currentIndex!=index){
			currentIndex=index;
			if(osi!=null){
				osi.indexChange(currentIndex);
			}
		}
		if(event.getAction()==MotionEvent.ACTION_UP){
			if(osi!=null){
				osi.indexSelected(index);
			}
			currentIndex=' ';
		}
		return super.onTouchEvent(event);
	}
}
