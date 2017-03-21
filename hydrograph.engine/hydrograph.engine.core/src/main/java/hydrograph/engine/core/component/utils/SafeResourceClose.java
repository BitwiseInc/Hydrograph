package hydrograph.engine.core.component.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by ganeshs on 3/7/2017.
 */
public class SafeResourceClose {
    static final Logger log = Logger.getLogger(SafeResourceClose.class.getName());
    public static void safeConnectionClose(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.warn("Failed to close the connection. The error is " + e.getMessage());
                throw e;
            }
        }
    }

    public static void safeReaderClose(Reader reader) throws IOException {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                log.warn("Failed to close the reader. The error is " + e.getMessage());
                throw e;
            }
        }
    }

    public static void safeWriterClose(Writer writer) throws IOException {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                log.warn("Failed to close the reader The error is " + e.getMessage());
                throw e;
            }
        }
    }

    public static void safeInputStreamClose(InputStream is) throws IOException {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.warn("Exception in closing input stream. The error message is " + e.getMessage());
                throw e;
            }
        }
    }
}
