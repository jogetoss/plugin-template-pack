package org.joget.marketplace.pluginTemplate.template;

import java.util.Map;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.lib.FormOptionsBinder;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SecurityUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.plugin.property.service.PropertyUtil;
import org.json.JSONObject;

public class PluginTemplateOptionsBinder extends FormBinder implements FormLoadOptionsBinder, FormAjaxOptionsBinder {

    public final static String MESSAGE_PATH = "messages/PluginTemplate";

    @Override
    public String getName() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateOptionsBinder.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getVersion() {
        return "7.0.0";
    }

    @Override
    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateOptionsBinder.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateOptionsBinder.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() { // for the configure form to appear
        
        String formDefField = null;
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        if (appDef != null) {
            String formJsonUrl = "[CONTEXT_PATH]/web/json/console/app/" + appDef.getId() + "/" + appDef.getVersion() + "/forms/options";
            formDefField = "{name:'formDefId',label:'@@form.defaultformoptionbinder.formId@@',type:'selectbox',options_ajax:'" + formJsonUrl + "',required : 'True'}";
        } else {
            formDefField = "{name:'formDefId',label:'@@form.defaultformoptionbinder.formId@@',type:'textfield',required : 'True'}";
        }
        
        String useAjax = "";
        if (SecurityUtil.getDataEncryption() != null && SecurityUtil.getNonceGenerator() != null) {
            useAjax = ",{name:'useAjax',label:'@@form.defaultformoptionbinder.useAjax@@',type:'checkbox',value :'false',options :[{value :'true',label :''}]}";
        }
        
        Object[] arguments = new Object[]{formDefField,useAjax};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/template/pluginTemplateOptionsBinder.json", arguments, true, MESSAGE_PATH);
        return json;
        
    }

    @Override
    public boolean useAjax() {
        
        try {
          
            String templateId = getPropertyString("templateId");

            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            EnvironmentVariable ev = environmentVariableDao.loadById(templateId, appDef);

            JSONObject templateObject = new JSONObject(ev.getRemarks());

            String className = (String) templateObject.get("pluginClass");
            String pluginPropertiesJSON = ev.getValue();
            pluginPropertiesJSON = AppUtil.processHashVariable(pluginPropertiesJSON, null, null, null);
            Map pluginProperties = PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON);
        
            return "true".equalsIgnoreCase((String)pluginProperties.get("useAjax"));
            
        } catch (Exception ex) {
            LogUtil.error(PluginTemplateOptionsBinder.class.getName(), ex, "Plugin Template - Options Binder Error");
        }
        
        return false;
        
    }

    @Override
    public FormRowSet load(Element element, String s, FormData formData) { // s is primary key

        FormRowSet results = new FormRowSet();
        results.setMultiRow(true);
        //Using filtered formset to ensure the returned result is clean with no unnecessary nulls
        FormRowSet filtered = new FormRowSet();
        filtered.setMultiRow(true);
        
        try {
          
            String templateId = getPropertyString("templateId");

            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            EnvironmentVariable ev = environmentVariableDao.loadById(templateId, appDef);

            JSONObject templateObject = new JSONObject(ev.getRemarks());

            String className = (String) templateObject.get("pluginClass");
            String pluginPropertiesJSON = ev.getValue();
            pluginPropertiesJSON = AppUtil.processHashVariable(pluginPropertiesJSON, null, null, null);
            Map pluginProperties = PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON);
            
            FormLoadOptionsBinder p = (FormLoadOptionsBinder) pluginManager.getPlugin(className);
            if (p instanceof PropertyEditable) {
                ((PropertyEditable) p).setProperties(pluginProperties);
            }
                
            return p.load(element, s, formData);
            
        } catch (Exception ex) {
            LogUtil.error(PluginTemplateOptionsBinder.class.getName(), ex, "Plugin Template - Options Binder Error");
        }
        
        return null;
        
    }

    @Override
    public FormRowSet loadAjaxOptions(String[] dependencyValues) {
        
        FormRowSet results = new FormRowSet();
        results.setMultiRow(true);
        //Using filtered formset to ensure the returned result is clean with no unnecessary nulls
        FormRowSet filtered = new FormRowSet();
        filtered.setMultiRow(true);
        
        try {
            
            String templateId = getPropertyString("templateId");

            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            EnvironmentVariable ev = environmentVariableDao.loadById(templateId, appDef);

            JSONObject templateObject = new JSONObject(ev.getRemarks());

            String className = (String) templateObject.get("pluginClass");
            String pluginPropertiesJSON = ev.getValue();
            pluginPropertiesJSON = AppUtil.processHashVariable(pluginPropertiesJSON, null, null, null);
            Map pluginProperties = PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON);
            
            FormLoadOptionsBinder p = (FormLoadOptionsBinder) pluginManager.getPlugin(className);
            if (p instanceof PropertyEditable) {
                ((PropertyEditable) p).setProperties(pluginProperties);
            }
            
            FormOptionsBinder q = (FormOptionsBinder) pluginManager.getPlugin(className);
            q.setProperties(PropertyUtil.getPropertiesValueFromJson(pluginPropertiesJSON));
            
            return q.loadAjaxOptions(dependencyValues);
            
        } catch (Exception ex) {
            LogUtil.error(PluginTemplateOptionsBinder.class.getName(), ex, "Plugin Template - Options Binder Error");
        }
        
        return null;
        
    }

}
