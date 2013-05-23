package sdu.edu.babydraw.interfaces;

/**
 * ����ǰ���ӿ�
 * @author hzy
 *
 */
public interface UndoCommand {
    public void undo();
    public void redo();
    public boolean canUndo();
    public boolean canRedo();
}
