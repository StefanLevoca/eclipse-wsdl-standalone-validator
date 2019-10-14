package com.github.novotnyr.wsdl;

import org.eclipse.wst.wsdl.validation.internal.ClassloaderWSDLValidatorDelegate;
import org.eclipse.wst.wsdl.validation.internal.IValidationMessage;
import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidator;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidatorDelegate;
import org.eclipse.wst.wsi.internal.WSITestToolsPlugin;
import org.eclipse.wst.wsi.internal.WSITestToolsProperties;
import org.eclipse.wst.wsi.internal.core.profile.validator.BaseValidator;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.ProfileValidatorFactoryImpl;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.envelope.EnvelopeValidatorImpl;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.message.MessageValidatorImpl;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.uddi.UDDIValidatorImpl;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.wsdl.WSDLValidatorImpl;
import org.eclipse.wst.wsi.internal.core.util.ArtifactType;
import org.eclipse.wst.wsi.internal.core.util.EntryType;
import org.eclipse.wst.wsi.internal.core.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Missing file argument");
            System.exit(2);
        }
        File wsdlFile = new File(args[0]);
        if (!wsdlFile.exists()) {
            System.err.println("File " + wsdlFile + " does not exist");
            System.exit(2);
        }


        try(InputStream wsdlInputStream = new FileInputStream(wsdlFile)) {
            WSDLValidator validator = new WSDLValidator();
            //validator.addURIResolver(new URIResolverWrapper());

            WSDLValidatorDelegate wsiDelegate = new ClassloaderWSDLValidatorDelegate("org.eclipse.wst.wsi.internal.validate.wsdl.WSDLValidator");
            validator.registerWSDLExtensionValidator("http://schemas.xmlsoap.org/wsdl/", wsiDelegate);

            // initialize static singletons
            String name = "Attachments Profile [1.0] (with Basic Profile [1.1] and Simple Soap Binding Profile [1.0]) Test Assertions";
            String version = "1.0.0.2";

            Utils.registerValidProfileTADVersion(name, version);

            WSITestToolsPlugin wsiTestToolsPlugin = new WSITestToolsPlugin();
            WSITestToolsProperties.setEclipseContext(false);

            ArtifactType.registerArtifactType("discovery");
            ArtifactType.registerArtifactType("description");
            ArtifactType.registerArtifactType("message");
            ArtifactType.registerArtifactType("envelope");

            WSDLValidatorImpl wsdlValidator = new WSDLValidatorImpl();
            MessageValidatorImpl messageValidator = new MessageValidatorImpl();
            EnvelopeValidatorImpl envelopeValidator = new EnvelopeValidatorImpl();
            UDDIValidatorImpl uddiValidator = new UDDIValidatorImpl();


            ProfileValidatorFactoryImpl.addToValidatatorRegistry("discovery", uddiValidator);
            ProfileValidatorFactoryImpl.addToValidatatorRegistry("description", wsdlValidator);
            ProfileValidatorFactoryImpl.addToValidatatorRegistry("message", messageValidator);
            ProfileValidatorFactoryImpl.addToValidatatorRegistry("envelope", envelopeValidator);

            List<BaseValidator> validators = new ArrayList<>();
            validators.add(wsdlValidator);
            validators.add(messageValidator);
            validators.add(envelopeValidator);
            validators.add(uddiValidator);



            try {
                Field validatorsField = WSITestToolsPlugin.class.getDeclaredField("validators");
                validatorsField.setAccessible(true);
                validatorsField.set(wsiTestToolsPlugin, validators.toArray(new BaseValidator[0]));

                Field reportArtifactTypesField = WSITestToolsPlugin.class.getDeclaredField("reportArtifactTypes");
                reportArtifactTypesField.setAccessible(true);
                // see org.eclipse.wst.wsi_1.0.650.v201904232049.jar!/plugin.xml:81
                reportArtifactTypesField.set(wsiTestToolsPlugin, new String[] { "message" });
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //ArtifactType.registerArtifactType("NULL");
            //EntryType.registerEntryType(ArtifactType.getArtifactType("NULL"), "NULL");

            for (BaseValidator v : validators) {
                for (String entryType : v.getEntryTypes()) {
                    EntryType.registerEntryType(ArtifactType.getArtifactType(v.getArtifactType()), entryType);
                }
            }

            IValidationReport report = validator.validate(wsdlFile.toURI().toURL().toString(), null, null);

            for (IValidationMessage validationMessage : report.getValidationMessages()) {
                System.out.println(validationMessage.getLine() + ":" + validationMessage.getColumn() + "\t" + validationMessage.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
