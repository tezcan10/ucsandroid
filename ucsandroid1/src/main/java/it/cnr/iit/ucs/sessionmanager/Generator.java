package it.cnr.iit.ucs.sessionmanager;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class Generator extends OrmLiteConfigUtil {
    public static void main( String args[] ) throws SQLException, IOException {
        Class<?> clazz[] = new Class<?>[] { Session.class, OnGoingAttribute.class };
        writeConfigFile( "ormlite_config", clazz );
    }

}
