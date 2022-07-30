package org.joget.marketplace;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.joget.marketplace.pluginTemplate.PluginTemplateManagementMenu;
import org.joget.marketplace.pluginTemplate.template.*;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();
        //Register plugin here
        registrationList.add(context.registerService(PluginTemplateManagementMenu.class.getName(), new PluginTemplateManagementMenu(), null));
        registrationList.add(context.registerService(PluginTemplateDatalistFormatter.class.getName(), new PluginTemplateDatalistFormatter(), null));
        registrationList.add(context.registerService(PluginTemplateProcessTool.class.getName(), new PluginTemplateProcessTool(), null));
        registrationList.add(context.registerService(PluginTemplatePermission.class.getName(), new PluginTemplatePermission(), null));
        registrationList.add(context.registerService(PluginTemplateOptionsBinder.class.getName(), new PluginTemplateOptionsBinder(), null));
        registrationList.add(context.registerService(PluginTemplateValidator.class.getName(), new PluginTemplateValidator(), null));
        
    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}