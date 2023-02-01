package com.daniel.db.dialect;

import org.hibernate.MappingException;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
/**
 * Class to support the Identity Column
 * @author dfcastro
 *
 */
public class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {

    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        return "integer";
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) throws MappingException {
        return "select last_insert_rowid()";
    }

    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }
}
