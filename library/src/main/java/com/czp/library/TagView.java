package com.czp.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by caizepeng on 16/6/11.
 */
public class TagView  extends View{
    public final static int TEXTSTYLE_NORMAL = 0;
    public final static int TEXTSTYLE_ITALIC = 1;
    public final static int TEXTSTYLE_BOLD = 2;
    public final static int TRIANGLE = 2;
    public final static int RECT = 1;
    public final static int IRREGULAR = 0;
    public final static int TOPLEFT = 1;
    public final static int TOPRIGHT = 0;
    public final static int LEFT_ROTATE = -45;
    public final static int RIGHT_ROTATE = 45;
    private int DEFAULT_TEXTCOLOR = Color.WHITE;
    private int DEFAULT_BGCOLOR = Color.BLUE;
    private float DEFAULT_TEXTSIZE =  sp2px(9);
    private float DEFAULT_PADDING = dp2px(5);
    private float DEFAULT_LINESPACE = dp2px(2);
    private int DEFAULT_SHAPE = RECT;
    private int mShape = DEFAULT_SHAPE;
    private String mTitle,mSubTitle;
    private Paint mPaint;
    private Paint mTextPaint,mSubTextPaint;
    private int mTitleColor,mSubTitleColor,mBgColor;
    private float mTitleSize,
            mSubTitleSize,
            mTagTopPadding,
            mTagBottomPadding,
            mTagLeftPadding,
            mTagRightPadding,
            mLineSpace;
    private int mPosition;
    private int triHeight,taiWidth;
    private int mRectSize;
    private int mTextStyle,mSubTextStyle;
    private int mTextHeight,mTextWidth,mSubTextHeight,mSubTextWidth;
    private Bitmap mBitmap;
    public TagView(Context context) {
        this(context,null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagView);
        mTitle = ta.getString(R.styleable.TagView_title);
        mTitleSize = ta.getDimension(R.styleable.TagView_titleSize,DEFAULT_TEXTSIZE);
        mTagBottomPadding = ta.getDimension(R.styleable.TagView_tagBottomPadding,DEFAULT_PADDING);
        mTagTopPadding = ta.getDimension(R.styleable.TagView_tagTopPadding,DEFAULT_PADDING);
        mSubTitle = ta.getString(R.styleable.TagView_subTitle);
        mSubTitleSize  = ta.getDimension(R.styleable.TagView_subTitleSize,DEFAULT_TEXTSIZE);
        mShape = ta.getInt(R.styleable.TagView_shape,DEFAULT_SHAPE);
        mLineSpace = ta.getDimension(R.styleable.TagView_lineSpace,DEFAULT_LINESPACE);
        mPosition = ta.getInteger(R.styleable.TagView_position,TOPLEFT);
        mTitleColor = ta.getColor(R.styleable.TagView_titleColor,DEFAULT_TEXTCOLOR);
        mSubTitleColor = ta.getColor(R.styleable.TagView_subTitleColor,DEFAULT_TEXTCOLOR);
        mBgColor = ta.getColor(R.styleable.TagView_bgColor,DEFAULT_BGCOLOR);
        mTagLeftPadding = ta.getDimension(R.styleable.TagView_tagLeftPadding,DEFAULT_PADDING);
        mTagRightPadding = ta.getDimension(R.styleable.TagView_tagRightPadding,DEFAULT_PADDING);
        mRectSize = (int) ta.getDimension(R.styleable.TagView_rectSize,0);
        mTextStyle = ta.getInt(R.styleable.TagView_textStyle,TEXTSTYLE_NORMAL);
        mSubTextStyle = ta.getInt(R.styleable.TagView_subTextStyle,TEXTSTYLE_NORMAL);

        ta.recycle();

        initShapePaint();
        iniTitlePaint();
        initSubTitlePaint();
        measureText();
    }
    public void initShapePaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBgColor);
    }
    public void iniTitlePaint(){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTitleSize);
        mTextPaint.setColor(mTitleColor);
        if (mTextStyle==TEXTSTYLE_ITALIC){
            mTextPaint.setTypeface(Typeface.SANS_SERIF);
        }else if (mTextStyle==TEXTSTYLE_BOLD){
            mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
    public void initSubTitlePaint(){
        mSubTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSubTextPaint.setTextAlign(Paint.Align.CENTER);
        mSubTextPaint.setTextSize(mSubTitleSize);
        mSubTextPaint.setColor(mSubTitleColor);
        if (mSubTextStyle==TEXTSTYLE_ITALIC){
            mSubTextPaint.setTypeface(Typeface.SANS_SERIF);
        }else if (mSubTextStyle==TEXTSTYLE_BOLD){
            mSubTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (mShape){
            case IRREGULAR:
                widthSize = (int) Math.max(mTextWidth+mTagLeftPadding+mTagRightPadding,widthSize);
                heightSize = (int) Math.max(mTextHeight+mTagTopPadding+mTagBottomPadding+mLineSpace+mSubTextHeight,heightSize);
                setMeasuredDimension(widthSize,heightSize);
                break;
            case RECT:
                if(mRectSize == 0)
                    mRectSize = Math.max(mTextWidth,Math.min(widthSize,heightSize));
                setMeasuredDimension(mRectSize,mRectSize);
                break;
            case TRIANGLE:
                triHeight = (int) (mSubTextHeight+mTextHeight+mTagBottomPadding+mTagTopPadding+mLineSpace);
                taiWidth = triHeight*2;
                setMeasuredDimension(taiWidth, (int) (triHeight*Math.sqrt(2)));
                break;
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_4444);
        switch (mShape){
            case RECT:
                canvasRect();
                break;
            case IRREGULAR:
                canvasIrr();
                break;
            case TRIANGLE:
                canvasTriangle();
                break;
        }

    }
    public void canvasTriangle(){
        Canvas canvas = new Canvas(mBitmap);
        canvas.translate(0, (float) ((triHeight * Math.sqrt(2)) - triHeight));
        if(mPosition == TOPLEFT){
            canvas.rotate(LEFT_ROTATE,0,triHeight);
        }else{
            canvas.rotate(RIGHT_ROTATE,triHeight*2,triHeight);
        }
        Path path = new Path();
        path.moveTo(0,triHeight);
        path.lineTo(triHeight,0);
        path.lineTo(triHeight*2,triHeight);
        path.close();
        canvas.drawPath(path,mPaint);

        canvas.drawText(mTitle,triHeight,mTextHeight+mTagTopPadding,mTextPaint);
        if(!TextUtils.isEmpty(mSubTitle)){
            canvas.drawText(mSubTitle,triHeight,mTextHeight+mTagTopPadding+mLineSpace+mSubTextHeight,mTextPaint);
        }
    }
    public void canvasRect(){
        Canvas canvas = new Canvas(mBitmap);
        float height = mTagBottomPadding+mTagTopPadding+mTextHeight;
        int h = getMeasuredHeight()*getMeasuredHeight();
        int target = (int) Math.sqrt(h+h);
        Path mPath = new Path();
        mPath.moveTo(0,-height);
        mPath.lineTo(0,0);
        mPath.lineTo(target,0);
        mPath.lineTo(target,-height);
        mPath.close();
        canvas.save();
        if(mPosition == TOPLEFT){
            canvas.translate(0,getMeasuredHeight());
            canvas.rotate(LEFT_ROTATE);
        }else{
            canvas.rotate(Math.abs(RIGHT_ROTATE));
        }
        mPaint.setColor(Color.RED);
        canvas.drawPoint(0,0,mPaint);
        canvas.drawPath(mPath,mPaint);
        int topRange = (int) ((mTextHeight/2+mTagTopPadding+mTagBottomPadding)/2);
        canvas.drawText(mTitle,target/2,-topRange,mTextPaint);
        canvas.restore();


    }
    public void canvasIrr(){
        Canvas canvas = new Canvas(mBitmap);
        Path path = new Path();
        path.moveTo(0,0);
        path.lineTo(0,getMeasuredHeight());
        path.lineTo(getMeasuredWidth()/2,getMeasuredHeight()-getMeasuredHeight()/4);
        path.lineTo(getMeasuredWidth(),getMeasuredHeight());
        path.lineTo(getMeasuredWidth(),0);
        path.close();
        canvas.drawPath(path,mPaint);
        int topRange = (getMeasuredHeight()-getMeasuredHeight()/4)/2-mTextHeight/2;
        canvas.drawText(mTitle,getMeasuredWidth()/2,topRange,mTextPaint);
        if(!TextUtils.isEmpty(mSubTitle)){
            canvas.drawText(mSubTitle,getMeasuredWidth()/2,(getMeasuredHeight()-getMeasuredHeight()/4)/2+mTextHeight+mSubTextHeight/2+mLineSpace-mTagBottomPadding,mSubTextPaint);
        }
        canvas.save();
    }
    public void measureText(){
        if(!TextUtils.isEmpty(mTitle)){
            Rect rect = new Rect();
            mTextPaint.getTextBounds(mTitle,0,mTitle.length(),rect);
            mTextHeight = rect.height();
            mTextWidth = rect.width();
        }


        if(!TextUtils.isEmpty(mSubTitle)){
            Rect subRect = new Rect();
            mSubTextPaint.getTextBounds(mSubTitle,0,mSubTitle.length(),subRect);
            mSubTextHeight = subRect.height();
            mSubTextWidth = subRect.width();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap,0,0,mPaint);
    }
    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public float sp2px(float spValue) {
        final float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return spValue * scale;
    }

    public int getShape() {
        return mShape;
    }

    public void setShape(int mShape) {
        this.mShape = mShape;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(String mSubTitle) {
        this.mSubTitle = mSubTitle;
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    public void setTitleColor(int mTitleColor) {
        this.mTitleColor = mTitleColor;
    }

    public int getSubTitleColor() {
        return mSubTitleColor;
    }

    public void setSubTitleColor(int mSubTitleColor) {
        this.mSubTitleColor = mSubTitleColor;
    }

    public float getTitleSize() {
        return mTitleSize;
    }

    public void setTitleSize(float mTitleSize) {
        this.mTitleSize = mTitleSize;
    }

    public float getSubTitleSize() {
        return mSubTitleSize;
    }

    public void setSubTitleSize(float mSubTitleSize) {
        this.mSubTitleSize = mSubTitleSize;
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
    }

    public float getTagTopPadding() {
        return mTagTopPadding;
    }

    public void setTagTopPadding(float mTagTopPadding) {
        this.mTagTopPadding = mTagTopPadding;
    }

    public float getTagBottomPadding() {
        return mTagBottomPadding;
    }

    public void setTagBottomPadding(float mTagBottomPadding) {
        this.mTagBottomPadding = mTagBottomPadding;
    }

    public float getTagLeftPadding() {
        return mTagLeftPadding;
    }

    public void setTagLeftPadding(float mTagLeftPadding) {
        this.mTagLeftPadding = mTagLeftPadding;
    }

    public float getTagRightPadding() {
        return mTagRightPadding;
    }

    public void setTagRightPadding(float mTagRightPadding) {
        this.mTagRightPadding = mTagRightPadding;
    }

    public float getLineSpace() {
        return mLineSpace;
    }

    public void setLineSpace(float mLineSpace) {
        this.mLineSpace = mLineSpace;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getTextStyle() {
        return mTextStyle;
    }

    public void setTextStyle(int mTextStyle) {
        this.mTextStyle = mTextStyle;
    }

    public int getRectSize() {
        return mRectSize;
    }

    public void setRectSize(int mRectSize) {
        this.mRectSize = mRectSize;
    }

    public int getSubTextStyle() {
        return mSubTextStyle;
    }

    public void setSubTextStyle(int mSubTextStyle) {
        this.mSubTextStyle = mSubTextStyle;
    }
}
