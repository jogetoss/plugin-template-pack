package org.joget.marketplace.pluginTemplate;

import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormBinder;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormLoadBinder;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormStoreBinder;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.property.service.PropertyUtil;
import org.json.JSONObject;

public class PluginTemplateManagementFormBinder extends FormBinder implements FormLoadBinder, FormStoreBinder {

    @Override
    public String getName() {
        return "Plugin Template Management Form Binder";
    }

    @Override
    public String getVersion() {
        return "7.0.1";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getLabel() {
        return "Plugin Template Management Form Binder";
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public FormRowSet load(Element element, String primaryKey, FormData formData) {
        FormRowSet results = new FormRowSet();
        if (primaryKey != null && primaryKey.trim().length() > 0) {
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            
            if(!primaryKey.startsWith("pt-")){
                primaryKey = "pt-" + primaryKey;
            }
            
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            EnvironmentVariable ev = environmentVariableDao.loadById(primaryKey, appDef);
            
            try{
                if (ev != null) {
                    FormRow row = new FormRow();
                    JSONObject templateObject = new JSONObject(ev.getRemarks());
                    
                    row.setId(ev.getId());
                    row.setProperty("pluginClass", (String)templateObject.get("pluginClass"));
                    row.setProperty("description", (String)templateObject.get("description"));
                    row.setProperty("name", (String)templateObject.get("name"));
                    row.setProperty("pluginProperties", PropertyUtil.propertiesJsonLoadProcessing(ev.getValue()));
                    results.add(row);
                }
            }catch(Exception ex){
                LogUtil.error(PluginTemplateManagementFormBinder.class.getName(), ex, "Template record cannot load");
            }
        }
        return results;
    }

    @Override
    public FormRowSet store(Element element, FormRowSet rows, FormData formData) {
        if (rows != null && !rows.isEmpty()) {
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            FormRow row = rows.get(0);
            EnvironmentVariable ev = new EnvironmentVariable();
            
            try{
                if (row.getProperty("pluginProperties") == null || row.getProperty("pluginProperties").isEmpty()) {
                    formData.addFormError("pluginClass", AppPluginUtil.getMessage("org.joget.marketplace.pluginTemplateManagement.pleaseConfigure", PluginTemplateManagementMenu.class.getName(), PluginTemplateManagementMenu.MESSAGE_PATH));
                    return rows;
                }

                if(!row.getId().startsWith("pt-")){
                    row.setId("pt-" + row.getId());
                }

                JSONObject templateObject = new JSONObject();
                templateObject.put("pluginClass", (String)row.getProperty("pluginClass"));
                templateObject.put("name", (String)row.getProperty("name"));
                templateObject.put("description", (String)row.getProperty("description"));

                ev.setId(row.getId());
                ev.setAppDefinition(appDef);
                ev.setRemarks(templateObject.toString());
                ev.setValue(PropertyUtil.propertiesJsonStoreProcessing(ev.getValue(), row.getProperty("pluginProperties")));

                if(environmentVariableDao.loadById(row.getId(), appDef) != null){
                    environmentVariableDao.update(ev);
                }else{
                    environmentVariableDao.add(ev);
                }
            }catch(Exception ex){
                LogUtil.error(PluginTemplateManagementFormBinder.class.getName(), ex, "Template record cannot store");
            }
        }
        return rows;
    }
}