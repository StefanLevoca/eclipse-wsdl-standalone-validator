package com.github.novotnyr.wsdl.validator;

import org.eclipse.wst.wsdl.validation.internal.ClassloaderWSDLValidatorDelegate;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EclipseWsitWsdlValidator {

    private WSDLValidatorImpl wsdlProfileValidator;
    private MessageValidatorImpl messageProfileValidator;
    private EnvelopeValidatorImpl envelopeProfileValidator;
    private UDDIValidatorImpl uddiProfileValidator;
    private WSDLValidator wsdlValidator;

    private void initializeValidators() {
        this.uddiProfileValidator = new UDDIValidatorImpl();
        this.wsdlProfileValidator = new WSDLValidatorImpl();
        this.messageProfileValidator = new MessageValidatorImpl();
        this.envelopeProfileValidator = new EnvelopeValidatorImpl();
    }

    private void registerArtifact(String artifactName, BaseValidator validator) {
        ArtifactType.registerArtifactType(artifactName);
        ProfileValidatorFactoryImpl.addToValidatatorRegistry(artifactName, validator);
    }

    private void registerTadProfiles() {
        Utils.registerValidProfileTADVersion("Basic Profile Test Assertions", "1.1.0");
        Utils.registerValidProfileTADVersion("Basic Profile 1.1 Test Assertions", "1.1.0");
        Utils.registerValidProfileTADVersion("Simple Soap Binding Profile [1.0] (with Basic Profile [1.1]) Test Assertions", "1.0.0");
        Utils.registerValidProfileTADVersion("Attachments Profile [1.0] (with Basic Profile [1.1] and Simple Soap Binding Profile [1.0]) Test Assertions", "1.0.0");
    }

    public void initialize() {
        WSITestToolsPlugin wsiTestToolsPlugin = new WSITestToolsPlugin();
        WSITestToolsProperties.setEclipseContext(false);

        initializeValidators();

        configureValidators(wsiTestToolsPlugin);
        configureReportArtifactTypes(wsiTestToolsPlugin);

        registerArtifact("description", this.wsdlProfileValidator);
        registerArtifact("discovery", this.uddiProfileValidator);
        registerArtifact("message", this.messageProfileValidator);
        registerArtifact("envelope", this.envelopeProfileValidator);

        registerTadProfiles();
        configureEntryTypes();

        this.wsdlValidator = new WSDLValidator();
        registerWsiValidator(this.wsdlValidator);
    }

    private void configureValidators(WSITestToolsPlugin wsiTestToolsPlugin) throws ValidatorConfigurationException {
        try {
            Field validatorsField = WSITestToolsPlugin.class.getDeclaredField("validators");
            validatorsField.setAccessible(true);
            validatorsField.set(wsiTestToolsPlugin, getBaseValidators());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ValidatorConfigurationException("Cannot assign validators via reflection", e);
        }
    }

    private void configureReportArtifactTypes(WSITestToolsPlugin wsiTestToolsPlugin) {
        try {
            Field reportArtifactTypesField = WSITestToolsPlugin.class.getDeclaredField("reportArtifactTypes");
            reportArtifactTypesField.setAccessible(true);
            // see org.eclipse.wst.wsi_1.0.650.v201904232049.jar!/plugin.xml:81
            reportArtifactTypesField.set(wsiTestToolsPlugin, new String[]{"message"});
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ValidatorConfigurationException("Cannot configure report artifact types via reflection", e);
        }
    }

    private void configureEntryTypes() {
        for (BaseValidator validator : getBaseValidators()) {
            for (String entryType : validator.getEntryTypes()) {
                EntryType.registerEntryType(ArtifactType.getArtifactType(validator.getArtifactType()), entryType);
            }
        }
    }

    private BaseValidator[] getBaseValidators() {
        List<BaseValidator> validators = new ArrayList<>();
        validators.add(this.wsdlProfileValidator);
        validators.add(this.messageProfileValidator);
        validators.add(this.envelopeProfileValidator);
        validators.add(this.uddiProfileValidator);

        return validators.toArray(new BaseValidator[0]);
    }

    public IValidationReport validate(File wsdlFile) {
        return this.wsdlValidator.validate(wsdlFile.toURI().toString());
    }

    protected void registerWsiValidator(WSDLValidator validator) {
        WSDLValidatorDelegate wsiDelegate = new ClassloaderWSDLValidatorDelegate("org.eclipse.wst.wsi.internal.validate.wsdl.WSDLValidator");
        validator.registerWSDLExtensionValidator("http://schemas.xmlsoap.org/wsdl/", wsiDelegate);
    }
}
