This project runs Eclipse WSI validator as a standalone project.

Usage
=====

* `com.github.novotnyr.wsdl.validator.Cli` is a class with `main()` method that accepts a single argument with WSDL filename. The file will be validated (regular and WS-I compliance).
* `com.github.novotnyr.wsdl.validator.EclipseWsitWsdlValidator` is a facade that hides WSDL/WS-I complexities behind a single method. See unit tests for usage.

Challenges
==========
Due to architectural setup of Eclipse plugin, it is very difficult to run the project outside of Eclipse Plugin framework.

Lots of code is based on the org.eclipse.wst.wsi.internal.WSITestToolsPlugin.getPlugin detection, hardwired all over the place.