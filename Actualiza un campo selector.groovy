import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.ImportUtils
import com.atlassian.plugin.PluginAccessor
import java.text.DecimalFormat
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.plugin.PluginAccessor
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.opensymphony.workflow.InvalidInputException
import groovy.sql.Sql
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.customfields.option.Option
import com.atlassian.jira.issue.customfields.manager.OptionsManager


/************Acceso issue CONSOLA*******************/
Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("PJ-6301") 
/************Acceso issue CONSOLA*******************/
def customFieldManager = ComponentAccessor.getCustomFieldManager()
OptionsManager optManager = ComponentAccessor.getOptionsManager();
def TipoAjuste = 18216
def CondAjuste = 18323
def value1 = "Ajuste"
def value2 = "Ajuste con Aprobación"

def cf_TipoAjuste = customFieldManager.getCustomFieldObject(TipoAjuste)
def cf_CondAjuste= customFieldManager.getCustomFieldObject(CondAjuste)
//String sourceCfValue = customFieldManager.getCustomFieldObject(cf_CondAjuste).getValue(issue).toString();

def val_TipoAjuste = issue.getCustomFieldValue(cf_TipoAjuste)
def val_CondAjuste = issue.getCustomFieldValue(cf_CondAjuste).toString()

log.warn('val_CondAjuste-->'+val_CondAjuste)

if (val_CondAjuste == 'Liberación de presupuesto'  || val_CondAjuste == 'Traspaso de presupuesto aprobado entre años, no incluye OPEX' 
    || val_CondAjuste == 'Reasignar presupuesto entre PEP de la misma clase (no incluye OPEX)'){
    Options options = optManager.getOptions(cf_TipoAjuste.getRelevantConfig(issue));
	Option newOption = options.getOptionForValue(value1,null);
	issue.setCustomFieldValue(cf_TipoAjuste, newOption); 
    issue.store()
    return true
}
if (val_CondAjuste == 'Traspaso de presupuesto aprobado entre años, incluye PEP de clase OPEX' || val_CondAjuste == 'Reasignar presupuesto entre PEP de clase OPEX' 
    || val_CondAjuste == 'Reasignar presupuesto entre PEP de dferente clase (incluye abrir nuevos PEP)'){
    Options options = optManager.getOptions(cf_TipoAjuste.getRelevantConfig(issue));
	Option newOption = options.getOptionForValue(value2,null);
	issue.setCustomFieldValue(cf_TipoAjuste, newOption); 
    issue.store()
    return newOption
}


































