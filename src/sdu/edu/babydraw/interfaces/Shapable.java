package sdu.edu.babydraw.interfaces;

import sdu.edu.babydraw.painttools.FirstCurrentPosition;
import android.graphics.Path;

/**
 * ��״�ӿ�
 * @author hzy
 *
 */
public interface Shapable {
	public Path getPath();

	public FirstCurrentPosition getFirstLastPoint();

	void setShap(ShapesInterface shape);
}
