package hydrograph.engine.spark.components;

import hydrograph.engine.core.component.entity.RunSqlEntity;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * The Class RunSQLComponentTest.
 *
 * @author Bitwise
 */
public class RunSQLComponentTest {

    RunSqlEntity runSqlEntity = new RunSqlEntity();
    Properties properties = new Properties();


    @Test(expected = DatabaseConnectionException.class)
    public void throwException_IfDatabaseConnectionNameIsEmpty() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setQueryCommand("insert into testsp values(1,1,1,1)");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("DBDEVSRV");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }


    @Test(expected = Exception.class)
    public void throwException_IfDatabaseNameIsEmpty() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setQueryCommand("insert into testsp values(1,1,1,1)");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("DBDEVSRV");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }


    @Test
    public void testAllPropertiesValid() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("DBDEVSRV");
        runSqlEntity.setQueryCommand("insert into testsp values(123456,1,1,1)");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();
    }


    @Test(expected = Exception.class)
    public void throwException_IfDatabasePasswordIsEmpty() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setQueryCommand("insert into testsp values(1,1,1,1)");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("DBDEVSRV");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }


    @Test(expected = Exception.class)
    public void throwException_IfDatabasePortNumberIsEmpty() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("");
        runSqlEntity.setQueryCommand("insert into testsp values(1,1,1,1)");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("DBDEVSRV");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }


    @Test(expected = DatabaseConnectionException.class)
    public void throwException_IfDatabaseQueryIsEmpty() throws Exception {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setQueryCommand("");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("DBDEVSRV");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }


    @Test(expected = RuntimeException.class)
    public void throwException_IfDatabaseUserNameIsEmpty() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setQueryCommand("insert into testSP(1,1,1,1)");
        runSqlEntity.setDbUserName("");
        runSqlEntity.setServerName("DBDEVSRV");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }


    @Test(expected = RuntimeException.class)
    public void throwException_IfDatabaseServerNameIsEmptyOrInvalid() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setQueryCommand("insert into testSP(1,1,1,1)");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }

    @Test(expected = RuntimeException.class)
    public void throwException_IfDatabaseQueryIsInvalid() throws IOException {
        runSqlEntity.setBatch("0");
        runSqlEntity.setComponentId("1");
        runSqlEntity.setComponentName("RunSQLComponent");
        runSqlEntity.setDatabaseConnectionName("Oracle");
        runSqlEntity.setDatabaseName("PRACTICE");
        runSqlEntity.setDbPwd("Bitwise2017");
        runSqlEntity.setPortNumber("1521");
        runSqlEntity.setQueryCommand("insert into testPassssssss(1,1,1,1)");
        runSqlEntity.setDbUserName("htcd");
        runSqlEntity.setServerName("DBDEVSRV");
        RunSQLComponent runSQLComponent = new RunSQLComponent(runSqlEntity);
        runSQLComponent.execute();

    }


}
