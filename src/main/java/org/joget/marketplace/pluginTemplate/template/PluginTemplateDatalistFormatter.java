package org.joget.marketplace.pluginTemplate.template;

import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormat;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.service.PropertyUtil;
import org.json.JSONObject;

public class PluginTemplateDatalistFormatter extends DataListColumnFormatDefault {
    public final static String MESSAGE_PATH = "messages/PluginTemplate";
    
    public String getName() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateDatalistFormatter.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getVersion() {
        return "7.0.1";
    }

    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateDatalistFormatter.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateDatalistFormatter.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/template/pluginTemplateDatalistFormatter.json", null, true, MESSAGE_PATH);
    }

    public String format(DataList dataList, DataListColumn column, Object row, Object value) {
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

            DataListColumnFormat p = (DataListColumnFormat)pluginManager.getPlugin(className);
            p.setProperties(PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON));

            return p.format(dataList, column, row, value);
        }catch(Exception ex){
            LogUtil.error(PluginTemplateDatalistFormatter.class.getName(), ex, "Plugin Template - Datalist Formatter Error");
        }
        return "";
    }
}
