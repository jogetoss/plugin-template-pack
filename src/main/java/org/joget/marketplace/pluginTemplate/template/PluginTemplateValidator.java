package org.joget.marketplace.pluginTemplate.template;

import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormValidator;
import org.joget.apps.form.model.Validator;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.service.PropertyUtil;
import org.json.JSONObject;

public class PluginTemplateValidator extends FormValidator {
    public final static String MESSAGE_PATH = "messages/PluginTemplate";
    
    public String getName() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateValidator.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getVersion() {
        return "7.0.0";
    }

    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateValidator.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateValidator.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getClassName() {
        return getClass().getName();
    }
    
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/template/pluginTemplateValidator.json", null, true, MESSAGE_PATH);
    }
    
    
    public boolean validate(Element element, FormData data, String[] values) {
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

            Validator v = (Validator)pluginManager.getPlugin(className);
            v.setProperties(PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON));

            return v.validate(element, data, values);
        }catch(Exception ex){
            LogUtil.error(PluginTemplateDatalistFormatter.class.getName(), ex, "Plugin Template - Datalist Formatter Error");
        }
        return false;
    }
    
    public String getElementDecoration() {
        String decoration = "";
        String type = (String) getProperty("type");
        String mandatory = (String) getProperty("mandatory");
        if ("true".equals(mandatory)) {
            decoration += " * ";
        }
        if (decoration.trim().length() > 0) {
            decoration = decoration.trim();
        }
        return decoration;
    }
}
