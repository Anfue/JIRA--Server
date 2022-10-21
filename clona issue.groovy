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

CustomFieldManager customFieldManager 	= ComponentAccessor.getCustomFieldManager();
Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("AJ-98");

Long origen = 12666L;	
Long destino = 18402L;	
CustomField origen_cf= customFieldManager.getCustomFieldObject(origen);
CustomField destino_cf= customFieldManager.getCustomFieldObject(destino);
def customField = customFieldManager.getCustomFieldObjects(issue).findByName('Clona Total Aprobado')
def value_origen =issue.getCustomFieldValue(origen_cf)


log.error('origen-->'+value_origen)
//destino_cf.updateValue(null, issue, new ModifiedValue(destino_cf, value_origen),new DefaultIssueChangeHolder());
customField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(customField), value_origen.toString()), new DefaultIssueChangeHolder())
log.warn('2 destino_cf-->'+issue.getCustomFieldValue(customField))