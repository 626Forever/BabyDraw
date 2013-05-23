package sdu.edu.babydraw.utils;

import java.io.File;
import java.util.regex.Pattern;

/**
 * �ļ���ȷ��
 * @author hzy
 *
 */
public class FileNameOk {
	public static boolean isFileNameOk(File file) {
		return Pattern.compile("[\\w%+,/=_-]+").matcher(file.getPath())
				.matches();
	}
}

