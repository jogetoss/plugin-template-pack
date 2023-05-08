package org.joget.marketplace.pluginTemplate.template;

import java.util.Map;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.ApplicationPlugin;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.plugin.property.service.PropertyUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.json.JSONObject;

public class PluginTemplateProcessTool extends DefaultApplicationPlugin{
    public final static String MESSAGE_PATH = "messages/PluginTemplate";
    
    public String getName() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateProcessTool.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getVersion() {
        return "7.0.2";
    }

    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateProcessTool.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateProcessTool.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/template/pluginTemplateProcessTool.json", null, true, MESSAGE_PATH);
    }

    @Override
    public Object execute(Map properties) {
        try{
            String templateId = (String)properties.get("templateId");

            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            EnvironmentVariable ev = environmentVariableDao.loadById(templateId, appDef);

            JSONObject templateObject = new JSONObject(ev.getRemarks());

            String className = (String)templateObject.get("pluginClass");
            String pluginPropertiesJSON = ev.getValue();

            //any #hash# that failed to parse due to lack of assignment/record ID context will also be parsed for one more time here
            if(properties.containsKey("workflowAssignment")){
                WorkflowAssignment wfAssignment = (WorkflowAssignment) properties.get("workflowAssignment");
                pluginPropertiesJSON = AppUtil.processHashVariable(pluginPropertiesJSON, wfAssignment, null, null);
            }else{
                pluginPropertiesJSON = AppUtil.processHashVariable(pluginPropertiesJSON, null, null, null);
            }

            ApplicationPlugin appPlugin = (ApplicationPlugin)pluginManager.getPlugin(className);
            Map pluginProperties = PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON);

            if (appPlugin instanceof PropertyEditable) {
                ((PropertyEditable) appPlugin).setProperties(pluginProperties);
            }
            
            return appPlugin.execute(pluginProperties);
        }catch(Exception ex){
            LogUtil.error(PluginTemplateDatalistFormatter.class.getName(), ex, "Plugin Template - Process Tool Error");
        }
        return null;
    }
}
