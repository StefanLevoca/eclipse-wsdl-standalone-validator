This is an abandoned attempt to run Eclipse WSI validator as a standalone project.

Due to architectural setup of Eclipse plugin, it is very difficult to run the project outside of Eclipse Plugin framework

Lots of code is based on the org.eclipse.wst.wsi.internal.WSITestToolsPlugin.getPlugin detection, hardwired all over the place.