package sdu.edu.babydraw.shapes;

import sdu.edu.babydraw.interfaces.Shapable;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 长方形类，封装绘画长方形方法
 * @author lhy
 *
 */
public class Rectangle extends ShapeAbstract {

	public Rectangle (Shapable paintTool) {
		super(paintTool);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		super.draw(canvas, paint);
		canvas.drawRect(x1, y1, x2, y2, paint);
	}

	@Override
	public String toString() {
		return "rectangle";
	}
}
