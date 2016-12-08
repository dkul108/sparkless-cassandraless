package org.cv.fis;


import org.cv.fis.files.provider.SourceException;
import org.cv.fis.files.LocalFileData;
import org.cv.fis.files.provider.FileRepositoryConnection;
import org.cv.fis.files.provider.LocalFileConnection;
import org.junit.Test;

import java.nio.file.Paths;

import static org.fest.assertions.api.Assertions.*;

public class LocalFileConnectionTest {

    @Test
    public void testAccessListOfLocalFilesByAbsolutePath() throws SourceException {
        accessLocalFilesnDirsByPath(System.getProperty("user.home"));
    }

    @Test
    public void testAccessListOfLocalFilesByRelativePath() throws SourceException {
        accessLocalFilesnDirsByPath(".");
    }

    @Test(expected = SourceException.class)
    public void testAccessWithExceptionByNonExistingPath() throws SourceException {
        accessLocalFilesnDirsByPath("sdsafsafasfasfasf");
    }


    private void accessLocalFilesnDirsByPath(String path) throws SourceException {
        try (FileRepositoryConnection connection = LocalFileConnection.open(path)) {
            connection.listFiles().forEach(
                    f -> {
                        System.out.println(f.getAbsolutePath());
                        assertThat(f).isInstanceOf(LocalFileData.class);
                        assertThat(f.getName()).isNotEmpty();
                        assertThat(Paths.get(f.getAbsolutePath()).isAbsolute()).isTrue();
                    }
            );
        }
    }
}
