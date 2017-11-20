package com.fengyun.cardgame.bean;

import android.graphics.Bitmap;

public class Card  implements Comparable<Card> {
	
	private String id;//卡的编号

	private int name; //Card的名称  3\4\...K、A(14) 2(15)、小王(16)、大王(17)
	
	private int x=0;      //绘制牌横坐标
	private int y=0;	  //绘制牌纵坐标
	
	private int width;    //扑克牌宽度
	private int height;   //扑克牌高度
	
	private int sw;//实际占宽度
	
	private Bitmap bitmap;//数字图片
	private Bitmap smallBitmap;//数字图片
	
	private boolean clicked=false;//是否被选中
	
	public Card(String id, int name, Bitmap bitmap, Bitmap smallBitmap) {
		this.id=id;
		this.name = name;
		this.setBitmap(bitmap);
		this.setSmallBitmap(smallBitmap);
	}

	/**
	 * 设置相应坐标
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param sw
	 */
	public void setLocationAndSize(int x,int y,int w,int h,int sw){
		this.x=x;
		this.y=y;
		this.width=w;
		this.height=h;
		this.sw=sw;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "id = " + id + " name = " + name;
	}

	@Override
	public int compareTo(Card another) {
		return another.name-this.name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getName() {
		return name;
	}

	public int getSw() {
		return sw;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public Bitmap getSmallBitmap() {
		return smallBitmap;
	}

	public void setSmallBitmap(Bitmap smallBitmap) {
		this.smallBitmap = smallBitmap;
	}

}
