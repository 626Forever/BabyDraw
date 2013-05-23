package sdu.edu.babydraw.shapes;

import sdu.edu.babydraw.interfaces.Shapable;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 绘画曲线类，封装了绘画曲线的方法
 * @author lhy
 *
 */
public class Curv extends ShapeAbstract  {


	public Curv(Shapable paintTool) {
		super(paintTool);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		super.draw(canvas, paint);
		canvas.drawPath(mPath, paint);
	}

	@Override
	public String toString() {
		return "curv";
	}
}
