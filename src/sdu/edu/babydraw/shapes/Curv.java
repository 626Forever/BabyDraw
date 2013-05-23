package sdu.edu.babydraw.shapes;

import sdu.edu.babydraw.interfaces.Shapable;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * �滭�����࣬��װ�˻滭���ߵķ���
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
