package sdu.edu.babydraw.interfaces;


import android.graphics.Canvas;

/**
 * 画笔工具接口
 * @author hzy
 *
 */
public interface ToolInterface {
	public void draw(Canvas canvas);

	public void touchDown(float x, float y);

	public void touchMove(float x, float y);

	public void touchUp(float x, float y);

	public boolean hasDraw();
}
