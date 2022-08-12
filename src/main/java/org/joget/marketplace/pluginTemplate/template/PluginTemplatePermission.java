package org.joget.marketplace.pluginTemplate.template;

import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DatalistPermission;
import org.joget.apps.form.model.FormPermission;
import org.joget.apps.userview.model.UserviewPermission;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.service.PropertyUtil;
import org.json.JSONObject;

public class PluginTemplatePermission extends UserviewPermission implements FormPermission, DatalistPermission {
    public final static String MESSAGE_PATH = "messages/PluginTemplate";
    
    public String getName() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplatePermission.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getVersion() {
        return "7.0.1";
    }

    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplatePermission.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplatePermission.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public boolean isAuthorize() {
        try{
            String templateId = getPropertyString("templateId");

            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            EnvironmentVariable ev = environmentVariableDao.loadById(templateId, appDef);

            JSONObject templateObject = new JSONObject(ev.getRemarks());

            String className = (String)templateObject.get("pluginClass");
            String pluginPropertiesJSON = ev.getValue();
            pluginPropertiesJSON = AppUtil.processHashVariable(pluginPropertiesJSON, null, null, null);

            UserviewPermission p = (UserviewPermission)pluginManager.getPlugin(className);
            p.setProperties(PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON));
            p.setCurrentUser(getCurrentUser());
            p.setRequestParameters(getRequestParameters());

            return p.isAuthorize();
        }catch(Exception ex){
            LogUtil.error(PluginTemplateDatalistFormatter.class.getName(), ex, "Plugin Template - Permission Error");
        }
        return false;
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/template/pluginTemplatePermission.json", null, true, MESSAGE_PATH);
    }
    
}
