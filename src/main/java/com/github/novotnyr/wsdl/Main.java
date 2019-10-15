package com.github.novotnyr.wsdl;

import com.github.novotnyr.wsdl.validator.EclipseWsitWsdlValidator;
import org.eclipse.wst.wsdl.validation.internal.ClassloaderWSDLValidatorDelegate;
import org.eclipse.wst.wsdl.validation.internal.IValidationMessage;
import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidator;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidatorDelegate;

import java.io.File;
import java.net.MalformedURLException;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        if (args.length == 0) {
            System.err.println("Missing file argument");
            System.exit(2);
        }
        File wsdlFile = new File(args[0]);
        if (!wsdlFile.exists()) {
            System.err.println("File " + wsdlFile + " does not exist");
            System.exit(2);
        }


        WSDLValidator validator = new WSDLValidator();

        WSDLValidatorDelegate wsiDelegate = new ClassloaderWSDLValidatorDelegate("org.eclipse.wst.wsi.internal.validate.wsdl.WSDLValidator");
        validator.registerWSDLExtensionValidator("http://schemas.xmlsoap.org/wsdl/", wsiDelegate);


        EclipseWsitWsdlValidator eclipseWsitWsdlValidator = new EclipseWsitWsdlValidator();
        eclipseWsitWsdlValidator.configureWsitPlugin();


        IValidationReport report = validator.validate(wsdlFile.toURI().toURL().toString(), null, null);

        for (IValidationMessage validationMessage : report.getValidationMessages()) {
            System.out.println(validationMessage.getLine() + ":" + validationMessage.getColumn() + "\t" + validationMessage.getMessage());
        }
    }
}
