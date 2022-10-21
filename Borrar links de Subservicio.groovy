import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.Issue

def issue = ComponentAccessor.issueManager.getIssueObject("PS-280")

def Subservicio = 15422
def cractivo = 14503

def cfsubservicio = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(Subservicio)
def cfcractivo = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(cractivo)

Issue sv = (Issue) issue.getCustomFieldValue(cfsubservicio)
cfcractivo.updateValue(null,sv,new ModifiedValue(issue.getCustomFieldValue(cfcractivo),null),new DefaultIssueChangeHolder())
