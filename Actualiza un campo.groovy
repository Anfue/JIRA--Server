import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("PPR-32720")

def cf_UP = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12712L)
def cf_UIT = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(11703L)
def val_UP = issue.getCustomFieldValue(cf_UP)
log.error('Val-->'+val_UP)
//cf_UIT.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_UIT), ),new DefaultIssueChangeHolder());

return issue.getCustomFieldValue(cf_UIT)