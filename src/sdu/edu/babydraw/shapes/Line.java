package sdu.edu.babydraw.shapes;

import sdu.edu.babydraw.interfaces.Shapable;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 绘画直线类，封装了直线方法
 * @author lhy
 *
 */
public class Line extends ShapeAbstract {

	public Line(Shapable paintTool) {
		super(paintTool);
	}

	@Override
	public void draw(Canvas canvas,Paint paint) {
		super.draw(canvas, paint);
		canvas.drawLine(x1, y1, x2, y2, paint);
	}

	@Override
	public String toString() {
		return " line";
	}
}
