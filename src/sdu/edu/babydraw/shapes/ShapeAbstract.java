package sdu.edu.babydraw.shapes;

import sdu.edu.babydraw.interfaces.Shapable;
import sdu.edu.babydraw.interfaces.ShapesInterface;
import sdu.edu.babydraw.painttools.FirstCurrentPosition;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 提供shape子类的默认实现
*/
public class ShapeAbstract implements ShapesInterface {

	protected Shapable paintTool = null;
	protected FirstCurrentPosition firstCurrentPos;
	Path mPath;
	protected float x1 = 0;
	protected float y1 = 0;
	protected float x2 = 0;
	protected float y2 = 0;

	ShapeAbstract(Shapable paintTool) {
		assert(paintTool!=null);
		this.paintTool = paintTool;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		firstCurrentPos = paintTool.getFirstLastPoint();
		mPath = paintTool.getPath();
		x1 = firstCurrentPos.firstX;
		y1 = firstCurrentPos.firstY;
		x2 = firstCurrentPos.currentX;
		y2 = firstCurrentPos.currentY;
	}

}
