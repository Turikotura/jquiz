package database;



import org.apache.commons.dbcp2.BasicDataSource;

import java.util.List;

public abstract class Database<T> {
    private BasicDataSource dataSource;

    public abstract List<T> getAll();
    public abstract T getById(int id);
    public abstract boolean removeById(int id);
    public abstract boolean setById(int id, T toChange);
}