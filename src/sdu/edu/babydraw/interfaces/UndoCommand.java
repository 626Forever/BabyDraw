package sdu.edu.babydraw.interfaces;

/**
 * 撤销前进接口
 * @author hzy
 *
 */
public interface UndoCommand {
    public void undo();
    public void redo();
    public boolean canUndo();
    public boolean canRedo();
}
