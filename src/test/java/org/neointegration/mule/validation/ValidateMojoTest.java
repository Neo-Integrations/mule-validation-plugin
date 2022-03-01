package org.neointegration.mule.validation;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import java.io.File;

public class ValidateMojoTest extends AbstractMojoTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        File pom = getTestFile( getBasedir(),"src/test/resources/test-pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );
        ValidationMojo validateMojo = (ValidationMojo) lookupMojo( "validate", pom );
        assertNotNull(validateMojo);
        validateMojo.execute();
    }
}
