package org.joget.marketplace.pluginTemplate;

import javax.servlet.http.HttpServletRequest;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionDefault;
import org.joget.apps.datalist.model.DataListActionResult;
import org.joget.workflow.util.WorkflowUtil;

public class PluginTemplateManagementDeleteAction extends DataListActionDefault {

    public String getName() {
        return "Plugin Template Management Delete Action";
    }

    public String getVersion() {
        return "7.0.2";
    }

    public String getDescription() {
        return "";
    }

    public String getLinkLabel() {
        return getPropertyString("label"); //get label from configured properties options
    }

    public String getHref() {
        return getPropertyString("href"); //Let system to handle to post to the same page
    }

    public String getTarget() {
        return "post";
    }

    public String getHrefParam() {
        return getPropertyString("hrefParam");  //Let system to set the parameter to the checkbox name
    }

    public String getHrefColumn() {
        return getPropertyString("hrefColumn"); //Let system to set the primary key column of the binder
    }

    public String getConfirmation() {
        return getPropertyString("confirmation"); //get confirmation from configured properties options
    }

    public DataListActionResult executeAction(DataList dataList, String[] rowKeys) {
        DataListActionResult result = new DataListActionResult();
        result.setType(DataListActionResult.TYPE_REDIRECT);
        result.setUrl("REFERER");
        
        // only allow POST
        HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
        if (request != null && !"POST".equalsIgnoreCase(request.getMethod())) {
            return result;
        }
        
        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        
        // check for submited rows
        if (rowKeys != null && rowKeys.length > 0) {
            for (String r : rowKeys) {
                if(r.startsWith("pt-")){
                    environmentVariableDao.delete(r, appDef);
                }
            }
        }
        
        return result;
    }

    public String getLabel() {
        return "Plugin Template Management Delete Action";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }
}
