package com.github.novotnyr.wsdl.validator;

import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class EclipseWsitWsdlValidatorTest {

    private EclipseWsitWsdlValidator validator;

    @Before
    public void setUp() throws Exception {
        this.validator = new EclipseWsitWsdlValidator();
        this.validator.initialize();
    }

    @Test
    public void testValidWsdl() {
        IValidationReport report = this.validator.validate(new File("src/test/resources/translator.wsdl"));
        assertTrue(report.isWSDLValid());
    }

    @Test
    public void testBP2012Violation() {
        IValidationReport report = this.validator.validate(new File("src/test/resources/translator-part-refers-to-type.wsdl"));
        assertTrue(report.isWSDLValid());
        assertTrue(report.getValidationMessages().length > 0);
        assertTrue(report.getValidationMessages()[0].getMessage().contains("BP2012"));
    }
}