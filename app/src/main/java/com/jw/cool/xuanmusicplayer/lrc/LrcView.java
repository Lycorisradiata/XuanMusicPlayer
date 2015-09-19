package com.jw.cool.xuanmusicplayer.lrc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.jw.cool.xuanmusicplayer.R;

/**
 * 自定义绘画歌词，产生滚动效果
 * @author wwj
 *
 */
public class LrcView extends android.widget.TextView {
	private float width;		//歌词视图宽度
	private float height;		//歌词视图高度
	private Paint currentPaint;	//当前画笔对象
	private Paint notCurrentPaint;	//非当前画笔对象
	private float textHeight = 82;	//文本高度
	private float textSize = 72;		//文本大小
	private int index = 0;		//list集合下标
	private int duration;
	private int focusColor;
	private int noFocusColor;
	private int verticalSpace = 20;
	private float moveDistance;
	private int currentTime;
	private boolean isIndexUseFirstTime;
	private int twoIndexTimeInterval;
	private int currentIndexTime;
	
	private List<LrcContent> lrcList = new ArrayList<LrcContent>();
	
	public void setLrcList(List<LrcContent> lrcList) {
		this.lrcList = lrcList;
		index = 0;
	}

	public LrcView(Context context) {
		super(context);
		init();
	}
	public LrcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		focusColor = Color.parseColor("#D2FBFB1D");
		noFocusColor = Color.parseColor("#8cffffff");

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LrcView);
		final int count = typedArray.getIndexCount();
		for (int i = 0; i < count; i++) {
			int attr = typedArray.getIndex(i);
			switch (attr){
				case R.styleable.LrcView_focus_color:
					focusColor = typedArray.getColor(attr, focusColor);
					break;
				case R.styleable.LrcView_no_focus_color:
					noFocusColor = typedArray.getColor(attr, noFocusColor);
					break;
				case R.styleable.LrcView_vertical_space:
					verticalSpace = typedArray.getDimensionPixelSize(attr, verticalSpace);
					break;
				default:
					break;
			}
		}

		init();
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setFocusable(true);		//设置可对焦
		
		//高亮部分
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);	//设置抗锯齿，让文字美观饱满
		currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式
		
		//非高亮部分
		notCurrentPaint = new Paint();
		notCurrentPaint.setAntiAlias(true);
		notCurrentPaint.setTextAlign(Paint.Align.CENTER);

		currentPaint.setColor(focusColor);
		currentPaint.setTextSize(getTextSize());
		currentPaint.setTypeface(getTypeface());

		notCurrentPaint.setColor(noFocusColor);
		notCurrentPaint.setTextSize(getTextSize());
		notCurrentPaint.setTypeface(getTypeface());

	}
	
	/**
	 * 绘画歌词
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(canvas == null) {
			return;
		}

//		currentPaint.setColor(Color.argb(210, 251, 248, 29));
//		notCurrentPaint.setColor(Color.argb(140, 255, 255, 255));
		
//		currentPaint.setTextSize(72);
//		currentPaint.setTypeface(Typeface.SERIF);
		
//		notCurrentPaint.setTextSize(textSize);
//		notCurrentPaint.setTypeface(Typeface.DEFAULT);
		
		try {
			setText("");
			canvas.drawText(lrcList.get(index).getLrcStr(), width / 2, height / 2, currentPaint);
			
			float tempY = height / 2;
			//画出本句之前的句子
			for(int i = index - 1; i >= 0 && i > index - 3; i--) {
				//向上推移
				tempY = tempY - textHeight;
				canvas.drawText(lrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
			}
			tempY = height / 2;
			//画出本句之后的句子
			for(int i = index + 1; i < lrcList.size() && i < index + 3 ; i++) {
				//往下推移
				tempY = tempY + textHeight;
				canvas.drawText(lrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
			} 
		} catch (Exception e) {
			setText("...木有歌词文件，赶紧去下载...");
		}
	}

	/**
	 * 当view大小改变的时候调用的方法
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void updateIndex(int time){
		currentTime = time;
		int lastIndex = index;
		index = lrcIndex();
		isIndexUseFirstTime = lastIndex == index ? true : false;

		//如果索引变化了，则重新计算到下一个索引的时间间隔
		if(isIndexUseFirstTime){
			currentIndexTime = lrcList.get(index).getLrcTime();
			if(index < lrcList.size() - 1){
				twoIndexTimeInterval = lrcList.get(index + 1).getLrcTime() - currentIndexTime;
			}else{
				twoIndexTimeInterval = 0;
			}
		}

		//这次刷新需要移动的距离
		if(twoIndexTimeInterval > 0){
			//textHeight + verticalSpace 表示两行歌词之间的高度差
			moveDistance = (textHeight + verticalSpace) / twoIndexTimeInterval
					       * (currentTime - currentIndexTime);

		}

	}


	/**
	 * 根据时间获取歌词显示的索引值
	 * @return
	 */
	public int lrcIndex() {
		if(currentTime < duration) {
			for (int i = 0; i < lrcList.size(); i++) {
				if (i < lrcList.size() - 1) {
					if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
						index = i;
					}
					if (currentTime > lrcList.get(i).getLrcTime()
							&& currentTime < lrcList.get(i + 1).getLrcTime()) {
						index = i;
					}
				}
				if (i == lrcList.size() - 1
						&& currentTime > lrcList.get(i).getLrcTime()) {
					index = i;
				}
			}
		}
		return index;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
