package database;

import java.util.List;

public interface Database<T> {
    List<T> getALL();
    T getById(int id);
    boolean setById(int id, T toChange);
    boolean removeById(int id);
}